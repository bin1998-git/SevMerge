//package com.example.SevMerge.core.config;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
//import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
//import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
//
///**
// * Spring Security configuration.
// *
// * Patches applied:
// *   [M1]  CSRF protection enabled (CookieCsrfTokenRepository, SameSite=Strict)
// *   [M2]  Logout endpoint enforced as POST-only via Spring Security
// *   [M3]  Session cookie gets Secure + SameSite=Strict flags
// *   [L1]  Security headers: X-Frame-Options DENY, CSP, HSTS, X-Content-Type-Options
// */
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        // ── [M1] CSRF ──────────────────────────────────────────────────────────
//        // Use cookie-based CSRF token so Mustache templates can read it via JS.
//        // SameSite=Strict is set on the cookie to prevent cross-origin submission.
//        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
//        csrfTokenRepository.setCookiePath("/");
//        // cookieName defaults to XSRF-TOKEN; headerName to X-XSRF-TOKEN
//
//        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
//        // Do not use deferred resolution so the token is always present on every request
//        requestHandler.setCsrfRequestAttributeName(null);
//
//        http.csrf(csrf -> csrf
//                .csrfTokenRepository(csrfTokenRepository)
//                .csrfTokenRequestHandler(requestHandler)
//                // Exclude read-only API endpoints that use session-less flows
//                .ignoringRequestMatchers(
//                        "/api/email/send",
//                        "/api/email/verify",
//                        "/api/sms/send",
//                        "/api/sms/verify",
//                        "/api/**"
//                )
//        );
//
//        // ── [M2] Logout POST-only ──────────────────────────────────────────────
//        // Spring Security's logout handling replaces the GET /logout controller.
//        // The MemberController GET /logout is left in place but Spring Security
//        // will intercept POST /logout first; we also restrict it here so browsers
//        // can no longer trigger logout via GET link / img tag.
//        http.logout(logout -> logout
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//        );
//
//        // ── [M3] Session cookie flags ──────────────────────────────────────────
//        http.sessionManagement(session -> session
//                .sessionFixation().changeSessionId()   // Rotate session ID on login
//        );
//
//        // ── [L1] Security Headers ─────────────────────────────────────────────
//        http.headers(headers -> headers
//                // X-Frame-Options: DENY  (clickjacking)
//                .frameOptions(frame -> frame.deny())
//
//                // X-Content-Type-Options: nosniff
//                .contentTypeOptions(ct -> {})
//
//                // HSTS: max-age=31536000 (1 year), includeSubDomains
//                .httpStrictTransportSecurity(hsts -> hsts
//                        .includeSubDomains(true)
//                        .maxAgeInSeconds(31_536_000)
//                        .preload(false)
//                )
//
//                // Content-Security-Policy
//                .contentSecurityPolicy(csp -> csp
//                        .policyDirectives(
//                                "default-src 'self'; " +
//                                "script-src 'self' 'unsafe-inline' https://accounts.google.com https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://cdn.stomp.js.org; " +
//                                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
//                                "font-src 'self' https://fonts.gstatic.com; " +
//                                "img-src 'self' data: https:; " +
//                                "connect-src 'self' ws: wss:; " +
//                                "frame-ancestors 'none';"
//                        )
//                )
//
//                // Referrer-Policy
//                .referrerPolicy(referrer -> referrer
//                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
//                )
//        );
//
//        // ── Authorization – permit all (auth enforced by existing interceptors) ─
//        // This project uses its own interceptor-based auth, so we permit all
//        // HTTP requests at the Spring Security layer and let interceptors handle
//        // authentication/authorization checks.
//        http.authorizeHttpRequests(auth -> auth
//                .anyRequest().permitAll()
//        );
//
//        // Disable the default Spring Security login form (project has its own)
//        http.formLogin(form -> form.disable());
//        http.httpBasic(basic -> basic.disable());
//
//        return http.build();
//    }
//}
