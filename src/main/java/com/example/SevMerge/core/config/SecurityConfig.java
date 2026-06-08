//package com.example.SevMerge.core.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
///**
// * SecurityConfig
// * - 소셜 로그인은 WebClient(RestTemplate) 방식으로 직접 처리 (OAuth2Client 미사용)
// * - 접근 제어는 기존 인터셉터(LoginInterceptor, AdminInterceptor)가 담당
// */
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll()
//            )
//            .formLogin(form -> form.disable());
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
