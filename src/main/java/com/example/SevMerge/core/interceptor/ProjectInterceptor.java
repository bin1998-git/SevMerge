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
        // 1. 로그인 여부 검사 (기존 LoginInterceptor 스타일)
        if (session == null || session.getAttribute(Define.SESSION_USER) == null) {
            response.sendRedirect("/login-form");
            return false;
        }

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        // 2. URL에서 {id} 값을 추출하여 작성자 본인 검사 (인가)
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null && pathVariables.containsKey("id")) {
            Long projectId = Long.parseLong(pathVariables.get("id"));
            ProjectResponeDTO.DetailDTO project = projectService.findProjectById(projectId);

            // 로그인한 유저와 글 작성자 ID가 다르면 권한 없음 예외 발생
            if (!project.getMemberId().equals(sessionUser.getId())) {
                throw new ForbiddenException("해당 프로젝트에 대한 권한이 없습니다.");
            }
        }
        return true;
    }
}
