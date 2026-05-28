package com.example.SevMerge.core.interceptor;

import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.UnauthorizedException;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionUser") == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Member sessionUser = (Member) session.getAttribute("sessionUser");
        if (!sessionUser.isAdmin()) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
        return true;
    }
}