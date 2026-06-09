package com.example.SevMerge.admin;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfileResponse;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final ProjectService projectService;

    @GetMapping("/admin")
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
            memberService.rejectExpert(memberId);
        } else {
            throw new IllegalArgumentException("잘못된 요청 액션입니다.");
        }

        return ResponseEntity.ok().build();
    }

}
