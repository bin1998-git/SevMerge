package com.example.SevMerge.admin;

import com.example.SevMerge.board.*;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfileResponse;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberResponse;
import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.member.Role;
import com.example.SevMerge.project.ProjectResponeDTO;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final ProjectService projectService;
    private final BoardService boardService;
    private final BoardRepository boardRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/admin-dashboard";
    }

    @GetMapping("/admin/dashboard2")
    public String adminDashboard2() {
        return "admin/admin-dashboard2";
    }

    @GetMapping("/admin/main")
    public String dashboardPage(HttpSession session, Model model) {

        long newMemberCount = memberService.getNewMemberCountThisMonth();

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser);

        // 전체회원 몇명이고 이번달 몇명 회원가입했는지 보여주는 코드
        model.addAttribute("stats", memberService.getAllMembers());
        model.addAttribute("newMemberCount", newMemberCount);

        // 전체 프로젝트 보여주고 진행중 몇건인지 보여주는 코드
        model.addAttribute("projectCount", projectService.findAllProjects().size());
        model.addAttribute("activeProjectCount", projectService.getActiveProjectsCount());

        // 완료한 프로젝트 보여주고 이번달 몇번 완료했는지 보여주는 코드
        model.addAttribute("completedCount", projectService.getDoneProjectsCount());
        model.addAttribute("newCompletedCount", projectService.getMonthDoneProjectsCount());

        // 승인 대기 전문가 조회하는 코드
        model.addAttribute("pendingExpertCount", memberService.getPendingExpertCount());


        return "admin/admin-main";
    }

    // 관리자 공지사항 관리
    @GetMapping("/admin/notices")
    public String adminNotices(@RequestParam(value = "keyword", required = false) String keyword,
                               Model model, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);

        BoardType type = BoardType.NOTICE;

        List<BoardResponse.ListDTO> adminNotices = boardService.getAdminBoardsByType(type, keyword);

        model.addAttribute("boards", adminNotices);
        model.addAttribute("isFree", false);
        model.addAttribute("isNotice", true);
        model.addAttribute("isInquiry", false);
        model.addAttribute("boardType", "NOTICE");
        model.addAttribute("keyword", keyword != null ? keyword : "");

        return "admin/admin-notices";
    }

    // 관리자 공지사항 수정화면 띄우기
    @GetMapping("/admin/notices/{id}/update")
    public String updateNoticeForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        boolean isAdmin = sessionUser != null && sessionUser.getRole() == Role.ADMIN;

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + id));

        model.addAttribute("board", board);
        model.addAttribute("isNotice", true);
        model.addAttribute("isFree", false);
        model.addAttribute("isAdmin", isAdmin);

        return "admin/admin-noticeupdate";
    }

    // 관리자 공지사항 수정처리
    @Transactional
    @PostMapping("/admin/notices/{id}/update")
    public String updateNotice(@PathVariable("id") Long id,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content) {
        Board board = boardRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("해당 게시글이 없습니다. id =" + id));

        board.setTitle(title);
        board.setContent(content);

        return "redirect:/admin/notices";
    }

    // 전문가 승인 관리 페이지
    @GetMapping("/admin/experts")
    public String adminExpertPage(@RequestParam(value = "status", required = false) String status, Model model) {

        List<ExpertProfileResponse> expertsList;

        if ("PENDING".equals(status)) {

            expertsList = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.PENDING);
            model.addAttribute("isPending", true);
        } else if ("APPROVED".equals(status)) {
            expertsList = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.ACTIVE);
            model.addAttribute("isApproved", true);
        } else if ("REJECTED".equals(status)) {

            expertsList = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.REJECTED);
            model.addAttribute("isRejected", true);
        } else {

            expertsList = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.PENDING);
            model.addAttribute("isAll", true);
        }

        model.addAttribute("experts", expertsList);
        return "admin/admin-expert";
    }

    // 전문가 승인/거절
    @ResponseBody
    @PatchMapping("/api/admin/experts/{memberId}/approval")
    public ResponseEntity<?> processExpertApproval(
            @PathVariable Long memberId,
            @RequestBody Map<String, String> body) {

        String action = body.get("action");

        if ("APPROVE".equals(action)) {
            memberService.approveExpert(memberId);
        } else if ("REJECT".equals(action)) {
            String reason = body.get("reason"); // 거절사유 추가
            memberService.rejectExpert(memberId,reason);
        } else {
            throw new IllegalArgumentException("잘못된 요청 액션입니다.");
        }

        return ResponseEntity.ok().build();
    }

}
