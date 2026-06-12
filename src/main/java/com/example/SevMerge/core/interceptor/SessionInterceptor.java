package com.example.SevMerge.core.interceptor;

import com.example.SevMerge.charge.ChargeService;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.notification.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class SessionInterceptor implements HandlerInterceptor {

    private final ChargeService chargeService;
    private final NotificationService notificationService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

        if (modelAndView != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object sessionUser = session.getAttribute("sessionUser");
                if (sessionUser != null) {
                    Member member = (Member) sessionUser;
                    modelAndView.addObject("isLoggedIn", true);
                    modelAndView.addObject("sessionUser", member);
                    modelAndView.addObject("isExpert", member.isExpert());
                    modelAndView.addObject("isAdmin", member.isAdmin());
                    // 헤더 잔액 — 요청마다 DB에서 최신값 조회
                    modelAndView.addObject("headerBalance", chargeService.getBalance(member.getId()));
                    modelAndView.addObject("hasNewNotification",
                            notificationService.countUnRead(member) > 0);
                } else {
                    modelAndView.addObject("isLoggedIn", false);
                    modelAndView.addObject("isExpert", false);
                    modelAndView.addObject("isAdmin", false);
                }
            }
        }
    }
}
