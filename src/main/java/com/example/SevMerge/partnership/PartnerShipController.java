package com.example.SevMerge.partnership;


import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
//        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
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

    @GetMapping("/admin/partnerships")
    public String partnershipsPage(HttpSession session, Model model) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        if(!sessionUser.isAdmin()){
            throw new BadRequestException("관리자만 들어갈수 있습니다.");
        }
        List<PartnerShipResponse> partnerShipResponseList = partnerShipService.list();

        model.addAttribute("partnerships",partnerShipResponseList);

        return "TEST/test";
    }

    // 승인
    @PostMapping("/admin/partnerships/{partnerShipId}/approve")
    public String approvePartnership(HttpSession session, @PathVariable Long partnerShipId){
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

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
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        if(sessionUser == null){
            throw new BadRequestException("로그인먼저 해주세요");
        }
        if( !sessionUser.isAdmin()){
            throw new BadRequestException("관리자만 승인을 할수 있습니다.");
        }
        partnerShipService.findByIdAndReject(partnerShipId);
        return "redirect:/admin/partnerships";
    }

    // 삭제
    @PostMapping("/admin/partnerships/{partnerShipId}/delete")
    public String deletePartnership(HttpSession session, @PathVariable Long partnerShipId){

        Member member = (Member) session.getAttribute(Define.SESSION_USER);
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
