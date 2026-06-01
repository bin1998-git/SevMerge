package com.example.SevMerge.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 구글 OAuth2 로그인 처리 서비스
 * - 구글에서 받은 유저 정보로 기존 회원 조회 또는 자동 신규 가입 처리
 * - 로그인 성공 후 세션은 SecurityConfig의 successHandler에서 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 구글에서 유저 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider   = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String providerId = (String) attributes.get("sub");   // 구글 고유 ID
        String email      = (String) attributes.get("email");
        String name       = (String) attributes.get("name");

        // 기존 회원 조회 → 없으면 자동 가입
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    log.info("[OAuth2] 신규 구글 회원 자동 가입 - email={}", email);
                    return memberRepository.save(
                            Member.builder()
                                    .email(email)
                                    .password(null)       // 소셜 로그인은 비밀번호 없음
                                    .name(name)
                                    .phone("")            // 전화번호는 추후 입력
                                    .role(Role.CLIENT)
                                    .status(Status.ACTIVE)
                                    .provider(provider)
                                    .providerId(providerId)
                                    .build()
                    );
                });

        log.info("[OAuth2] 로그인 성공 - memberId={}, email={}", member.getId(), email);

        // 세션에 저장할 Member 객체를 attribute로 담아서 전달
        return new OAuth2MemberDetails(member, attributes);
    }
}
