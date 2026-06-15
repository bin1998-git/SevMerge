package com.example.SevMerge.faq;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;
    private final MemberService memberService;


    // FAQ
    @GetMapping("/policys/faq")
    public String faq(Model model, HttpSession session){

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);

        boolean isAdmin = false;
        if (sessionMember != null) {
            Member memberEntity = memberService.findMemberById(sessionMember.getId());
            isAdmin = memberEntity.isAdmin();
        }

        model.addAttribute("faqList", faqService.findAll());
        model.addAttribute("isAdmin", isAdmin);
        return "footerProc/faq";
    }

    // FAQ 등록
    @PostMapping("/faqs/save")
    private String saveFaq(FaqRequest request, HttpSession session){

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        faqService.save(sessionUser, request);

        return "redirect:/policys/faq";
    }

    // FAQ 업데이트
    @PostMapping("/faqs/update/{faqId}")
    public String updateFaq(FaqRequest request, HttpSession session, @PathVariable("faqId") Long faqId){

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (!sessionUser.isAdmin()){
            throw new BadRequestException("관리자만 업데이트 가능");
        }
        // 해당 멤버가 관자인지 그리고 어떤 FAQ 를 업데이트 할지 그리고 내용입력 유효성검사
        faqService.update(faqId,sessionUser,request);
        return "redirect:/policys/faq";
    }

    // FAQ 삭제
    @PostMapping("faqs/delete/{faqId}")
    public String deleteFaq(HttpSession session, @PathVariable(name = "faqId") Long faqId){

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        // 여긴 member가 관리자인지 , 어떤 FAQ 삭제할지 넣음
        faqService.delete(sessionUser,faqId);
        return "redirect:/policys/faq";
    }
}
