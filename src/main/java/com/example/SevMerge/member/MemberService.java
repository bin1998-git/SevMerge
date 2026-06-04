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
    private final HttpSession session;


    // application.yml에 등록된 카카오 환경 변수 가져오기
    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    /**
     * 회원 전체 조회 기능
     */
    public List<MemberResponse> getAllMembers() {
        // 1. DB에서 전체 회원 엔티티 리스트 조회 (기본 내장 메서드)
        List<Member> members = memberRepository.findAll();

        // 2. 엔티티 리스트를 응답용 DTO 리스트로 변환하여 반환
        return members.stream()
                .map(MemberResponse::from)
                .toList();
    }

    // 이번 달 가입한 신규 회원 수 조회 기능
    @Transactional(readOnly = true)
    public long getNewMemberCountThisMonth() {
        return memberRepository.countNewMembersThisMonth();
    }

    // 승인 대기 전문가 조회 기능
    public long getPendingExpertCount() {
        log.info("승인 대기 전문가 수 조회 서비스 시작");
        Long count = memberRepository.pendingProjectsCount();
        return count == null ? 0L : count;
    }

    //회원가입
    @Transactional
    public void join(MemberRequest.Join request) {

        log.info("세션에서 꺼낸 verified_email: {}", session.getAttribute("verified_email"));
        log.info("요청 이메일: {}", request.getEmail());
        String verifiedEmail = (String) session.getAttribute("verified_email");
        if (verifiedEmail == null || !verifiedEmail.equals(request.getEmail())) {
            throw new BadRequestException("이메일 인증이 완료되지 않았습니다.");
        }

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
                    // 프로필이미지 널값 방지
                    .profileImage("default.png")
                    .intro("")
                    .career("")
                    .speciality("")
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

    //유틸
    @Transactional(readOnly = true) //단독수행
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    // --------------------------------------------------------------------------------------

    /**
     * 카카오 회원가입처리
     */
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
    // 카카오 콜백 1단계: 프로필만 받아서 기존 회원인지 확인
    // 기존 회원 → Member 반환 / 신규 → null 반환(가입은 역할 선택 후)
    @Transactional(readOnly = true)
    public MemberResponse.KakaoProfile getKakaoProfile(String code) {
        MemberResponse.OAuthToken oAuthToken = getKakaoAccessToken(code);
        return getKakaoUserProfile(oAuthToken.getAccessToken());
    }

    // 카카오 식별자로 기존 회원 조회 (없으면 null)
    @Transactional(readOnly = true)
    public Member findKakaoMember(Long kakaoId) {
        String kakaoUserKey = String.valueOf(kakaoId);
        return memberRepository.findByEmail(kakaoUserKey).orElse(null);
    }

    // 카카오 신규 회원 가입 (역할 선택 후 호출)
    @Transactional
    public Member registerKakaoMember(Long kakaoId, String nickname, String selectedRole) {
        String kakaoUserKey = String.valueOf(kakaoId);

        // 이미 가입돼 있으면 그대로 반환 (중복 방지)
        Member existing = memberRepository.findByEmail(kakaoUserKey).orElse(null);
        if (existing != null) return existing;

        Role role = "EXPERT".equals(selectedRole) ? Role.EXPERT : Role.CLIENT;
        Status status = (role == Role.EXPERT) ? Status.PENDING : Status.ACTIVE;
        String dummyPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString());

        Member newMember = Member.builder()
                .email(kakaoUserKey)
                .password(dummyPassword)
                .name(nickname)
                .phone("010-0000-0000")
                .role(role)
                .status(status)
                .build();
        Member savedMember = memberRepository.save(newMember);

        if (role == Role.EXPERT) {
            expertProfileRepository.save(ExpertProfile.builder()
                    .member(savedMember)
                    .profileImage("default.png")
                    .avgRating(BigDecimal.ZERO)
                    .totalReviews(0)
                    .isCertified(false)
                    .build());
            log.info("카카오 전문가 가입 완료 - memberId={}", savedMember.getId());
        }
        return savedMember;
    }

    /**
     * 구글 신규 회원 가입 처리 (역할 선택 완료 후 호출)
     */
    @Transactional
    public Member registerGoogleMember(String googleId, String nickname, String email, String selectedRole) {

        // 1. 역할(Role) 및 상태(Status) 결정
        Role role = "EXPERT".equals(selectedRole) ? Role.EXPERT : Role.CLIENT;
        Status status = (role == Role.EXPERT) ? Status.PENDING : Status.ACTIVE;

        // 소셜 로그인은 비밀번호가 없으므로 암호화된 임의의 UUID 비밀번호 부여
        String dummyPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString());

        // 2. Member 엔티티 생성 및 저장
        Member newMember = Member.builder()
                .email(email)
                .password(dummyPassword)
                .name(nickname)
                .phone("") // 전화번호는 빈 값 처리 (이후 정보수정에서 입력 가능)
                .role(role)
                .status(status)
                .provider("google")
                .providerId(googleId)
                .build();

        Member savedMember = memberRepository.save(newMember);

        // 3. 전문가(EXPERT)를 선택한 경우에만 전문가 프로필 데이터 생성
        if (role == Role.EXPERT) {
            expertProfileRepository.save(ExpertProfile.builder()
                    .member(savedMember)
                    .profileImage("default.png")
                    .avgRating(java.math.BigDecimal.valueOf(0.00))
                    .totalReviews(0)
                    .isCertified(false)
                    .build());
        }

        return savedMember;
    }
}