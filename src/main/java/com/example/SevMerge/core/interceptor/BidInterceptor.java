package com.example.SevMerge.core.interceptor;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
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
public class BidInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                   HttpSession session = request.getSession(false);

            // 1. 비로그인이면 ➡️ 로그인 폼으로 리다이렉트 후 종료
            if (session == null || session.getAttribute(Define.SESSION_USER) == null) {
                response.sendRedirect("/login");
                return false;
            }

            Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

            // 2. 전문가(Expert)가 아니면 (예: 의뢰인이 제안서 작성 주소로 강제 진입했을 때)
            if (!sessionUser.isExpert()) {
                // URL에서 projectId 파라미터를 읽어와서 원래 보던 프로젝트 상세 페이지로 튕겨버림
                String projectId = request.getParameter("projectId");
                if (projectId != null) {
                    response.sendRedirect("/projects/" + projectId);
                } else {
                    response.sendRedirect("/projects"); // 프로젝트 ID가 없으면 그냥 목록으로
                }
                return false; // 컨트롤러 진입 차단!
            }

            return true;
        }



}
