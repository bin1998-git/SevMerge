package com.example.SevMerge.core.interceptor;

import com.example.SevMerge.member.Member;
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
                } else {
                    modelAndView.addObject("isLoggedIn", false);
                    modelAndView.addObject("isExpert", false);
                    modelAndView.addObject("isAdmin", false);
                }
            }
        }
    }
}
