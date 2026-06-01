package com.example.SevMerge.member;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final PasswordEncoder passwordEncoder;


    // application.yml에 등록된 카카오 환경 변수 가져오기
    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    //회원가입
    @Transactional
    public void join(MemberRequest.Join request) {
        if (memberRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("이미 사용 중인 이메일입니다.");

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role(request.getRole())
                .status(request.getRole() == Role.EXPERT ? Status.PENDING : Status.ACTIVE)
                .build();
        memberRepository.save(member);

        // 전문가 신청 시 ExpertProfile 초기 생성
        if (request.getRole() == Role.EXPERT) {
            expertProfileRepository.save(ExpertProfile.builder()
                    .member(member)
                    .avgRating(BigDecimal.ZERO)
                    .totalReviews(0)
                    .isCertified(false)
                    .build());
            log.info("전문가 신청 완료 - memberId={}", member.getId());
        }
    }

    //로그인 / 로그아웃
    @Transactional(readOnly = true)
    public void login(MemberRequest.Login request, HttpSession session) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword()))
            throw new BadRequestException("비밀번호가 올바르지 않습니다.");

        if (member.getStatus() == Status.SUSPENDED)
            throw new BadRequestException("정지된 계정입니다.");

        session.setAttribute("sessionUser", member);
        log.info("로그인 성공 - memberId={}", member.getId());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    // 마이페이지
    @Transactional(readOnly = true)
    public MemberResponse getMyInfo(Long memberId) {
        return MemberResponse.from(findMemberById(memberId));
    }

    @Transactional
    public void updateMyInfo(Long memberId, MemberRequest.Update request) {
        Member member = findMemberById(memberId);
        member.updateInfo(request.getName(), request.getPhone());
    }

    @Transactional
    public void changePassword(Long memberId, MemberRequest.ChangePassword request) {
        Member member = findMemberById(memberId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword()))
            throw new BadRequestException("현재 비밀번호가 올바르지 않습니다.");
        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    // 관리자 전용
    @Transactional(readOnly = true)
    public List<MemberResponse> getPendingExperts() {
        return memberRepository.findByRoleAndStatus(Role.EXPERT, Status.PENDING)
                .stream().map(MemberResponse::from).toList();
    }

    @Transactional
    public void approveExpert(Long memberId) {
        Member member = findMemberById(memberId);
        if (member.getRole() != Role.EXPERT || member.getStatus() != Status.PENDING)
            throw new BadRequestException("전문가 승인 처리가 불가능한 상태입니다.");
        member.approve();
        log.info("전문가 승인 완료 - memberId={}", memberId);
    }

    @Transactional
    public void rejectExpert(Long memberId) {
        Member member = findMemberById(memberId);
        if (member.getRole() != Role.EXPERT || member.getStatus() != Status.PENDING)
            throw new BadRequestException("전문가 승인 처리가 불가능한 상태입니다.");
        member.reject();
        log.info("전문가 거부 처리 - memberId={}", memberId);
    }

    @Transactional
    public void suspendMember(Long memberId) {
        findMemberById(memberId).suspend();
        log.info("회원 정지 처리 - memberId={}", memberId);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> searchMembers(String keyword) {
        return memberRepository.searchByKeyword(keyword)
                .stream().map(MemberResponse::from).toList();
    }

    //내부 유틸
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    // 카카오 로그인 (역할은 state로 전달받음)
    @Transactional
    public Member kakaoLogin(String code, String selectedRole) {
        MemberResponse.OAuthToken oAuthToken = getKakaoAccessToken(code);
        MemberResponse.KakaoProfile kakaoProfile = getKakaoUserProfile(oAuthToken.getAccessToken());
        return processKakaoUserSync(kakaoProfile, selectedRole);
    }

    // 1. 인가 코드로 카카오 액세스 토큰 요청
    private MemberResponse.OAuthToken getKakaoAccessToken(String code) {
        RestTemplate restTemplate1 = new RestTemplate();

        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("grant_type", "authorization_code");
        multiValueMap.add("client_id", kakaoClientId);
        multiValueMap.add("redirect_uri", "http://localhost:8080/kakao-redirect");  // 콘솔 등록값과 일치
        multiValueMap.add("code", code);
        multiValueMap.add("client_secret", kakaoClientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(multiValueMap, headers1);

        ResponseEntity<MemberResponse.OAuthToken> response1 = restTemplate1.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                request,
                MemberResponse.OAuthToken.class
        );
        return response1.getBody();
    }

    // 2단계 액세스 토큰으로 카카오 사용자 정보 조회
    private MemberResponse.KakaoProfile getKakaoUserProfile(String accessToken) {

        RestTemplate restTemplate2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + accessToken);
        headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity request2 = new HttpEntity(headers2);

        // HTTP  요청 2
        ResponseEntity<MemberResponse.KakaoProfile> response2 = restTemplate2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                request2,
                MemberResponse.KakaoProfile.class
        );
        MemberResponse.KakaoProfile kakaoProfile = response2.getBody();
        return kakaoProfile;

    }

    // 3단계.
    private Member processKakaoUserSync(MemberResponse.KakaoProfile kakaoProfile, String selectedRole) {

        String nickname = kakaoProfile.getKakaoAccount().getProfile().getNickname();

        //todo 이메일 제거
        // 1. MemberRepository 표준 인터페이스 사양(Optional)에 맞춰 연동 및 가입 유무 분기 처리
        return memberRepository.findByEmail(nickname)
                .orElseGet(() -> {
                    log.info("기존 SevMerge 소셜 회원이 아님 - 카카오 자동 회원 가입을 진행합니다: {}");

                    Role role = Role.valueOf(selectedRole);
                    // 전문가(EXPERT) 계정으로 접근한 경우 관리자 승인 전까지 PENDING, 의뢰인은 즉시 ACTIVE 상태 부여
                    Status status = (role == Role.EXPERT) ? Status.PENDING : Status.ACTIVE;

                    // password 컬럼 무결성(nullable=false) 규격을 충족하기 위해 유추 불가능한 난수 패스워드 주입
                    String dummyPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString());

                    // 2. 신규 Member 엔티티 구축 및 영속화
                    Member newMember = Member.builder()
                            //todo 이메일 제거
                            //.email(email)
                            .password(dummyPassword)
                            .name(nickname)
                            .phone("010-0000-0000") // 엔티티 필수 컬럼 제약 방어 공통 기본값
                            .role(role)
                            .status(status)
                            .build();

                    Member savedMember = memberRepository.save(newMember);

                    // 3. 전문가(EXPERT) 소셜 회원 가입인 경우, 1:1 매핑 테이블인 ExpertProfile 연관 데이터 동시 생성
                    if (role == Role.EXPERT) {
                        ExpertProfile defaultProfile = ExpertProfile.builder()
                                .member(savedMember)
                                .profileImage("default.png") // 필수 이미지 텍스트 조건 방어
                                .avgRating(BigDecimal.ZERO)
                                .totalReviews(0)
                                .isCertified(false)
                                .build();
                        expertProfileRepository.save(defaultProfile);
                        log.info("카카오 전문가 프로필 껍데기 레코드 적재 완료 - memberId={}", savedMember.getId());
                    }

                    return savedMember;
                });
    }
}