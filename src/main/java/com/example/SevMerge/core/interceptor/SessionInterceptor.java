package com.example.SevMerge.core.interceptor;

import com.example.SevMerge.charge.ChargeService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.Status;
import com.example.SevMerge.notification.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Component
@RequiredArgsConstructor
public class SessionInterceptor implements HandlerInterceptor {

    private final ChargeService chargeService;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;

    // 세션 갱신 간격 (5분) — 매 요청마다 DB 조회하지 않도록 스로틀
    private static final String LAST_REFRESH_KEY = "_sessionLastRefresh";
    private static final long REFRESH_INTERVAL_MS = 5 * 60 * 1000L;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) return true;

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return true;

        Long lastRefresh = (Long) session.getAttribute(LAST_REFRESH_KEY);
        long now = System.currentTimeMillis();
        if (lastRefresh != null && now - lastRefresh < REFRESH_INTERVAL_MS) return true;

        // 5분마다 DB에서 최신 상태 재조회 — 정지/탈퇴 계정 즉시 차단
        Member fresh = memberRepository.findById(sessionUser.getId()).orElse(null);
        if (fresh == null || fresh.isDeleted() || fresh.getStatus() == Status.SUSPENDED) {
            session.invalidate();
            response.sendRedirect("/login");
            return false;
        }
        session.setAttribute(Define.SESSION_USER, fresh);
        session.setAttribute(LAST_REFRESH_KEY, now);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

        if (modelAndView != null) {
            // [L3] Skip redirect views — model attributes would become query params in the URL,
            // leaking auth state (e.g. /login?isLoggedIn=false&isExpert=false&isAdmin=false).
            if (modelAndView.getView() instanceof RedirectView) return;
            String viewName = modelAndView.getViewName();
            if (viewName != null && viewName.startsWith("redirect:")) return;

            HttpSession session = request.getSession(false);
            if (session != null) {
                Member member = (Member) session.getAttribute(Define.SESSION_USER);
                if (member != null) {
                    modelAndView.addObject("isLoggedIn", true);
                    modelAndView.addObject("sessionUser", member);
                    modelAndView.addObject("isExpert", member.isExpert());
                    modelAndView.addObject("isAdmin", member.isAdmin());
                    try {
                        modelAndView.addObject("headerBalance", chargeService.getBalance(member.getId()));
                    } catch (Exception e) {
                        modelAndView.addObject("headerBalance", 0);
                    }
                    try {
                        modelAndView.addObject("hasNewNotification",
                                notificationService.countUnRead(member) > 0);
                    } catch (Exception e) {
                        modelAndView.addObject("hasNewNotification", false);
                    }
                } else {
                    modelAndView.addObject("isLoggedIn", false);
                    modelAndView.addObject("isExpert", false);
                    modelAndView.addObject("isAdmin", false);
                }
            }
        }
    }
}
