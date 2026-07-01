package com.example.SevMerge.core.interceptor;

import com.example.SevMerge.charge.ChargeService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.SessionUser;
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

        SessionUser sessionUser;
        try {
            sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        } catch (IllegalStateException e) {
            // 빠른 페이지 이동 시 병렬 요청 중 하나가 세션을 invalidate한 경우
            return true;
        }
        if (sessionUser == null) return true;

        Long lastRefresh;
        try {
            lastRefresh = (Long) session.getAttribute(LAST_REFRESH_KEY);
        } catch (IllegalStateException e) {
            return true;
        }
        long now = System.currentTimeMillis();
        if (lastRefresh != null && now - lastRefresh < REFRESH_INTERVAL_MS) return true;

        Member fresh = memberRepository.findById(sessionUser.getId()).orElse(null);
        if (fresh == null || fresh.isDeleted() || fresh.getStatus() == Status.SUSPENDED) {
            try {
                session.invalidate();
            } catch (IllegalStateException ignored) {}
            response.sendRedirect("/login");
            return false;
        }
        try {
            session.setAttribute(Define.SESSION_USER, new SessionUser(fresh));
            session.setAttribute(LAST_REFRESH_KEY, now);
        } catch (IllegalStateException ignored) {
            // 세션이 다른 스레드에 의해 이미 invalidate된 경우 무시
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            if (modelAndView.getView() instanceof RedirectView) return;
            String viewName = modelAndView.getViewName();
            if (viewName != null && viewName.startsWith("redirect:")) return;

            HttpSession session = request.getSession(false);
            if (session != null) {
                SessionUser member;
                try {
                    member = (SessionUser) session.getAttribute(Define.SESSION_USER);
                } catch (IllegalStateException e) {
                    // 세션이 이미 invalidate된 경우
                    modelAndView.addObject("isLoggedIn", false);
                    modelAndView.addObject("isExpert", false);
                    modelAndView.addObject("isAdmin", false);
                    return;
                }
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
                        // notificationService.countUnRead()는 Member 엔티티를 파라미터로 받으므로
                        // 추가 DB 조회 없이 SessionUser의 ID만으로 처리하도록 위임
                        modelAndView.addObject("hasNewNotification",
                                notificationService.hasUnRead(member.getId()));
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
