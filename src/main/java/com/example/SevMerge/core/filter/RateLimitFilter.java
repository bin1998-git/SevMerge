package com.example.SevMerge.core.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Per-IP rate limiting filter.
 *
 * Rules:
 *   POST /login            – 5 requests per 60 s ; lockout 5 min on breach
 *   POST /api/email/send   – 3 requests per 60 s ; lockout 5 min on breach
 *   POST /api/sms/send     – 3 requests per 60 s ; lockout 5 min on breach
 *
 * Returns HTTP 429 with a plain-text body when the limit is exceeded.
 */
@Component
public class RateLimitFilter implements Filter {

    // ── tunables ──────────────────────────────────────────────────────────────
    private static final int  LOGIN_MAX_REQUESTS      = 5;
    private static final long LOGIN_WINDOW_MS         = 60_000L;
    private static final long LOGIN_LOCKOUT_MS        = 5 * 60_000L;

    private static final int  OTP_MAX_REQUESTS        = 3;
    private static final long OTP_WINDOW_MS           = 60_000L;
    private static final long OTP_LOCKOUT_MS          = 5 * 60_000L;

    // ── per-IP bucket state ───────────────────────────────────────────────────
    private static class Bucket {
        final AtomicInteger count = new AtomicInteger(0);
        volatile long windowStart  = System.currentTimeMillis();
        volatile long lockedUntil  = 0L;
    }

    // key: "path|ip"
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // ── Filter entry point ────────────────────────────────────────────────────
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String method = request.getMethod();
        String path   = request.getServletPath();

        // Only rate-limit specific POST endpoints
        if ("POST".equalsIgnoreCase(method)) {
            if ("/login".equals(path)) {
                if (isRateLimited(request, response, "/login",
                        LOGIN_MAX_REQUESTS, LOGIN_WINDOW_MS, LOGIN_LOCKOUT_MS)) {
                    return;
                }
            } else if ("/api/email/send".equals(path)) {
                if (isRateLimited(request, response, "/api/email/send",
                        OTP_MAX_REQUESTS, OTP_WINDOW_MS, OTP_LOCKOUT_MS)) {
                    return;
                }
            } else if ("/api/sms/send".equals(path)) {
                if (isRateLimited(request, response, "/api/sms/send",
                        OTP_MAX_REQUESTS, OTP_WINDOW_MS, OTP_LOCKOUT_MS)) {
                    return;
                }
            }
        }

        chain.doFilter(req, res);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private boolean isRateLimited(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String pathKey,
                                   int maxRequests,
                                   long windowMs,
                                   long lockoutMs) throws IOException {

        String ip     = getClientIp(request);
        String key    = pathKey + "|" + ip;
        long   now    = System.currentTimeMillis();
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket());

        synchronized (bucket) {
            // Locked-out?
            if (now < bucket.lockedUntil) {
                sendTooManyRequests(response,
                        "너무 많은 요청입니다. 잠시 후 다시 시도해 주세요.");
                return true;
            }

            // Window expired → reset
            if (now - bucket.windowStart > windowMs) {
                bucket.windowStart = now;
                bucket.count.set(0);
            }

            int current = bucket.count.incrementAndGet();
            if (current > maxRequests) {
                bucket.lockedUntil = now + lockoutMs;
                sendTooManyRequests(response,
                        "요청 한도를 초과했습니다. " + (lockoutMs / 60_000) + "분 후에 다시 시도하세요.");
                return true;
            }
        }

        return false;
    }

    private void sendTooManyRequests(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(429);
        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().write(message);
    }

    /** Respect X-Forwarded-For (reverse-proxy deployments). */
    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
