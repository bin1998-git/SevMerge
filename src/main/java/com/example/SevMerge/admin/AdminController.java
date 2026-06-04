package com.example.SevMerge.admin;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
