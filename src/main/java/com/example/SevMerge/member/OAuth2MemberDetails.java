package com.example.SevMerge.member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 로그인 결과를 감싸는 래퍼
 * - SecurityConfig successHandler에서 member 객체를 꺼내 세션에 저장하기 위해 사용
 */
public class OAuth2MemberDetails implements OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes;

    public OAuth2MemberDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
    }

    @Override
    public String getName() {
        return member.getEmail();
    }
}
