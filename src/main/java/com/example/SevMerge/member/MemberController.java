package com.example.SevMerge.member;

import com.example.SevMerge.Report.BlackList;
import com.example.SevMerge.Report.BlacklistRepository;
import com.example.SevMerge.adbid.AdBidService;
import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.board.BoardService;
import com.example.SevMerge.bookmark.BookMarkService;
import com.example.SevMerge.cancelrequest.CancelRequestService;
import com.example.SevMerge.charge.ChargeService;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertwish.ExpertWishService;
import com.example.SevMerge.message.MessageRepository;
import com.example.SevMerge.message.MessageService;
import com.example.SevMerge.payment.PaymentService;
import com.example.SevMerge.portfolio.PortfolioService;
import com.example.SevMerge.project.ProjectResponseDTO;
import com.example.SevMerge.project.ProjectService;
import com.example.SevMerge.refund.RefundApplicationService;
import com.example.SevMerge.review.ReviewRepository;
import com.example.SevMerge.review.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final ExpertWishService expertWishService;

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
    private final BookMarkService bookMarkService;
    private final CancelRequestService cancelRequestService;
    private final AdBidService adBidService;

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
                       @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile,
                       Model model) {
        try {
            memberService.join(request, profileImageFile);
        } catch (BadRequestException e) {
            model.addAttribute("errorMessage", e.getMessage());
            if (request.getRole() == Role.EXPERT) {
                return "member/join-form-expert";
            }
            return "member/join-form-client";
        }

        if (request.getRole() != null && "EXPERT".equalsIgnoreCase(request.getRole().toString())) {
            return "redirect:/social-pending";
        }
        return "redirect:/login";
    }

    // 로그인/ 로그아웃
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (loginMember != null) {
            return "redirect:/main";
        }
        model.addAttribute("email", "");
        model.addAttribute("googleClientId", googleClientId);
        return "member/login-form";
    }

    @PostMapping("/login")
    public String login(MemberRequest.Login request, HttpSession session, Model model) {
        try {
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
            return "redirect:/main";
        } catch (BadRequestException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("googleClientId", googleClientId);
            return "member/login-form";
        }
    }

    @PostMapping("/api/member/check-email")
    @ResponseBody
    public ResponseEntity<?> checkEmailDuplicate(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || !email.contains("@"))
            return ResponseEntity.badRequest().body(Map.of("available", false, "message", "올바른 이메일 형식이 아닙니다."));
        boolean exists = memberRepository.existsByEmail(email);
        if (exists) return ResponseEntity.ok(Map.of("available", false, "message", "이미 사용 중인 이메일입니다."));
        return ResponseEntity.ok(Map.of("available", true, "message", "사용 가능한 이메일입니다."));
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        memberService.logout(session);
        log.info("로그아웃완료");
        return "redirect:/";
    }

    // 비밀번호/이메일 찾기 페이지
    @GetMapping("/find-account")
    public String findAccountPage() {
        return "member/find-account";
    }

    // 이메일로 회원 존재 확인 (비밀번호 찾기용)
    @PostMapping("/api/member/find-by-phone")
    @ResponseBody
    public ResponseEntity<?> findByPhone(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String name = body.get("name");
        boolean exists = memberService.existsByNameAndPhone(name, phone);
        if (exists) return ResponseEntity.ok(Map.of("exists", true));
        return ResponseEntity.ok(Map.of("exists", false));
    }

    // 임시 비밀번호 발급
    @PostMapping("/api/member/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String name = body.get("name");
        try {
            String tempPw = memberService.resetPasswordByPhone(name, phone);
            return ResponseEntity.ok(Map.of("message", "임시 비밀번호가 문자로 발송되었습니다."));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 이메일 찾기
    @PostMapping("/api/member/find-email")
    @ResponseBody
    public ResponseEntity<?> findEmail(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String name = body.get("name");
        try {
            String email = memberService.findEmailByNameAndPhone(name, phone);
            return ResponseEntity.ok(Map.of("email", email));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
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


    // 마이페이지 (의뢰인 전용)
    @GetMapping("/my-pages")
    public String mypage(@RequestParam(required = false) String tab,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(defaultValue = "1") int page,
                         HttpSession session, Model model,
                         HttpServletRequest request) {
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        // 세션 유저 방어(로그아웃 상태 시 로그인창으로)
        if (loginMember == null) {
            return "redirect:/login";
        }
        // 전문가는 마이페이지 대신 전문가 대시보드로
        if (loginMember.isExpert()) {
            return "redirect:/experts/dashboard";
        }

        Member member = memberRepository.findById(loginMember.getId()).orElseThrow();

        model.addAttribute("isDashboard", true);
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
        List<ProjectResponseDTO.ListDTO> myProjects = projectService.myProjects(member);
        List<ProjectResponseDTO.ListDTO> visibleProjects = myProjects.stream()
                .filter(p -> !p.isDone())
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("projectCount", visibleProjects.size());
        model.addAttribute("inProgressCount", visibleProjects.stream().filter(p -> p.isClosed()).count());
        model.addAttribute("completedCount", myProjects.stream().filter(p -> p.isDone()).count());
        long proposalCount = 0L;
        try {
            proposalCount = bidService.findBidsForClient(member).size();
        } catch (Exception ignore) {
        }
        model.addAttribute("proposalCount", proposalCount);

        //  메시지 카운트
        long unreadMessageCount = messageRepository.countUnreadMessages(member);
        model.addAttribute("messageCount", unreadMessageCount);
        model.addAttribute("isMessages", tab.equalsIgnoreCase("messages"));
        // 북마크
        model.addAttribute("isBookmarks", tab.equalsIgnoreCase("bookmarks"));
        model.addAttribute("isWishlist", tab.equalsIgnoreCase("wishlist"));
        model.addAttribute("isBids", tab.equalsIgnoreCase("bids"));

        List<Map<String, Object>> auctionAds = adBidService.getApprovedWinnerBids().stream()
                .filter(b -> b.getBannerImage() != null)
                .map(b -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("expertId", b.getExpertId());
                    map.put("bannerImage", "/images/" + b.getBannerImage());
                    return map;
                }).toList();
        model.addAttribute("auctionAds", auctionAds);
        model.addAttribute("hasAuctionAds", !auctionAds.isEmpty());
        // 탭별 데이터
        final int MY_PAGE_SIZE = 10;
        if (tab.equals("projects")) {
            String projectFilter = request.getParameter("filter");
            List<ProjectResponseDTO.ListDTO> base;
            if ("inprogress".equals(projectFilter)) {
                base = visibleProjects.stream().filter(p -> p.isClosed()).collect(java.util.stream.Collectors.toList());
                model.addAttribute("projectsTitle", "진행중인 프로젝트");
                model.addAttribute("projectsDesc", "낙찰되어 진행 중인 프로젝트예요.");
            } else if ("done".equals(projectFilter)) {
                base = myProjects.stream().filter(p -> p.isDone()).collect(java.util.stream.Collectors.toList());
                model.addAttribute("projectsTitle", "완료된 프로젝트");
                model.addAttribute("projectsDesc", "정산까지 완료된 프로젝트예요.");
            } else {
                base = visibleProjects;
                projectFilter = "";
                model.addAttribute("projectsTitle", "내 프로젝트");
                model.addAttribute("projectsDesc", "내가 등록한 프로젝트를 관리해요.");
            }
            model.addAttribute("projectFilter", projectFilter == null ? "" : projectFilter);
            List<ProjectResponseDTO.ListDTO> all = base.stream()
                    .map(project -> {
                        project.setBidCount((int) bidService.countByProjectId(project.getId()));
                        if (project.isClosed()) {
                            project.setCancelPending(cancelRequestService.hasPendingRequest(project.getId()));
                        }
                        if (project.getSelectedExpertId() != null) {
                            boolean hasReview = reviewRepository.existsByReviewerAndTargeterAndProject(
                                    loginMember.getId(), project.getSelectedExpertId(), project.getId()
                            );
                            project.setHasReview(hasReview);
                        }
                        project.setReviewSkipped(projectService.isReviewSkipped(project.getId()));
                        return project;
                    })
                    .toList();
            int tp = Math.max(1, (int) Math.ceil((double) all.size() / MY_PAGE_SIZE));
            int s = (page - 1) * MY_PAGE_SIZE, e = Math.min(s + MY_PAGE_SIZE, all.size());
            model.addAttribute("projects", s < all.size() ? all.subList(s, e) : List.of());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", tp);
            model.addAttribute("prevPage", page > 1 ? page - 1 : null);
            model.addAttribute("nextPage", page < tp ? page + 1 : null);
        } else if (tab.equals("boards")) {
            List<?> all = boardService.findAllByMyBoard(loginMember.getId());
            int tp = Math.max(1, (int) Math.ceil((double) all.size() / MY_PAGE_SIZE));
            int s = (page - 1) * MY_PAGE_SIZE, e = Math.min(s + MY_PAGE_SIZE, all.size());
            model.addAttribute("boards", s < all.size() ? all.subList(s, e) : List.of());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", tp);
            model.addAttribute("prevPage", page > 1 ? page - 1 : null);
            model.addAttribute("nextPage", page < tp ? page + 1 : null);
        } else if (tab.equals("reviews")) {
            List<?> all = reviewService.findMySaveReviews(loginMember.getId());
            int tp = Math.max(1, (int) Math.ceil((double) all.size() / MY_PAGE_SIZE));
            int s = (page - 1) * MY_PAGE_SIZE, e = Math.min(s + MY_PAGE_SIZE, all.size());
            model.addAttribute("reviews", s < all.size() ? all.subList(s, e) : List.of());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", tp);
            model.addAttribute("prevPage", page > 1 ? page - 1 : null);
            model.addAttribute("nextPage", page < tp ? page + 1 : null);
        } else if (tab.equals("edit")) {
            model.addAttribute("rawName", loginMember.getName());
            model.addAttribute("rawEmail", loginMember.getEmail());
        } else if (tab.equals("messages")) {
            Page<?> msgPage = messageService.findMessages(member, "received", page, "desc", null);
            int tp = msgPage.getTotalPages() == 0 ? 1 : msgPage.getTotalPages();
            model.addAttribute("messages", msgPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", tp);
            model.addAttribute("prevPage", page > 1 ? page - 1 : null);
            model.addAttribute("nextPage", page < tp ? page + 1 : null);
        } else if (tab.equals("payments")) {
            try {
                List<?> all = paymentService.getClientPayments(loginMember.getId());
                int tp = Math.max(1, (int) Math.ceil((double) all.size() / MY_PAGE_SIZE));
                int s = (page - 1) * MY_PAGE_SIZE, e2 = Math.min(s + MY_PAGE_SIZE, all.size());
                model.addAttribute("payments", s < all.size() ? all.subList(s, e2) : List.of());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", tp);
                model.addAttribute("prevPage", page > 1 ? page - 1 : null);
                model.addAttribute("nextPage", page < tp ? page + 1 : null);
            } catch (Exception ex) {
                log.warn("결제 내역 조회 실패 - {}", ex.getMessage());
                model.addAttribute("payments", List.of());
            }
        } else if (tab.equals("chargeHistory")) {
            try {
                List<?> all = chargeService.getMyCharges(loginMember.getId());
                int tp = Math.max(1, (int) Math.ceil((double) all.size() / MY_PAGE_SIZE));
                int s = (page - 1) * MY_PAGE_SIZE, e2 = Math.min(s + MY_PAGE_SIZE, all.size());
                model.addAttribute("chargeHistories", s < all.size() ? all.subList(s, e2) : List.of());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", tp);
                model.addAttribute("prevPage", page > 1 ? page - 1 : null);
                model.addAttribute("nextPage", page < tp ? page + 1 : null);
            } catch (Exception ex) {
                log.warn("충전 내역 조회 실패 - {}", ex.getMessage());
                model.addAttribute("chargeHistories", List.of());
            }
        } else if (tab.equals("refundHistory")) {
            try {
                List<?> all = refundApplicationService.getMyApplications(loginMember.getId());
                int tp = Math.max(1, (int) Math.ceil((double) all.size() / MY_PAGE_SIZE));
                int s = (page - 1) * MY_PAGE_SIZE, e2 = Math.min(s + MY_PAGE_SIZE, all.size());
                model.addAttribute("refunds", s < all.size() ? all.subList(s, e2) : List.of());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", tp);
                model.addAttribute("prevPage", page > 1 ? page - 1 : null);
                model.addAttribute("nextPage", page < tp ? page + 1 : null);
            } catch (Exception ex) {
                log.warn("환불 내역 조회 실패 - {}", ex.getMessage());
                model.addAttribute("refunds", List.of());
            }
        } else if (tab.equals("bookmarks")) {
            List<?> all;
            if (keyword != null && !keyword.isBlank()) {
                all = bookMarkService.filterBookMarks(loginMember.getId(), keyword);
            } else {
                all = bookMarkService.findAllMyBookMarks(loginMember.getId());
            }
            int tp = Math.max(1, (int) Math.ceil((double) all.size() / MY_PAGE_SIZE));
            int s = (page - 1) * MY_PAGE_SIZE, e2 = Math.min(s + MY_PAGE_SIZE, all.size());
            model.addAttribute("bookMarks", s < all.size() ? all.subList(s, e2) : List.of());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", tp);
            model.addAttribute("prevPage", page > 1 ? page - 1 : null);
            model.addAttribute("nextPage", page < tp ? page + 1 : null);
            model.addAttribute("keyword", keyword);
        } else if (tab.equals("wishlist")) {
            int wishPage = 0;
            try {
                String wishPageParam = request.getParameter("wishPage");
                if (wishPageParam != null) wishPage = Integer.parseInt(wishPageParam);
            } catch (NumberFormatException ignored) {
            }

            Pageable wishPageable = PageRequest.of(wishPage, 10);
            Page<com.example.SevMerge.expertwish.ExpertWish> wishResult;

            if (keyword != null && !keyword.isBlank()) {
                wishResult = expertWishService.filterWishesPaged(loginMember.getId(), keyword, wishPageable);
            } else {
                wishResult = expertWishService.findMyWishesPaged(loginMember.getId(), wishPageable);
            }
            model.addAttribute("wishes", wishResult.getContent());
            model.addAttribute("keyword", keyword);
            model.addAttribute("wishCurrentPage", wishPage + 1);  // 1-based 표시용
            model.addAttribute("wishTotalPages", wishResult.getTotalPages() == 0 ? 1 : wishResult.getTotalPages());
            model.addAttribute("wishHasPrev", wishResult.hasPrevious());
            model.addAttribute("wishHasNext", wishResult.hasNext());
            model.addAttribute("wishPrevPage", wishPage - 1);
            model.addAttribute("wishNextPage", wishPage + 1);
            model.addAttribute("wishTotalElements", wishResult.getTotalElements());
        } else if (tab.equals("bids")) {
            try {
                List<?> all = bidService.findBidsForClient(member);
                int tp = Math.max(1, (int) Math.ceil((double) all.size() / MY_PAGE_SIZE));
                int s = (page - 1) * MY_PAGE_SIZE, e2 = Math.min(s + MY_PAGE_SIZE, all.size());
                model.addAttribute("clientBids", s < all.size() ? all.subList(s, e2) : List.of());
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", tp);
                model.addAttribute("prevPage", page > 1 ? page - 1 : null);
                model.addAttribute("nextPage", page < tp ? page + 1 : null);
            } catch (Exception ex) {
                log.warn("제안서 목록 조회 실패 - {}", ex.getMessage());
                model.addAttribute("clientBids", List.of());
            }
        }
        return "member/client-mypage";
    }

    // 회원 탈퇴 (본인)
    @DeleteMapping("/my-pages/withdraw")
    @ResponseBody
    public ResponseEntity<?> withdrawMyAccount(HttpSession session) {
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return ResponseEntity.status(401).body("세션 만료");
        try {
            memberService.withdrawMember(loginMember.getId());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        session.invalidate();
        return ResponseEntity.ok().body("탈퇴 완료");
    }

    // 회원 정보 수정 페이지 이동 (GET)
    @GetMapping("/mypage/update") //
    public String updateMemberPage(HttpSession session, Model model) {
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
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
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return ResponseEntity.status(401).body("세션 만료");

        try {
            memberService.updateMyInfo(loginMember.getId(), request, profileImageFile);
            session.setAttribute(Define.SESSION_USER, new SessionUser(memberService.findMemberById(loginMember.getId())));
            return ResponseEntity.ok().body("정보 변경 완료");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/my-pages/profile-image")
    @ResponseBody
    public ResponseEntity<?> deleteProfileImage(HttpSession session) {
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return ResponseEntity.status(401).body("세션 만료");
        memberService.deleteProfileImage(loginMember.getId());
        session.setAttribute(Define.SESSION_USER, new SessionUser(memberService.findMemberById(loginMember.getId())));
        return ResponseEntity.ok().body("삭제 완료");
    }

    // 비밀번호 확인api
    @PostMapping("/api/member/verify-password")
    @ResponseBody
    public ResponseEntity<?> verifyCurrentPassword(@RequestBody Map<String, String> body, HttpSession session) {
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
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
                               @RequestParam(value = "roleFilter", defaultValue = "ALL") String roleFilter, // 1. 권한 파라미터 추가
                               Model model) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, 10, Sort.by(direction, "createdAt"));

        // 2. 권한 필터(ALL 여부)를 포함하여 회원 목록 페이징 조회
        Page<MemberResponse> memberPage;
        if ("ALL".equals(roleFilter)) {
            memberPage = memberService.searchMembersOrderByCreatedAt(keyword, pageable);
        } else {
            memberPage = memberService.searchMembersByRoleAndKeyword(roleFilter, keyword, pageable);
        }

        List<MemberResponse> content = memberPage.getContent();

        int startNo = (page * 10) + 1;
        for (int i = 0; i < content.size(); i++) {
            content.get(i).setVirtualNo(startNo + i);
        }

        // HTML에 적용된 템플릿 변수명이 stats일 수 있으므로 둘 다 넘겨주거나 맞춰주어야 합니다.
        model.addAttribute("members", content);
        model.addAttribute("stats", content); // HTML 반복문 이름이 stats면 이 줄이 필요합니다.

        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("isAdmin", true);
        model.addAttribute("sort", sort);
        model.addAttribute("isNewSort", "asc".equalsIgnoreCase(sort));

        // 3. Mustache select 태그의 selected 유지를 위한 상태값 전달
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("isRoleAll", "ALL".equals(roleFilter));
        model.addAttribute("isRoleClient", "CLIENT".equals(roleFilter));
        model.addAttribute("isRoleExpert", "EXPERT".equals(roleFilter));
        model.addAttribute("isRoleAdmin", "ADMIN".equals(roleFilter));

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
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
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

            session.setAttribute(Define.SESSION_USER, new SessionUser(existing));
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

            session.setAttribute(Define.SESSION_USER, new SessionUser(existing));
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
            session.setAttribute(Define.SESSION_USER, new SessionUser(freshMember));
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