package com.example.SevMerge.footer;


import com.example.SevMerge.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FooterController {

    private final MemberService memberService;

    // 고객센터
    @GetMapping("/supports")
    public String support(@RequestParam(required = false) String tab, Model model) {
        model.addAttribute("isSupport", tab == null);
        model.addAttribute("isAbout", "about".equals(tab));
        model.addAttribute("isCareers", "careers".equals(tab));
        model.addAttribute("isPartnership", "partnership".equals(tab));
        model.addAttribute("tabExpert", "expert".equals(tab));
        log.debug("support 탭 요청 - tab={}, isExpert={}", tab, "expert".equals(tab));
        return "footerProc/support";
    }
    // 이용약관
    @GetMapping("/policys/terms")
    public String terms(Model model) {
        model.addAttribute("isTerms", true);
        return "footerProc/terms";
    }
    // 개인정보 방침
    @GetMapping("/policys/privacy")
    public String privacy(Model model) {
        model.addAttribute("isPrivacy", true);
        return "footerProc/privacy";
    }
    // 광고방침
    @GetMapping("/policys/ad")
    public String ad(Model model) {
        model.addAttribute("isAd", true);
        return "footerProc/ad";
    }
    // 운영정책
    @GetMapping("/policys/operation")
    public String operation(Model model) {
        model.addAttribute("isOperation", true);
        return "footerProc/operation";
    }




}
