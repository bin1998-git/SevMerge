package com.example.SevMerge.member;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 구글 로그인 역할 선택(최초 가입자)
 */

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. OAuth2MemberService에서 인증 완료 후 넘겨준 유저 상세 정보 객체 꺼내기
        OAuth2MemberDetails oAuth2User = (OAuth2MemberDetails) authentication.getPrincipal();
        Member member = oAuth2User.getMember();
        HttpSession session = request.getSession();

        // 2. ID 존재 여부(DB에 진짜 저장되어 있는지)로 기존 회원 / 신규 회원 판별
        if (member.getId() != null) {
            // 기존 회원 -> 진짜 로그인 세션을 세팅하고 메인 화면으로 이동
            session.setAttribute("sessionUser", member);
            log.info("[구글 소셜] 기존 회원 로그인 성공 - memberId={}", member.getId());
            response.sendRedirect("/");
        } else {
            // 신규 회원 -> 구글 정보를 세션에 임시 보관하고 역할 선택 화면으로 강제 이동
            // 카카오에서 사용하던 세션 키값 규격과 통일하여 뷰 화면을 그대로 재활용
            session.setAttribute("googleId", member.getProviderId());
            session.setAttribute("googleNickname", member.getName() + "_" + member.getProviderId());
            session.setAttribute("googleEmail", member.getEmail());

            log.info("[구글 소셜] 신규 회원 감지 -> 역할 선택 페이지(/kakao-role)로 이동시킴.");
            response.sendRedirect("/kakao-role");
        }
    }
}