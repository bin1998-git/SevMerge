package com.example.SevMerge.core.config;

import com.example.SevMerge.member.CustomOAuth2SuccessHandler;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.OAuth2MemberDetails;
import com.example.SevMerge.member.OAuth2MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2MemberService oAuth2MemberService;

    // 구글 로그인용 추가
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF: Mustache 폼 방식이므로 우선 비활성화 (필요 시 활성화)
            .csrf(csrf -> csrf.disable())

            // 모든 경로 허용 — 접근 제어는 기존 인터셉터가 담당
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )

            // 일반 로그인 (기존 MemberController /login POST 유지)
            .formLogin(form -> form.disable())

            // 구글 OAuth2 로그인 설정
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")                        // 커스텀 로그인 페이지
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2MemberService)       // 유저 정보 처리
                )
                    .successHandler(customOAuth2SuccessHandler)
                    .failureUrl("/login?error=true")
            );

        return http.build();
    }

    // PasswordEncoder Bean — WebMvcConfig에서 이쪽으로 이동
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
