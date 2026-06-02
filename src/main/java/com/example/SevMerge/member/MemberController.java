package com.example.SevMerge.member;

import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.board.BoardService;
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
    public String login(MemberRequest.Login request, HttpSession session) {
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
        model.addAttribute("member", memberService.getMyInfo(loginMember.getId()));
        model.addAttribute("isProjects", tab.equalsIgnoreCase("projects"));
        model.addAttribute("isBoards", tab.equalsIgnoreCase("boards"));
        model.addAttribute("isReviews", tab.equalsIgnoreCase("reviews"));
        model.addAttribute("isReviews", tab.equalsIgnoreCase("bids"));

        if (tab.equals("projects")) {
            model.addAttribute("projects", projectService.myProjects(loginMember));
        } else if (tab.equals("boards")) {
            model.addAttribute("boards", boardService.findAllByMyBoard(loginMember.getId()));
        } else if (tab.equals("reviews")) {
            model.addAttribute("reviews", projectService.myProjects(loginMember));
        } else if (tab.equals("bids")) {
            model.addAttribute("bid", projectService.myProjects(loginMember));
        }

        return "member/mypage";
    }

    @GetMapping("/mypage/update")
    public String updateMemberPage(){
        return "member/update-form";
    }

    @PostMapping("/mypage/update")
    public String updateMember(MemberRequest.Update request, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("sessionUser");
        memberService.updateMyInfo(loginMember.getId(), request);
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

    // 카카오 소셜 로그인 콜백
    @GetMapping("/kakao-redirect")
    public String kakaoRedirect(@RequestParam(name = "code") String code,
                                @RequestParam String state,
                                HttpSession session) {
        Member member = memberService.kakaoLogin(code, state);
        session.setAttribute("sessionUser", member);
        log.info("카카오 로그인 성공 - memberId={}", member.getId());
        return "redirect:/";
    }
}