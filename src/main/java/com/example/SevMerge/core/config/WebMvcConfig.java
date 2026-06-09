package com.example.SevMerge.core.config;


import com.example.SevMerge.core.interceptor.*;
import com.example.SevMerge.core.interceptor.AdminInterceptor;
import com.example.SevMerge.core.interceptor.BidInterceptor;
import com.example.SevMerge.core.interceptor.LoginInterceptor;
import com.example.SevMerge.core.interceptor.ProjectInterceptor;
import com.example.SevMerge.core.interceptor.SessionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final SessionInterceptor sessionInterceptor;
    private final ProjectInterceptor projectInterceptor;
    private final BidInterceptor bidInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 모든 요청에서 세션 정보 뷰에 주입
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**");

        // 로그인 체크
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/users/**", "/my-pages", "/my-pages/**", "/projects/save-form", "/messages/**")
                .excludePathPatterns(
                        "/",
                        "/login",
                        "/join",
                        "/logout",
                        "/projects/list",
                        "/projects/{id}/detail",
                        "/google-redirect",    // 구글 로그인 통과
                        "/kakao-redirect",     // 카카오 로그인 통과
                        "/social-role",        // 소셜 가입 페이지 통과
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico"
                );

        // 관리자 체크
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**","/board/**")
                .excludePathPatterns(
                        "/boards",
                        "/boards/save-form"
                        );

        // 프로젝트 작성자 권한 체크
        registry.addInterceptor(projectInterceptor)
                .addPathPatterns(
                        "/projects/save-form",
                        "/projects",
                        "/projects/{id}/edit", // 수정 폼
                        "/projects/{id}", // 수정 처리, 삭제 처리
                        "/projects/{id}/done" // 검토 확인
                );

        // 전문가 제안서 (bid)
        registry.addInterceptor(bidInterceptor)
                .addPathPatterns(
                        "/bid/save-form",
                        "/bid/save",
                        "/bid/my-list",
                        "/bid/{id}/update-form",
                        "/bid/{id}"

                ).excludePathPatterns(
                        "/bid/list",
                        "/bid/{id}/select"
                );

    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
