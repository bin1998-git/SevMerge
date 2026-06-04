package com.example.SevMerge.member;

import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.board.BoardService;
import com.example.SevMerge.portfolio.PortfolioService;
import com.example.SevMerge.project.ProjectService;
import com.example.SevMerge.review.Review;
import com.example.SevMerge.review.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ProjectService projectService;
    private final ReviewService reviewService;
    private final BoardService boardService;
    private final BidService bidService;
    private final PortfolioService portfolioService;

    // 회원가입
    @GetMapping("/join")
    public String joinForm() {
        return "member/join-form";
    }

    @PostMapping("/join")
    public String join(MemberRequest.Join request) {
        memberService.join(request);
        return "redirect:/login";
    }

    // 로그인 / 로그아웃
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("email", "");
        return "member/login-form";
    }

    @PostMapping("/login")
    public String login(MemberRequest.Login request, HttpSession session, Model model) {
        memberService.login(request, session);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        memberService.logout(session);
        log.info("로그아웃완료");
        return "redirect:/";
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String mypage(@RequestParam(defaultValue = "projects") String tab,
                         HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("sessionUser");
        // 세션 유저 방어(로그아웃 상태 시 로그인창으로)
        if (loginMember == null) {
            return "redirect:/login";
        }
        model.addAttribute("member", memberService.getMyInfo(loginMember.getId()));
        model.addAttribute("isProjects", tab.equalsIgnoreCase("projects"));
        model.addAttribute("isBoards", tab.equalsIgnoreCase("boards"));
        model.addAttribute("isReviews", tab.equalsIgnoreCase("reviews"));
        model.addAttribute("isBids", tab.equalsIgnoreCase("bids"));
        model.addAttribute("isPortfolio", tab.equalsIgnoreCase("portfolios"));
        model.addAttribute("projectCount",projectService.myProjects(loginMember).size());
        if(loginMember.getRole() == Role.EXPERT) {
            model.addAttribute("bidCount",bidService.findMyBids(loginMember).size());
        }
        if (tab.equals("projects")) {
            model.addAttribute("projects", projectService.myProjects(loginMember));
        } else if (tab.equals("boards")) {
            model.addAttribute("boards", boardService.findAllByMyBoard(loginMember.getId()));
        } else if (tab.equals("reviews")) {
            model.addAttribute("reviews", reviewService.findMyReviews(loginMember.getId()));
        } else if (tab.equals("bids")) {
            model.addAttribute("bids", bidService.findMyBids(loginMember));
        } else if (tab.equals("portfolios")) {
            model.addAttribute("portfolios", portfolioService.findByMemberId(loginMember.getId()));
        }

        return "member/mypage";
    }

    // 회원 정보 수정 페이지 이동 (GET)
    @GetMapping("/mypage/update") //
    public String updateMemberPage(HttpSession session, Model model){
        Member loginMember = (Member) session.getAttribute("sessionUser");
        if (loginMember == null) {
            return "redirect:/login";
        }
        // 머스테치에 주입
        model.addAttribute("member", memberService.getMyInfo(loginMember.getId()));
        return "member/update-form";
    }

    // 회원 정보 수정 처리 (POST)
    @PostMapping("/mypage/update")
    public String updateMember(MemberRequest.Update request, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("sessionUser");
        if (loginMember == null) {
            return "redirect:/login";
        }

        // 1. DB 회원 정보 수정 요청
        memberService.updateMyInfo(loginMember.getId(), request);

        Member updatedMember = memberService.findMemberById(loginMember.getId());
        session.setAttribute("sessionUser", updatedMember);

        log.info("회원 정보 수정 완료 - memberId={}", loginMember.getId());
        return "redirect:/mypage";
    }

    // 회원 목록
    @GetMapping("/members")
    public String memberList(Model model) {
        model.addAttribute("members", memberService.getPendingExperts());
        return "member/member-list";
    }

    // 관리자 - 회원 관리
    @GetMapping("/admin/members")
    public String adminMembers(Model model) {
        model.addAttribute("members", memberService.getPendingExperts());
        return "admin/admin-members";
    }

    @PostMapping("/admin/members/{id}/delete")
    public String deleteMember(@PathVariable Long id) {
        memberService.suspendMember(id);
        return "redirect:/admin/members";
    }

    // 관리자 - 전문가 승인
    @GetMapping("/admin/experts")
    public String adminExperts(Model model) {
        model.addAttribute("experts", memberService.getPendingExperts());
        return "admin/admin-expert";
    }

    @PostMapping("/admin/experts/{id}/approve")
    public String approveExpert(@PathVariable Long id) {
        memberService.approveExpert(id);
        return "redirect:/admin/experts";
    }

    @PostMapping("/admin/experts/{id}/reject")
    public String rejectExpert(@PathVariable Long id) {
        memberService.rejectExpert(id);
        return "redirect:/admin/experts";
    }

    // --------------------------------------------------------------------

    // 카카오 콜백: 기존 회원이면 바로 로그인, 신규면 역할 선택 화면으로
    @GetMapping("/kakao-redirect")
    public String kakaoRedirect(@RequestParam String code,
                                HttpSession session, Model model) {

        MemberResponse.KakaoProfile profile = memberService.getKakaoProfile(code);
        Long kakaoId = profile.getId();
        String nickname = profile.getKakaoAccount().getProfile().getNickname() + "_" + kakaoId;

        Member existing = memberService.findKakaoMember(kakaoId);

        if (existing != null) {
            // 기존 회원 → 바로 로그인
            session.setAttribute("sessionUser", existing);
            log.info("카카오 기존 회원 로그인 - memberId={}", existing.getId());
            return "redirect:/";
        }

        // 신규 회원 → 카카오 정보 잠깐 세션에 보관하고 역할 선택 화면으로
        session.setAttribute("kakaoId", kakaoId);
        session.setAttribute("kakaoNickname", nickname);
        return "redirect:/kakao-role";
    }

    // 역할 선택 화면
    @GetMapping("/kakao-role")
    public String kakaoRoleForm(HttpSession session) {
        // 카카오 인증 안 거치고 직접 들어오면 차단
        if (session.getAttribute("kakaoId") == null) {
            return "redirect:/login";
        }
        return "member/kakao-role";
    }

    // 역할 선택 후 가입 처리
    @PostMapping("/kakao-join")
    public String kakaoJoin(@RequestParam String role, HttpSession session) {
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        String nickname = (String) session.getAttribute("kakaoNickname");

        if (kakaoId == null) {
            return "redirect:/login";
        }

        Member member = memberService.registerKakaoMember(kakaoId, nickname, role);
        session.setAttribute("sessionUser", member);

        // 임시 보관한 카카오 정보 정리
        session.removeAttribute("kakaoId");
        session.removeAttribute("kakaoNickname");

        log.info("카카오 신규 가입+로그인 - memberId={}", member.getId());
        return "redirect:/";
    }
}