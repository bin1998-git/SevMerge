package com.example.SevMerge.faq;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;


    // FAQ
    @GetMapping("/policys/faq")
    public String faq(Model model, HttpSession session) {

        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);

        boolean isAdmin = false;
        if (sessionMember != null) {
            Member memberEntity = memberService.findMemberById(sessionMember.getId());
            isAdmin = memberEntity.isAdmin();
        }

        model.addAttribute("faqList", faqService.findAll());
        model.addAttribute("isAdmin", isAdmin);
        return "faq/faq";
    }

    // 등록 페이지
    @GetMapping("/faqs/save")
    public String saveFaqPage(HttpSession session) {
        SessionUser memberEntity = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (memberEntity == null || !memberEntity.isAdmin()) {
            throw new BadRequestException("관리자만 FAQ등록 가능 합니다");
        }

        return "faq/saveFaq";

    }

    // FAQ 등록
    @PostMapping("/faqs/save")
    private String saveFaq(FaqRequest request, HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        faqService.save(member, request);

        return "redirect:/policys/faq";
    }

    // 수정 페이지
    @GetMapping("/faqs/edit/{faqId}")
    public String editFaqPage(HttpSession session, @PathVariable(name = "faqId") Long faqId, Model model) {
        SessionUser memberEntity = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (memberEntity == null || !memberEntity.isAdmin()) {
            throw new BadRequestException("관리자만 수정할수 있습니다");
        }
        Faq faq = faqService.findById(faqId);
        model.addAttribute("faq", faq);
        return "faq/editFaq";
    }

    // FAQ 수정
    @PostMapping("/faqs/update/{faqId}")
    public String updateFaq(FaqRequest request, HttpSession session, @PathVariable("faqId") Long faqId) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (!sessionUser.isAdmin()) {
            throw new BadRequestException("관리자만 업데이트 가능");
        }
        // 해당 멤버가 관자인지 그리고 어떤 FAQ 를 업데이트 할지 그리고 내용입력 유효성검사
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        faqService.update(faqId, member, request);
        return "redirect:/policys/faq";
    }

    // FAQ 삭제
    @PostMapping("faqs/delete/{faqId}")
    public String deleteFaq(HttpSession session, @PathVariable(name = "faqId") Long faqId) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        // 여긴 member가 관리자인지 , 어떤 FAQ 삭제할지 넣음
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        faqService.delete(member, faqId);
        return "redirect:/policys/faq";
    }
}
