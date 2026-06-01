package com.example.SevMerge.member;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final PasswordEncoder passwordEncoder;

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

//         전문가 신청 시 ExpertProfile 초기 생성
//        if (request.getRole() == Role.EXPERT) {
//            expertProfileRepository.save(ExpertProfile.builder()
//                    .member(member)
//                    .avgRating(BigDecimal.ZERO)
//                    .totalReviews(0)
//                    .isCertified(false)
//                    .build());
//            log.info("전문가 신청 완료 - memberId={}", member.getId());
//        }
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
}