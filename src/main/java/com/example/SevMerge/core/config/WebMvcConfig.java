package com.example.SevMerge.core.config;


import com.example.SevMerge.core.interceptor.AdminInterceptor;
import com.example.SevMerge.core.interceptor.LoginInterceptor;
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


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 모든 요청에서 세션 정보 뷰에 주입
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**");

        // 로그인 체크
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/users/**")
                .excludePathPatterns(
                        "/",
                        "/login-form",
                        "/join-form",
                        "/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/templates"
                );

        // 관리자 체크
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
