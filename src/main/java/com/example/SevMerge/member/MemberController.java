package com.example.SevMerge.member;

import com.example.SevMerge.Report.BlackList;
import com.example.SevMerge.Report.BlacklistRepository;
import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.board.BoardService;
import com.example.SevMerge.charge.ChargeService;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.message.MessageRepository;
import com.example.SevMerge.message.MessageService;
import com.example.SevMerge.payment.PaymentService;
import com.example.SevMerge.portfolio.PortfolioService;
import com.example.SevMerge.project.ProjectResponeDTO;
import com.example.SevMerge.project.ProjectService;
import com.example.SevMerge.refund.RefundApplicationService;
import com.example.SevMerge.review.ReviewRepository;
import com.example.SevMerge.review.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Value("${oauth.google.client-id}")
    private String googleClientId;
    private final ProjectService projectService;
    private final ReviewService reviewService;
    private final BoardService boardService;
    private final BidService bidService;
    private final ReviewRepository reviewRepository;
    private final PortfolioService portfolioService;
    private final MessageService messageService;
    private final MessageRepository messageRepository;
    private final PaymentService paymentService;
    private final BlacklistRepository blacklistRepository;
    private final ChargeService chargeService;
    private final RefundApplicationService refundApplicationService;

    @GetMapping("/join-start")
    public String joinStart(Model model) {
        model.addAttribute("googleClientId", googleClientId);
        return "member/join-start";
    }

    // 일반 회원가입 - 역할 선택 화면
    @GetMapping("/join-role")
    public String joinRole() {
        return "member/join-role";
    }

    // 회원가입 role에 따라 의뢰인/전문가 폼 나눔
    @GetMapping("/join")
    public String joinForm(@RequestParam(required = false) String role, Model model) {
        if ("EXPERT".equals(role)) {
            return "member/join-form-expert";
        }
        // role 없거나 CLIENT면 의뢰인
        return "member/join-form-client";
    }

    @PostMapping("/join")
    public String join(MemberRequest.Join request,
                       @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile) {
        memberService.join(request, profileImageFile);

        if (request.getRole() != null && "EXPERT".equalsIgnoreCase(request.getRole().toString())) {
            return "redirect:/social-pending";
        }
        return "redirect:/login";
    }

    // 로그인 / 로그아웃
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("email", "");
        model.addAttribute("googleClientId", googleClientId);
        return "member/login-form";
    }

    @PostMapping("/login")
    public String login(MemberRequest.Login request, HttpSession session, Model model) {
        Member member = memberService.login(request, session);

        // 거절된 전문가는 안내 페이지로
        if (member.getStatus() == Status.REJECTED) {
            session.setAttribute("rejectedMemberId", member.getId());
            return "redirect:/expert-rejected";
        }

        // 3회 신고 누적으로 정지된 회원은 정지안내 페이지로 가게 만들기
        if (member.getStatus() == Status.SUSPENDED) {
            session.setAttribute("suspendedMemberId", member.getId());
            return "redirect:/banned-info";
        }

        if ("ADMIN".equals(String.valueOf(member.getRole()))) {
            return "redirect:/admin/main";
        }

        return "redirect:/exmain";
    }

    /**
     * [M2] Logout is restricted to POST only to prevent CSRF-via-GET attacks.
     * GET /logout now returns a redirect to a confirmation page or the home page,
     * without actually invalidating the session.
     *
     * The actual session invalidation is handled by Spring Security POST /logout
     * (configured in SecurityConfig). This GET mapping exists only to avoid 404
     * when users click a plain href="/logout" link; it safely redirects them home.
     */
    @GetMapping("/logout")
    public String logoutGet() {
        // [M2] Do NOT invalidate session via GET — return a safe redirect instead.
        // Real logout must go through POST /logout (Spring Security handles it).
        log.warn("[M2] GET /logout attempted - ignoring. Logout requires POST.");
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        memberService.logout(session);
        log.info("로그아웃완료");
        return "redirect:/";
    }

    // 정지화면 안내 페이지 조회
    @GetMapping("/banned-info")
    public String bannedInfoPage(Model model, HttpSession session) {
        Long suspendedMemberId = (Long) session.getAttribute("suspendedMemberId");
        if (suspendedMemberId == null) {
            return "redirect:/login";
        }

        BlackList latestBanLog = blacklistRepository.findFirstByMemberIdAndIsActiveTrueOrderByIdDesc(suspendedMemberId)
                .orElse(null);

        String banReason = (latestBanLog != null) ? latestBanLog.getReason() : "운영정책 위반으로 인한 정지";
        String expiredAt = (latestBanLog != null && latestBanLog.getExpiredAt() != null)
                ? latestBanLog.getExpiredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "기한 제한 없음";

        model.addAttribute("banReason", banReason);
        model.addAttribute("expiredAt", expiredAt);

        session.invalidate();
        return "member/banned";
    }

//    // 클라이언트 대시보드
//    @GetMapping("/clients/dashboard")
//    public String clientDashboard(HttpSession session, Model model) {
//        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
//        if (loginMember == null) return "redirect:/login";
//        if (loginMember.isExpert()) return "redirect:/experts/dashboard";
//
//        List<ProjectResponeDTO.ListDTO> myProjects = projectService.myProjects(loginMember);
//        long completedCount = myProjects.stream().filter(ProjectResponeDTO.ListDTO::isDone).count();
//        long activeCount    = myProjects.stream()
//                .filter(p -> "IN_PROGRESS".equals(p.getProjectStatus()) || "CLOSED".equals(p.getProjectStatus()))
//                .count();
//
//        model.addAttribute("projectCount",   myProjects.size());
//        model.addAttribute("completedCount", completedCount);
//        model.addAttribute("activeCount",    activeCount);
//        return "member/exclient-dashboard";
//    }

    // 마이페이지 (의뢰인 전용)
    @GetMapping("/my-pages")
    public String mypage(@RequestParam(required = false) String tab,
                         HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        // 세션 유저 방어(로그아웃 상태 시 로그인창으로)
        if (loginMember == null) {
            return "redirect:/login";
        }
        // 전문가는 마이페이지 대신 전문가 대시보드로
        if (loginMember.isExpert()) {
            return "redirect:/experts/dashboard";
        }

        if (tab == null) tab = "projects";

        model.addAttribute("member", memberService.getMyInfo(loginMember.getId()));
        model.addAttribute("isProjects", tab.equalsIgnoreCase("projects"));
        model.addAttribute("isBoards", tab.equalsIgnoreCase("boards"));
        model.addAttribute("isReviews", tab.equalsIgnoreCase("reviews"));
        model.addAttribute("isChats", tab.equalsIgnoreCase("chats"));
        model.addAttribute("isEdit", tab.equalsIgnoreCase("edit"));
        // 준비중 메뉴 (포인트 충전/출금/결제내역)
        model.addAttribute("isCharge", tab.equalsIgnoreCase("charge"));
        model.addAttribute("isWithdraw", tab.equalsIgnoreCase("withdraw"));
        model.addAttribute("isPayments", tab.equalsIgnoreCase("payments"));
        model.addAttribute("isChargeHistory", tab.equalsIgnoreCase("chargeHistory"));
        model.addAttribute("isRefundHistory", tab.equalsIgnoreCase("refundHistory"));
        // 통계 (등록 프로젝트 수, 완료 프로젝트 수)
        List<ProjectResponeDTO.ListDTO> myProjects = projectService.myProjects(loginMember);
        model.addAttribute("projectCount", myProjects.size());
        model.addAttribute("completedCount", myProjects.stream()
                .filter(ProjectResponeDTO.ListDTO::isDone).count());

        //  메시지 카운트
        long unreadMessageCount = messageRepository.countUnreadMessages(loginMember);
        model.addAttribute("messageCount", unreadMessageCount);
        model.addAttribute("isMessages", tab.equalsIgnoreCase("messages"));
        // 탭별 데이터
        if (tab.equals("projects")) {
            List<ProjectResponeDTO.ListDTO> projects = myProjects.stream()
                    .map(project -> {
                        if (project.getSelectedExpertId() != null) {
                            boolean hasReview = reviewRepository.existsByReviewerAndTargeterAndProject(
                                    loginMember.getId(), project.getSelectedExpertId(), project.getId()
                            );
                            project.setHasReview(hasReview);
                        }
                        return project;
                    })
                    .toList();
            model.addAttribute("projects", projects);
        } else if (tab.equals("boards")) {
            model.addAttribute("boards", boardService.findAllByMyBoard(loginMember.getId()));
        } else if (tab.equals("reviews")) {
            model.addAttribute("reviews", reviewService.findMySaveReviews(loginMember.getId()));
        } else if (tab.equals("edit")) {
            model.addAttribute("rawName", loginMember.getName());
            model.addAttribute("rawEmail", loginMember.getEmail());
        } else if (tab.equals("messages")) {
            model.addAttribute("messages",
                    messageService.findMessages(loginMember, "received", 1, "desc", null).getContent());
        } else if (tab.equals("payments")) {
            try {
                model.addAttribute("payments",
                        paymentService.getClientPayments(loginMember.getId()));
            } catch (Exception e) {
                log.warn("결제 내역 조회 실패 - {}", e.getMessage());
                model.addAttribute("payments", List.of());
            }
        } else if (tab.equals("chargeHistory")) {
            try {
                model.addAttribute("chargeHistories",
                        chargeService.getMyCharges(loginMember.getId()));
            } catch (Exception e) {
                log.warn("충전 내역 조회 실패 - {}", e.getMessage());
                model.addAttribute("chargeHistories", List.of());
            }
        } else if (tab.equals("refundHistory")) {
            try {
                model.addAttribute("refunds", refundApplicationService.getMyApplications(loginMember.getId()));
            } catch (Exception e) {
                log.warn("환불 내역 조회 실패 - {}", e.getMessage());
                model.addAttribute("refunds", List.of());
            }
        }

        return "member/client-mypage";
    }

    // 회원 탈퇴 (본인)
    @DeleteMapping("/my-pages/withdraw")
    @ResponseBody
    public ResponseEntity<?> withdrawMyAccount(HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return ResponseEntity.status(401).body("세션 만료");
        memberService.withdrawMember(loginMember.getId());
        session.invalidate();
        return ResponseEntity.ok().body("탈퇴 완료");
    }

    // 회원 정보 수정 페이지 이동 (GET)
    @GetMapping("/mypage/update") //
    public String updateMemberPage(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) {
            return "redirect:/login";
        }
        // 머스테치에 주입
        model.addAttribute("member", memberService.getMyInfo(loginMember.getId()));
        return "member/update-form";
    }

    // 회원 정보 수정 처리 (PUT)
    @PutMapping("/my-pages")
    @ResponseBody
    public ResponseEntity<?> updateMember(
            @RequestPart("data") MemberRequest.Update request,
            @RequestPart(value = "profileImageFile", required = false) MultipartFile profileImageFile,
            HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return ResponseEntity.status(401).body("세션 만료");

        try {
            memberService.updateMyInfo(loginMember.getId(), request, profileImageFile);
            session.setAttribute(Define.SESSION_USER, memberService.findMemberById(loginMember.getId()));
            return ResponseEntity.ok().body("정보 변경 완료");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 비밀번호 확인api
    @PostMapping("/api/member/verify-password")
    @ResponseBody
    public ResponseEntity<?> verifyCurrentPassword(@RequestBody Map<String, String> body, HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));

        String currentPassword = body.get("currentPassword");
        boolean matches = memberService.verifyPassword(loginMember.getId(), currentPassword);

        if (matches) {
            return ResponseEntity.ok().body(Map.of("message", "확인되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
        }
    }

    // 회원 목록
    @GetMapping("/members")
    public String memberList(Model model) {
        model.addAttribute("members", memberService.getPendingExperts());
        return "member/member-list";
    }

    // 관리자 - 회원 관리
    @GetMapping("/admin/members")
    public String adminMembers(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "sort", defaultValue = "desc") String sort,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               Model model) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, 10, Sort.by(direction, "createdAt"));
        Page<MemberResponse> memberPage = memberService.searchMembers(keyword, pageable);
        List<MemberResponse> content = new ArrayList<>(memberPage.getContent());


        int startNo = (page * 10) + 1;
        for (int i = 0; i < content.size(); i++) {
            content.get(i).setVirtualNo(startNo + i);
        }

        model.addAttribute("members", content);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("isAdmin", true);
        model.addAttribute("sort", sort);
        model.addAttribute("isNewSort","asc".equalsIgnoreCase(sort));

        int currentPage = memberPage.getNumber();
        int totalPages = memberPage.getTotalPages();
        int displayTotalPages = totalPages == 0 ? 1 : totalPages;

        model.addAttribute("displayPage", currentPage + 1);
        model.addAttribute("totalPages", displayTotalPages);
        model.addAttribute("totalElements", memberPage.getTotalElements());
        model.addAttribute("hasNext", memberPage.hasNext());
        model.addAttribute("hasPrev", memberPage.hasPrevious());
        model.addAttribute("prevPage", currentPage - 1);
        model.addAttribute("nextPage", currentPage + 1);

        return "admin/admin-member";
    }

    // 관리자 - 회원 삭제
    @PostMapping("/admin/members/{id}/delete")
    public String deleteMemberByAdmin(@PathVariable(name = "id") Long id,
                                      HttpSession session,
                                      Model model) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser.isAdmin());
        memberService.withdrawMember(id);
        return "redirect:/admin/members";
    }

    // 탈퇴처리 DELETE 전환
    @DeleteMapping("/admin/members/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        memberService.suspendMember(id);
        return ResponseEntity.ok().body("제재 처리가 성공했습니다.");
    }

    // --------------------------------------------------------------------

    // 구글 콜백: 기존 회원이면 바로 로그인, 신규면 역할 선택 화면으로
    @GetMapping("/google-redirect")
    public String googleRedirect(@RequestParam String code, HttpSession session) {

        MemberResponse.GoogleProfile profile = memberService.getGoogleProfile(code);
        String googleId = profile.getSub();
        String nickname = profile.getName();
        String email = profile.getEmail();

        Member existing = memberService.findGoogleMember(googleId);


        if (existing != null) {
            // 승인 대기 중인 전문가
            if (existing.getStatus() == Status.PENDING) {
                log.info("구글 PENDING 전문가 로그인 차단 - memberId={}", existing.getId());
                return "redirect:/social-pending";
            }
            // 정지된 계정
            if (existing.getStatus() == Status.SUSPENDED) {
                log.info("구글 정지 계정 로그인 차단 - memberId={}", existing.getId());
                return "redirect:/login";
            }

            if (existing.getStatus() == Status.REJECTED) {
                session.setAttribute("rejectedMemberId", existing.getId());
                return "redirect:/expert-rejected";
            }

            session.setAttribute(Define.SESSION_USER, existing);
            log.info("구글 기존 회원 로그인 - memberId={}", existing.getId());
            return "redirect:/";
        }

        // 신규 회원 -> 세션에 임시 보관 후 역할 선택 화면으로
        session.setAttribute("googleId", googleId);
        session.setAttribute("googleNickname", nickname);
        session.setAttribute("googleEmail", email);
        session.setAttribute("googleImage", profile.getPicture());
        return "redirect:/social-role";
    }

    // 카카오 콜백: 기존 회원이면 바로 로그인, 신규면 역할 선택 화면으로
    @GetMapping("/kakao-redirect")
    public String kakaoRedirect(@RequestParam String code,
                                HttpSession session, Model model) {

        MemberResponse.KakaoProfile profile = memberService.getKakaoProfile(code);
        Long kakaoId = profile.getId();
        String nickname = profile.getKakaoAccount().getProfile().getNickname() + "_" + kakaoId;
        String image = profile.getKakaoAccount().getProfile().getProfileImageUrl();

        Member existing = memberService.findKakaoMember(kakaoId);

        if (existing != null) {
            // 승인 대기 중인 전문가
            if (existing.getStatus() == Status.PENDING) {
                log.info("카카오 PENDING 전문가 로그인 차단 - memberId={}", existing.getId());
                return "redirect:/social-pending";
            }
            // 정지된 계정
            if (existing.getStatus() == Status.SUSPENDED) {
                log.info("카카오 정지 계정 로그인 차단 - memberId={}", existing.getId());
                return "redirect:/login";
            }

            if (existing.getStatus() == Status.REJECTED) {
                session.setAttribute("rejectedMemberId", existing.getId());
                return "redirect:/expert-rejected";
            }

            session.setAttribute(Define.SESSION_USER, existing);
            log.info("카카오 기존 회원 로그인 - memberId={}", existing.getId());
            return "redirect:/";
        }

        // 신규 회원 : 카카오 정보세션에서 잠깐 보관후 역할 선택 화면
        session.setAttribute("kakaoId", kakaoId);
        session.setAttribute("kakaoNickname", nickname);
        session.setAttribute("kakaoImage", image);
        return "redirect:/social-role";
    }

    // 역할 선택 화면
    @GetMapping("/social-role")
    public String socialRoleForm(HttpSession session) {
        // 카카오 ID도 없고 구글 ID도 세션에 없으면 차단
        if (session.getAttribute("kakaoId") == null && session.getAttribute("googleId") == null) {
            return "redirect:/login";
        }
        return "member/social-role";
    }

    // 3. 역할 선택 후 가입 처리 (카카오 + 구글 통합 분기 완료)
    @PostMapping("/social-join")
    public String socialJoin(@RequestParam String role, HttpSession session) {
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        String googleId = (String) session.getAttribute("googleId");

        // 둘 다 세션에 없으면 가입 불가
        if (kakaoId == null && googleId == null) {
            return "redirect:/login";
        }

        Member member = null;

        if (kakaoId != null) {
            // 카카오 가입 처리
            String nickname = (String) session.getAttribute("kakaoNickname");
            String image = (String) session.getAttribute("kakaoImage");
            member = memberService.registerKakaoMember(kakaoId, nickname, image, role);

            session.removeAttribute("kakaoId");
            session.removeAttribute("kakaoNickname");
            session.removeAttribute("kakaoImage");
            log.info("카카오 소셜 가입 완료 - memberId={}", member.getId());
        } else if (googleId != null) {
            // 구글 가입 처리 로직 (CustomSuccessHandler에서 세팅한 값을 꺼냄)
            String nickname = (String) session.getAttribute("googleNickname");
            String email = (String) session.getAttribute("googleEmail");
            String image = (String) session.getAttribute("googleImage");

            member = memberService.registerGoogleMember(googleId, nickname, email, image, role);

            session.removeAttribute("googleId");
            session.removeAttribute("googleNickname");
            session.removeAttribute("googleEmail");
            session.removeAttribute("googleImage");
            log.info("구글 소셜 가입 완료 - memberId={}", member.getId());
        }
        if (member != null) {
            Member freshMember = memberService.findMemberById(member.getId());
            session.setAttribute(Define.SESSION_USER, freshMember);
            log.info("소셜 회원가입 최종 완료 -> 온전한 세션 주입 완료 - memberId={}", freshMember.getId());
        }

        return "redirect:/";
    }


    // 전문가 추가정보 입력 화면
    @GetMapping("/social-expert-form")
    public String socialExpertForm(HttpSession session, Model model) {
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        String googleId = (String) session.getAttribute("googleId");

        // 소셜 인증 안 거치고 직접 들어오면 차단
        if (kakaoId == null && googleId == null) {
            return "redirect:/login";
        }

        if (kakaoId != null) {
            // 카카오: 닉네임 자동 채움, 이메일은 직접 입력
            model.addAttribute("name", session.getAttribute("kakaoNickname"));
            model.addAttribute("email", "");
            model.addAttribute("emailReadonly", false);
        } else {
            // 구글: 이름·이메일 자동 채움, 이메일 읽기전용
            model.addAttribute("name", session.getAttribute("googleNickname"));
            model.addAttribute("email", session.getAttribute("googleEmail"));
            model.addAttribute("emailReadonly", true);
        }

        return "member/expert-join";
    }

    // 전문가 추가정보 제출 -> PENDING 가입 (세션 주입 X = 로그인 안 됨)
    @PostMapping("/social-expert-join")
    public String socialExpertJoin(MemberRequest.ExpertJoin request, HttpSession session) {
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        String googleId = (String) session.getAttribute("googleId");

        if (kakaoId == null && googleId == null) {
            return "redirect:/login";
        }

        if (kakaoId != null) {
            String nickname = (String) session.getAttribute("kakaoNickname");
            String image = (String) session.getAttribute("kakaoImage");
            memberService.registerKakaoExpert(kakaoId, nickname, image, request);

            session.removeAttribute("kakaoId");
            session.removeAttribute("kakaoNickname");
            session.removeAttribute("kakaoImage");
            log.info("카카오 전문가 가입(PENDING) 완료");
        } else {
            String nickname = (String) session.getAttribute("googleNickname");
            String email = (String) session.getAttribute("googleEmail");
            String image = (String) session.getAttribute("googleImage");
            request.setEmail(email);  // disabled로 안 넘어온 이메일을 세션에서 보충
            memberService.registerGoogleExpert(googleId, nickname, email, image, request);

            session.removeAttribute("googleId");
            session.removeAttribute("googleNickname");
            session.removeAttribute("googleEmail");
            session.removeAttribute("googleImage");
            log.info("구글 전문가 가입(PENDING) 완료");
        }

        // 전문가는 세션에 안 넣고 승인 대기 안내 화면으로 보냄
        return "redirect:/social-pending";
    }

    @GetMapping("/expert-rejected")
    public String expertRejected(HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("rejectedMemberId");
        if (memberId == null) {
            return "redirect:/login";
        }
        String reason = memberService.getLatestRejectReason(memberId);
        model.addAttribute("reason", reason);
        return "member/expert-rejected";
    }

    // 재신청 폼 (기존 정보 채워서 보여줌)
    @GetMapping("/expert-reapply-form")
    public String expertReapplyForm(HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("rejectedMemberId");
        if (memberId == null) {
            return "redirect:/login";
        }
        model.addAttribute("profile", memberService.getExpertProfileForReapply(memberId));
        model.addAttribute("reason", memberService.getLatestRejectReason(memberId));
        return "member/expert-reapply-form";
    }

    // 재신청 처리 (정보 업데이트 + PENDING)
    @PostMapping("/expert-reapply")
    public String expertReapply(MemberRequest.ExpertJoin request, HttpSession session) {
        Long memberId = (Long) session.getAttribute("rejectedMemberId");
        if (memberId == null) {
            return "redirect:/login";
        }
        memberService.reapplyExpert(memberId, request);
        session.removeAttribute("rejectedMemberId");
        return "redirect:/social-pending";
    }

    // 승인 대기 안내 화면
    @GetMapping("/social-pending")
    public String socialPending() {
        return "member/expert-pending";
    }
}