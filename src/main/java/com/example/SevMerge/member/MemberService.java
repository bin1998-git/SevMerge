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


    // 카카오 환경 변수
    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth.kakao.client-secret}")
    private String kakaoClientSecret;

    // 구글 환경 변수
    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

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
        // 문자 메세지 보내기 기능 예시
//        if (member.isClient()) {
//            // todo - SevMerge 프로젝트명으로 수정
//            solApiService.sendSms(member.getPhone(), member.getName() + "의뢰인님 Sev Merge에 가입하신 걸 환영합니다!");
//        } else if (member.isExpert()) {
//            solApiService.sendSms(member.getPhone(), member.getName() + "전문가님 Sev Merge에 가입하신 걸 환영합니다!\n관리자 승인까지 약 30분 소요됩니다.");
//        }
    }

    //로그인 / 로그아웃
    @Transactional(readOnly = true)
    public void login(MemberRequest.Login request, HttpSession session) {
        Member member = memberRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new BadRequestException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword()))
            throw new BadRequestException("비밀번호가 올바르지 않습니다.");

        if (member.getStatus() == Status.SUSPENDED)
            throw new BadRequestException("정지된 계정입니다.");

        if (member.getStatus() == Status.PENDING)
            throw new BadRequestException("관리자 승인 대기 중인 계정입니다. 승인 후 로그인할 수 있습니다.");

        session.setAttribute("sessionUser", member);
        log.info("로그인 성공 - memberId={}", member.getId());
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }


    // 소프트삭제 메서드
    @Transactional
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        //엔티티 메서드를 호출해 상태만 true 변경.
        member.withdraw();
        log.info("회원 탈퇴 완료 (Dirty Checking으로 DB 반영) - memberId={}", memberId);
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
        //  탈퇴하지 않은 유저만 필터링 추가
        return memberRepository.searchByKeyword(keyword)
                .stream()
                .filter(m -> !m.isDeleted())
                .map(MemberResponse::from)
                .toList();
    }

    //유틸
    @Transactional(readOnly = true) //단독수행
    public Member findMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));

        // 탈퇴자 조회시 예외 처리
        if (member.isDeleted()) {
            throw new NotFoundException("탈퇴한 회원입니다.");
        }
        return member;
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
        return response2.getBody();
    }

    // 3단계.
    // 카카오 콜백 1단계: 프로필만 받아서 기존 회원인지 확인
    // 기존 회원 -> Member 반환 / 신규 -> null 반환(가입은 역할 선택 후)
    @Transactional(readOnly = true)
    public MemberResponse.KakaoProfile getKakaoProfile(String code) {
        MemberResponse.OAuthToken oAuthToken = getKakaoAccessToken(code);
        return getKakaoUserProfile(oAuthToken.getAccessToken());
    }

    // 카카오 식별자로 기존 회원 조회 (없으면 null)
    @Transactional(readOnly = true)
    public Member findKakaoMember(Long kakaoId) {
        String kakaoUserKey = String.valueOf(kakaoId);
        Member member = memberRepository.findByEmailAndIsDeletedFalse(kakaoUserKey).orElse(null);

        if (member != null && member.isDeleted()) {
            return null;
        }
        return member;
    }

    // 카카오 신규 의뢰인 회원가입 전용
    @Transactional
    public Member registerKakaoMember(Long kakaoId, String nickname, String selectedRole) {
        String kakaoUserKey = String.valueOf(kakaoId);

        Member existing = findKakaoMember(kakaoId);
        if (existing != null) return existing;

        String dummyPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString());

        Member newMember = Member.builder()
                .email(kakaoUserKey)
                .password(dummyPassword)
                .name(nickname)
                .phone("010-0000-0000")
                .role(Role.CLIENT)
                .status(Status.ACTIVE)
                .build();
        return memberRepository.save(newMember);

    }

    /**
     * 구글 신규 회원 가입 처리 (역할 선택 완료 후 호출)
     */
    @Transactional
    public Member registerGoogleMember(String googleId, String nickname, String email, String selectedRole) {

        Member existing = findGoogleMember(googleId);
        if (existing != null) return existing;
        // 소셜 로그인은 비밀번호가 없으므로 암호화된 임의의 UUID 비밀번호 부여
        String dummyPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString());

        // 2. Member 엔티티 생성 및 저장
        Member newMember = Member.builder()
                .email(email)
                .password(dummyPassword)
                .name(nickname)
                .phone("") // 전화번호 빈 값 처리 (이후 정보수정에서 입력 가능)
                .role(Role.CLIENT)
                .status(Status.ACTIVE)
                .provider("google")
                .providerId(googleId)
                .build();

        return memberRepository.save(newMember);

    }

    // 카카오 전문가 가입
    @Transactional
    public Member registerKakaoExpert(Long kakaoId, String nickname, MemberRequest.ExpertJoin req) {
        String kakaoUserKey = String.valueOf(kakaoId);

        Member existing = findKakaoMember(kakaoId);
        if (existing != null) return existing;

        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new BadRequestException("연락 가능한 이메일을 입력해 주세요.");
        }

        String dummyPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString());
        Member newMember = Member.builder()
                .email(kakaoUserKey)                          // 로그인 식별자는 고유ID 유지
                .password(dummyPassword)
                .name(req.getName() != null ? req.getName() : nickname)  // 화면에서 수정한 이름
                .phone("010-0000-0000")
                .role(Role.EXPERT)
                .status(Status.PENDING)
                .build();
        Member saved = memberRepository.save(newMember);

        saveExpertProfile(saved, req);
        log.info("카카오 전문가 가입(PENDING) - memberId={}", saved.getId());
        return saved;
    }

    // 구글 전문가 가입
    @Transactional
    public Member registerGoogleExpert(String googleId, String nickname, String email, MemberRequest.ExpertJoin req) {
        Member existing = findGoogleMember(googleId);
        if (existing != null) return existing;

        if (memberRepository.existsByEmail(email)) {
            throw new BadRequestException("이미 가입된 이메일입니다.");
        }

        String dummyPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString());
        Member newMember = Member.builder()
                .email(email)                                 // 구글은 이메일이 로그인 식별자
                .password(dummyPassword)
                .name(req.getName() != null ? req.getName() : nickname)
                .phone("")
                .role(Role.EXPERT)
                .status(Status.PENDING)
                .provider("google")
                .providerId(googleId)
                .build();
        Member saved = memberRepository.save(newMember);

        saveExpertProfile(saved, req);
        log.info("구글 전문가 가입(PENDING) - memberId={}", saved.getId());
        return saved;
    }

    // 전문가 프로필 저장
    private void saveExpertProfile(Member member, MemberRequest.ExpertJoin req) {
        expertProfileRepository.save(ExpertProfile.builder()
                .member(member)
                .profileImage("default.png")
                .intro(req.getIntro())
                .career(req.getCareer())
                .githubUrl(req.getGithubUrl())
                .contactEmail(req.getEmail())
                .speciality("")
                .avgRating(BigDecimal.ZERO)
                .totalReviews(0)
                .isCertified(false)
                .build());
    }

    // ===================== 구글 WebClient 방식 =====================

    /**
     * 구글 인가 코드 → 액세스 토큰 → 사용자 정보 조회
     */
    public MemberResponse.GoogleProfile getGoogleProfile(String code) {
        // 1. 액세스 토큰 발급
        RestTemplate rt1 = new RestTemplate();
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> req1 = new HttpEntity<>(params, headers1);
        ResponseEntity<MemberResponse.GoogleToken> tokenRes = rt1.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                req1,
                MemberResponse.GoogleToken.class
        );
        String accessToken = tokenRes.getBody().getAccessToken();

        // 2. 사용자 정보 조회
        RestTemplate rt2 = new RestTemplate();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> req2 = new HttpEntity<>(headers2);
        ResponseEntity<MemberResponse.GoogleProfile> profileRes = rt2.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET,
                req2,
                MemberResponse.GoogleProfile.class
        );
        return profileRes.getBody();
    }

    /**
     * 구글 sub(고유 ID)로 기존 회원 조회 (없으면 null)
     */
    @Transactional(readOnly = true)
    public Member findGoogleMember(String googleId) {
        Member member = memberRepository.findByProviderAndProviderId("google", googleId).orElse(null);

        if (member != null && member.isDeleted()) {
            return null;
        }
        return member;
    }
}