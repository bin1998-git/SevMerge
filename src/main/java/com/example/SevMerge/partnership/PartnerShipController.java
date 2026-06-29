package com.example.SevMerge.partnership;


import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.member.SessionUser;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PartnerShipController {

    private final PartnerShipService partnerShipService;
    private final MemberService memberService;
    private final ProjectService projectService;


@PostMapping("/partnership/inquiry")
    public String request(PartnerShipRequest request){
    partnerShipService.save(request);
    return "redirect:/supports";

}

    // 테스트
//    @GetMapping("/admin/TEST")
//    public String TESTPage(HttpSession session, Model model) {
//
//        long newMemberCount = memberService.getNewMemberCountThisMonth();
//        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
//        model.addAttribute("isAdmin", sessionUser);
//
//        // 전체회원 몇명이고 이번달 몇명 회원가입했는지 보여주는 코드
//        model.addAttribute("stats", memberService.getAllMembers());
//        model.addAttribute("newMemberCount", newMemberCount);
//
//        // 전체 프로젝트 보여주고 진행중 몇건인지 보여주는 코드
//        model.addAttribute("projectCount", projectService.findAllProjects().size());
//        model.addAttribute("activeProjectCount", projectService.getActiveProjectsCount());
//
//        // 완료한 프로젝트 보여주고 이번달 몇번 완료했는지 보여주는 코드
//        model.addAttribute("completedCount", projectService.getDoneProjectsCount());
//        model.addAttribute("newCompletedCount", projectService.getMonthDoneProjectsCount());
//
//        // 승인 대기 전문가 조회하는 코드
//        model.addAttribute("pendingExpertCount", memberService.getPendingExpertCount());
//
//        model.addAttribute("recentPartnerships",partnerShipService.list());
//
//        return "TEST/admin-main";
//    }


    // 관리자 페이지의 제휴 페이지
    @GetMapping("/admin/partnerships")
    public String partnershipsPage(@RequestParam(defaultValue = "1") int page,
                                   HttpSession session, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (!sessionUser.isAdmin()) throw new BadRequestException("관리자만 들어갈수 있습니다.");

        List<PartnerShipResponse> all = partnerShipService.list();
        int ps = 15, total = all.size(), tp = Math.max(1, (int) Math.ceil((double) total / ps));
        int s = (page - 1) * ps, e = Math.min(s + ps, total);
        model.addAttribute("partnerships", s < total ? all.subList(s, e) : new ArrayList<>());
        model.addAttribute("currentPage", page); model.addAttribute("totalPages", tp);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < tp ? page + 1 : null);
        return "admin/admin-partnership";
    }

    // 승인
    @PostMapping("/admin/partnerships/{partnerShipId}/approve")
    public String approvePartnership(HttpSession session, @PathVariable Long partnerShipId){
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        if(sessionUser == null){
            throw new BadRequestException("로그인먼저 해주세요");
        }
        if( !sessionUser.isAdmin()){
            throw new BadRequestException("관리자만 승인을 할수 있습니다.");
        }


        partnerShipService.findByIdAndApprove(partnerShipId);
        return "redirect:/admin/partnerships";
    }
    // 거절
    @PostMapping("/admin/partnerships/{partnerShipId}/reject")
    public String rejectPartnership(HttpSession session, @PathVariable Long partnerShipId){
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        if(sessionUser == null){
            throw new BadRequestException("로그인먼저 해주세요");
        }
        if( !sessionUser.isAdmin()){
            throw new BadRequestException("관리자만 거절 할수 있습니다.");
        }

        partnerShipService.findByIdAndReject(partnerShipId);

        return "redirect:/admin/partnerships";
    }

    // 삭제
    @PostMapping("/admin/partnerships/{partnerShipId}/delete")
    public String deletePartnership(HttpSession session, @PathVariable Long partnerShipId){

        SessionUser member = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (member == null){
            throw new BadRequestException("로그인 먼저 해주세요");
        }
        if(!member.isAdmin()){
            throw new BadRequestException("관리자만 삭제 가능");
        }
        partnerShipService.delete(partnerShipId);
        return "redirect:/admin/partnerships";
    }

}
