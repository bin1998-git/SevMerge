package com.example.SevMerge.core.interceptor;

import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.UnauthorizedException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.ProjectResponeDTO;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProjectInterceptor implements HandlerInterceptor {

    private final ProjectService projectService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        // 1. 비로그인이면  로그인으로 즉시 리다이렉트
        if (session == null || session.getAttribute(Define.SESSION_USER) == null) {
            response.sendRedirect("/login");
            return false;
        }

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        // 2. 의뢰인(Client)이 아니면 (예: 전문가가 프로젝트 등록/수정 주소로 강제 진입 시)
        if (!sessionUser.isClient()) {
            // 프로젝트 목록 페이지로 안전하게 튕구기
            response.sendRedirect("/projects");
            return false; // 컨트롤러 진입 차단!
        }

        return true;
    }
}
