package com.example.SevMerge.member;

import com.example.SevMerge.bid.BidRepository;
import com.example.SevMerge.core.exception.AdminException;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.core.util.FileUtil;
import com.example.SevMerge.expertprofile.*;
import com.example.SevMerge.notification.NotificationService;
import com.example.SevMerge.notification.SolApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;
    private final ExpertReviewLogRepository expertReviewLogRepository;
    private final BidRepository bidRepository;

    //문자 발송
    private final SolApiService solApiService;

    private final NotificationService notificationService;

    // ── [L4] Account lockout state (in-memory, per email) ────────────────────
    private static final int  MAX_FAIL_ATTEMPTS  = 5;
    private static final long LOCKOUT_DURATION_MS = 10 * 60_000L; // 10 minutes

    private static class LoginAttempt {
        final AtomicInteger failures = new AtomicInteger(0);
        volatile long lockedUntil = 0L;
    }
    private final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();


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
        List<Member> members = memberRepository.findAllByIsDeletedFalse();

        // 2. 엔티티 리스트를 응답용 DTO 리스트로 변환하여 반환
        return members.stream()
                .map(MemberResponse::from)
                .toList();
    }


    //회원가입
    @Transactional
    public void join(MemberRequest.Join request, MultipartFile profileImageFile) {

        log.info("세션에서 꺼낸 verified_email: {}", session.getAttribute("verified_email"));
        log.info("요청 이메일: {}", request.getEmail());
        String verifiedEmail = (String) session.getAttribute("verified_email");
        if (verifiedEmail == null || !verifiedEmail.equals(request.getEmail())) {
            throw new BadRequestException("이메일 인증이 완료되지 않았습니다.");
        }
        if (memberRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("이미 사용 중인 이메일입니다.");

        String verifiedPhone = (String) session.getAttribute("verified_phone");
        String reqPhone = request.getPhone() == null ? "" : request.getPhone().replaceAll("-", "");
        if (verifiedPhone == null || !verifiedPhone.equals(reqPhone)) {
            throw new BadRequestException("휴대폰 인증이 완료되지 않았습니다.");
        }
        String savedImage = null;
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            if (!FileUtil.isImageFile(profileImageFile)) {
                throw new BadRequestException("이미지 파일만 업로드할 수 있습니다.");
            }
            try {
                savedImage = FileUtil.saveFile(profileImageFile, FileUtil.IMAGES_DIR);
            } catch (IOException e) {
                throw new BadRequestException("이미지 업로드에 실패했습니다.");
            }
        }
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role(request.getRole())
                .status(request.getRole() == Role.EXPERT ? Status.PENDING : Status.ACTIVE)
                .profileImage(savedImage)
                .build();
        memberRepository.save(member);
        session.removeAttribute("verified_email");
        session.removeAttribute("verified_phone");

        // 전문가 신청 시 ExpertProfile 초기 생성
        if (request.getRole() == Role.EXPERT) {
            expertProfileRepository.save(ExpertProfile.builder()
                    .member(member)
                    .isCertified(false)
                    .profileImage(savedImage != null ? savedImage : "default.png")
                    .intro(request.getIntro())
                    .career(request.getCareer())
                    .githubUrl(request.getGithubUrl())
                    .contactEmail(request.getEmail())   // 일반가입은 입력 이메일이 연락처
                    .speciality(request.getSpeciality() != null ? request.getSpeciality() : "")
                    .expertGrade(Grade.NORMAL)
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
    public Member login(MemberRequest.Login request, HttpSession session) {
        String email = request.getEmail();

        // ── [L4] Account lockout check ─────────────────────────────────────
        LoginAttempt attempt = loginAttempts.computeIfAbsent(email, k -> new LoginAttempt());
        long now = System.currentTimeMillis();
        if (now < attempt.lockedUntil) {
            long remainSec = (attempt.lockedUntil - now) / 1000;
            throw new BadRequestException("계정이 일시적으로 잠겼습니다. " + remainSec + "초 후에 다시 시도하세요.");
        }

        Member member = memberRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> {
                    // Count failed attempt even for non-existent email (timing-safe)
                    recordFailedAttempt(attempt, now, email);
                    return new BadRequestException("이메일 또는 비밀번호가 일치하지 않습니다.");
                });

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            recordFailedAttempt(attempt, now, email);
            throw new BadRequestException("비밀번호가 올바르지 않습니다.");
        }

        // Success – clear failure counter
        attempt.failures.set(0);
        attempt.lockedUntil = 0L;
        log.info("[L4] 로그인 성공, 실패 카운터 초기화 - email={}", email);

        if (member.getStatus() == Status.SUSPENDED)
            return member;

        if (member.getStatus() == Status.PENDING)
            throw new BadRequestException("관리자 승인 대기 중인 계정입니다. 승인 후 로그인할 수 있습니다.");

        // 거절자는 세션에 안 넣고 멤버만 반환 (컨트롤러가 안내 페이지로 보냄)
        if (member.getStatus() == Status.REJECTED) {
            return member;
        }

        session.setAttribute("sessionUser", member);
        log.info("로그인 성공 - memberId={}", member.getId());
        return member;
    }

    /** Record one failed login attempt; lock account after MAX_FAIL_ATTEMPTS. */
    private void recordFailedAttempt(LoginAttempt attempt, long now, String email) {
        int failures = attempt.failures.incrementAndGet();
        log.warn("[L4] 로그인 실패 누적 {}/{} - email={}", failures, MAX_FAIL_ATTEMPTS, email);
        if (failures >= MAX_FAIL_ATTEMPTS) {
            attempt.lockedUntil = now + LOCKOUT_DURATION_MS;
            log.warn("[L4] 계정 잠금 적용 ({}분) - email={}", LOCKOUT_DURATION_MS / 60_000, email);
        }
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

        if (member.isAdmin()) {
            throw new AdminException("관리자 계정은 삭제가 불가능합니다.");
        }
        member.withdraw();
        log.info("회원 탈퇴 완료 (Dirty Checking으로 DB 반영) - memberId={}", memberId);
    }

    // 마이페이지
    @Transactional(readOnly = true)
    public MemberResponse getMyInfo(Long memberId) {
        return MemberResponse.from(findMemberById(memberId));
    }

    @Transactional
    public void updateMyInfo(Long memberId, MemberRequest.Update request, MultipartFile profileImageFile) {
        Member member = findMemberById(memberId);

        String phone = (request.getPhone() != null && !request.getPhone().isBlank())
                ? request.getPhone() : member.getPhone();
        member.updateInfo(request.getName(), phone);

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (member.getProvider() != null && !member.getProvider().isBlank()) {
                throw new BadRequestException("소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
            }
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
                throw new BadRequestException("현재 비밀번호를 입력해 주세요.");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
                throw new BadRequestException("현재 비밀번호가 일치하지 않습니다.");
            }
            if (passwordEncoder.matches(request.getNewPassword(), member.getPassword())) {
                throw new BadRequestException("기존 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.");
            }
            member.changePassword(passwordEncoder.encode(request.getNewPassword()));
        }

        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            if (!FileUtil.isImageFile(profileImageFile)) {
                throw new BadRequestException("이미지 파일만 업로드할 수 있습니다.");
            }
            try {
                String savedImage = FileUtil.saveFile(profileImageFile, FileUtil.IMAGES_DIR);
                member.updateProfileImage(savedImage);
            } catch (IOException e) {
                throw new BadRequestException("이미지 업로드에 실패했습니다.");
            }
        }
    }

    // 비밀번호 검증 메서드
    @Transactional(readOnly = true)
    public boolean verifyPassword(Long memberId, String rawPassword) {
        Member member = findMemberById(memberId);
        if (rawPassword == null || rawPassword.isBlank()) return false;
        if (member.getProvider() != null && !member.getProvider().isBlank()) return false;
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }

    @Transactional
    public void changePassword(Long memberId, MemberRequest.ChangePassword request) {
        Member member = findMemberById(memberId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword()))
            throw new BadRequestException("현재 비밀번호가 올바르지 않습니다.");
        member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }


    // 이름+전화번호로 이메일 찾기
    @Transactional(readOnly = true)
    public String findEmailByNameAndPhone(String name, String phone) {
        String cleanPhone = phone.replaceAll("-", "");
        return memberRepository.findAll().stream()
                .filter(m -> !m.isDeleted()
                        && m.getName().equals(name)
                        && m.getPhone() != null
                        && m.getPhone().replaceAll("-", "").equals(cleanPhone))
                .findFirst()
                .map(m -> {
                    // 소셜 로그인 계정은 이메일 노출 대신 안내
                    if (m.getProvider() != null && !m.getProvider().isBlank()) {
                        return m.getProvider().toUpperCase() + " 소셜 계정으로 가입된 계정입니다.";
                    }
                    // 이메일 마스킹
                    String email = m.getEmail();
                    int atIdx = email.indexOf('@');
                    if (atIdx <= 2) return email;
                    return email.substring(0, 2) + "**" + email.substring(atIdx);
                })
                .orElseThrow(() -> new BadRequestException("일치하는 회원 정보가 없습니다."));
    }

    // 이름+전화번호로 임시 비밀번호 발급
    @Transactional
    public String resetPasswordByPhone(String name, String phone) {
        String cleanPhone = phone.replaceAll("-", "");
        Member member = memberRepository.findAll().stream()
                .filter(m -> !m.isDeleted()
                        && m.getName().equals(name)
                        && m.getPhone() != null
                        && m.getPhone().replaceAll("-", "").equals(cleanPhone))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("일치하는 회원 정보가 없습니다."));

        if (member.getProvider() != null && !member.getProvider().isBlank()) {
            throw new BadRequestException("소셜 로그인 계정은 비밀번호를 재설정할 수 없습니다.");
        }

        // 임시 비밀번호 생성 (영문+숫자+특수문자 10자)
        String tempPw = generateTempPassword();
        member.changePassword(passwordEncoder.encode(tempPw));

        sendStatusSms(member, "[SevMerge] 임시 비밀번호: " + tempPw + " (로그인 후 반드시 변경해 주세요)");
        log.info("임시 비밀번호 발급 - memberId={}", member.getId());
        return tempPw;
    }

    // 이름+전화번호로 회원 존재 확인
    @Transactional(readOnly = true)
    public boolean existsByNameAndPhone(String name, String phone) {
        String cleanPhone = phone.replaceAll("-", "");
        return memberRepository.findAll().stream()
                .anyMatch(m -> !m.isDeleted()
                        && m.getName().equals(name)
                        && m.getPhone() != null
                        && m.getPhone().replaceAll("-", "").equals(cleanPhone));
    }

    // 임시 비밀번호 생성
    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789!@#$%";
        java.util.Random random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder();
        // 최소 구성 보장
        sb.append("ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz".charAt(random.nextInt(46))); // 영문
        sb.append("23456789".charAt(random.nextInt(8)));  // 숫자
        sb.append("!@#$%".charAt(random.nextInt(5)));     // 특수문자
        for (int i = 3; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        // 섞기
        char[] arr = sb.toString().toCharArray();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }
        return new String(arr);
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

    // 역할별 카테고리 조회
    public Page<MemberResponse> searchMembersByRoleAndKeyword(String roleFilter, String keyword, Pageable pageable) {
        try {
            Role role = Role.valueOf(roleFilter.toUpperCase());
            Page<Member> memberPage = memberRepository.findByRoleAndKeyword(role, keyword, pageable);
            return memberPage.map(MemberResponse::from);
        } catch (BadRequestException e) {
            return Page.empty(pageable);
        }
    }

    // 승인 대기 전문가 조회 기능
    public long getPendingExpertCount() {
        log.info("승인 대기 전문가 수 조회 서비스 시작");
        Long count = memberRepository.pendingProjectsCount();
        return count == null ? 0L : count;
    }

    // 일자별 가입자 데이터 차트만들기
    public List<Integer> getPast7DaysMemberTrend() {
        List<Object[]> rawData = memberRepository.findRecent7DaysRegistrationCount();
        Map<String, Integer> dateCountMap = new HashMap<>();
        for (Object[] row : rawData) {
            String date = (String) row[0];
            int count = ((Number) row[1]).intValue();
            dateCountMap.put(date, count);
        }

        List<Integer> trendData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            String targetDate = today.minusDays(i).format(formatter);
            trendData.add(dateCountMap.getOrDefault(targetDate, 0));
        }
        return trendData;
    }

    // 상태 변경 문자 발송
    private void sendStatusSms(Member member, String message) {
        String phone = member.getPhone();
        // 휴대폰 번호 없을때 건너뛰기
        if (phone == null || phone.isBlank() || phone.equals("010-0000-0000")) {
            log.info("휴대폰 번호 없음 - 문자 생략 memberId={}", member.getId());
            return;
        }
        try {
            String to = phone.replaceAll("-", "");  // 하이픈 제거
            solApiService.sendSms(to, message);
            log.info("상태 변경 문자 발송 완료 - memberId={}", member.getId());
        } catch (Exception e) {
            // 문자 실패가 승인/거절에 영향주지 않게 설정
            log.warn("문자 발송 실패 (처리는 정상) - memberId={}, 사유={}", member.getId(), e.getMessage());
        }
    }

    // 전문가 승인
    @Transactional
    public void approveExpert(Long memberId) {
        Member member = findMemberById(memberId);
        if (member.getRole() != Role.EXPERT || member.getStatus() != Status.PENDING)
            throw new BadRequestException("전문가 승인 처리가 불가능한 상태입니다.");
        member.approve();

        expertProfileRepository.findByMemberId(memberId).ifPresent(profile -> {
            profile.setCertified(true);
        });

        // 심사 승인 정보저장
        expertReviewLogRepository.save(ExpertReviewLog.builder()
                .member(member)
                .result("APPROVED")
                .reason(null)
                .build());
        log.info("전문가 승인 완료 - memberId={}", memberId);
        sendStatusSms(member,
                "[Sev Merge] " + member.getName() + " 전문가님, 전문가 신청이 승인되었습니다. 지금 바로 활동을 시작해보세요!");
        notificationService.notifyExpertApproved(member);
    }

    // 전문가 거절
    @Transactional
    public void rejectExpert(Long memberId, String reason) {
        Member member = findMemberById(memberId);
        if (member.getRole() != Role.EXPERT || member.getStatus() != Status.PENDING)
            throw new BadRequestException("전문가 승인 처리가 불가능한 상태입니다.");
        member.reject();

        expertProfileRepository.findByMemberId(memberId).ifPresent(profile -> {
            profile.setCertified(false);
        });

        //심사 거절 정보저장
        expertReviewLogRepository.save(ExpertReviewLog.builder()
                .member(member)
                .result("REJECTED")
                .reason(reason)
                .build());
        log.info("전문가 거부 처리 - memberId={}", memberId);
        sendStatusSms(member,
                "[Sev Merge] " + member.getName() + " 전문가님, 전문가 신청이 거부되었습니다. 자세한 내용은 고객센터를 이용해주세요.");
        notificationService.notifyExpertRejected(member);
    }

    // 재신청용 기존 프로필 조회
    @Transactional(readOnly = true)
    public ExpertProfileResponse getExpertProfileForReapply(Long memberId) {
        ExpertProfile profile = expertProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("전문가 프로필이 없습니다."));
        return ExpertProfileResponse.from(profile);
    }

    // 거절된 전문가 재신청 (정보 업데이트 + REJECTED → PENDING)
    @Transactional
    public void reapplyExpert(Long memberId, MemberRequest.ExpertJoin request) {
        Member member = findMemberById(memberId);
        if (member.getRole() != Role.EXPERT || member.getStatus() != Status.REJECTED) {
            throw new BadRequestException("재신청할 수 없는 상태입니다.");
        }

        // 전문가 정보 업데이트
        ExpertProfile profile = expertProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("전문가 프로필이 없습니다."));
        profile.setIntro(request.getIntro());
        profile.setCareer(request.getCareer());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setSpeciality(request.getSpeciality() != null ? request.getSpeciality() : "");

        // 상태 되돌리기
        member.reapply();
        log.info("전문가 재신청 (정보 수정) - memberId={}", memberId);
    }

    // 최근 거절 사유 조회
    @Transactional(readOnly = true)
    public String getLatestRejectReason(Long memberId) {
        return expertReviewLogRepository
                .findLatestReject(memberId)
                .map(ExpertReviewLog::getReason)
                .orElse(null);
    }

    // 회원 정지처리
    @Transactional
    public void suspendMember(Long memberId) {
        Member member = findMemberById(memberId);
        member.suspend();

        // 전문가 계정 정지 시 진행 중인 프로젝트 의뢰인에게 알림
        if (member.isExpert()) {
            bidRepository.findSelectedBidsByExpertId(memberId)
                    .forEach(bid -> notificationService.notifyExpertSuspended(
                            bid.getProject().getMember(),
                            member.getName(),
                            bid.getProject().getTitle()
                    ));
        }
        log.info("회원 정지 처리 - memberId={}", memberId);
    }

    // 회원 검색
    @Transactional(readOnly = true)
    public Page<MemberResponse> searchMembers(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return memberRepository.searchByKeyword(keyword.trim(), pageable).map(MemberResponse::from);
        }
        return memberRepository.findByIsDeletedFalse(pageable).map(MemberResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> searchMembersOrderByCreatedAt(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return memberRepository.searchByKeywordOrderByCreatedAt(keyword.trim(), pageable).map(MemberResponse::from);
        }
        return memberRepository.findByIsDeletedFalseOrderByCreatedAt(pageable).map(MemberResponse::from);
    }

    // 대기중 전문가 목록 조회
    @Transactional(readOnly = true)
    public List<ExpertProfileResponse> getPendingExpertProfiles() {
        List<Member> pendingMembers = memberRepository.findByRoleAndStatus(Role.EXPERT, Status.PENDING);
        return pendingMembers.stream()
                .map(member -> {
                    ExpertProfile profile = expertProfileRepository.findByMemberId(member.getId()).orElse(null);
                    if (profile == null) return null;
                    return ExpertProfileResponse.from(profile);
                })
                .filter(java.util.Objects::nonNull) // 프로필이 혹시 없는 예외 케이스 제외
                .toList();
    }

    // 전문가 승인 대기, 완료, 거절 상태 확인
    @Transactional(readOnly = true)
    public List<ExpertProfileResponse> getExpertProfilesByStatus(Status status) {

        List<Member> allMembers = memberRepository.findAll();
        List<ExpertProfileResponse> responseList = new java.util.ArrayList<>();

        for (Member member : allMembers) {
            if (!member.isDeleted() && member.getRole() == Role.EXPERT && member.getStatus() == status) {

                ExpertProfile profile = expertProfileRepository.findByMemberId(member.getId()).orElse(null);

                if (profile != null) {

                    responseList.add(ExpertProfileResponse.from(profile));
                } else {

                    ExpertProfile dummyProfile = ExpertProfile.builder()
                            .member(member)
                            .profileImage("default.png")
                            .intro("등록된 소개글이 없습니다.")
                            .career("경력 정보 없음")
                            .speciality("기술 분야 없음")
                            .build();

                    responseList.add(ExpertProfileResponse.from(dummyProfile));
                }
            }
        }
        return responseList;
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
    public Member registerKakaoMember(Long kakaoId, String nickname, String image, String selectedRole) {
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
                .provider("kakao")
                .providerId(kakaoUserKey)
                .profileImage(image)
                .build();
        return memberRepository.save(newMember);

    }

    /**
     * 구글 신규 회원 가입 처리 (역할 선택 완료 후 호출)
     */
    @Transactional
    public Member registerGoogleMember(String googleId, String nickname, String email, String image, String selectedRole) {

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
                .profileImage(image)
                .build();

        return memberRepository.save(newMember);

    }

    // 카카오 전문가 가입
    @Transactional
    public Member registerKakaoExpert(Long kakaoId, String nickname, String image, MemberRequest.ExpertJoin req) {
        String kakaoUserKey = String.valueOf(kakaoId);

        Member existing = findKakaoMember(kakaoId);
        if (existing != null) return existing;


        String dummyPassword = passwordEncoder.encode(java.util.UUID.randomUUID().toString());
        Member newMember = Member.builder()
                .email(kakaoUserKey)                          // 로그인 식별자는 고유ID 유지
                .password(dummyPassword)
                .name(req.getName() != null ? req.getName() : nickname)  // 화면에서 수정한 이름
                .phone("010-0000-0000")
                .role(Role.EXPERT)
                .status(Status.PENDING)
                .provider("kakao")
                .providerId(kakaoUserKey)
                .profileImage(image)
                .build();
        Member saved = memberRepository.save(newMember);

        saveExpertProfile(saved, req);
        log.info("카카오 전문가 가입(PENDING) - memberId={}", saved.getId());
        return saved;
    }

    // 구글 전문가 가입
    @Transactional
    public Member registerGoogleExpert(String googleId, String nickname, String email, String image, MemberRequest.ExpertJoin req) {
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
                .profileImage(image)
                .build();
        Member saved = memberRepository.save(newMember);

        saveExpertProfile(saved, req);
        log.info("구글 전문가 가입(PENDING) - memberId={}", saved.getId());
        return saved;
    }

    // 전문가 프로필 저장
    private void saveExpertProfile(Member member, MemberRequest.ExpertJoin req) {
        String contactEmail = (req.getEmail() != null && !req.getEmail().isBlank())
                ? req.getEmail()
                : member.getEmail();
        expertProfileRepository.save(ExpertProfile.builder()
                .member(member)
                .profileImage("default.png")
                .intro(req.getIntro())
                .career(req.getCareer())
                .githubUrl(req.getGithubUrl())
                .contactEmail(contactEmail)
                .speciality(req.getSpeciality() != null ? req.getSpeciality() : "")
                .isCertified(false)
                .expertGrade(Grade.NORMAL)
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