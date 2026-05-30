package com.example.SevMerge.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

        // 뷰를 반환하는 요청일 때만 동작
        if (modelAndView != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object sessionUser = session.getAttribute("sessionUser");
                if (sessionUser != null) {
                    modelAndView.addObject("isLoggedIn", true);
                    modelAndView.addObject("sessionUser", sessionUser);
                } else {
                    modelAndView.addObject("isLoggedIn", false);
                }
            }
        }
    }
}
