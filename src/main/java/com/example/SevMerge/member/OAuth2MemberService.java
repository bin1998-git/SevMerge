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

        //변경: 없으면 바로 저장하지 않고, null인 상태로 둠
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId).orElse(null);

        if (member != null) {
            log.info("[OAuth2] 구글 기존 회원 확인 - email={}", email);
        } else {
            log.info("[OAuth2] 구글 신규 유저 감지 (역할 선택 필요) - email={}", email);
            // 임시 세팅 (가입 x)
            member = Member.builder()
                    .email(email)
                    .name(name)
                    .provider(provider)
                    .providerId(providerId)
                    .role(Role.CLIENT)
                    .build();
        }
        log.info("[OAuth2] 로그인 성공 - memberId={}, email={}", member.getId(), email);
        // 세션에 저장할 Member 객체를 attribute로 담아서 전달
        return new OAuth2MemberDetails(member, attributes);
    }
}
