package com.example.SevMerge.footer;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FooterController {

    // 고객센터
    @GetMapping("/support")
    public String support(@RequestParam(required = false) String tab, Model model) {
        model.addAttribute("isSupport", tab == null);
        model.addAttribute("isAbout", "about".equals(tab));
        model.addAttribute("isCareers", "careers".equals(tab));
        model.addAttribute("isPartnership", "partnership".equals(tab));
        return "footerProc/support";
    }
    // 이용약관
    @GetMapping("/policy/terms")
    public String terms(Model model) {
        model.addAttribute("isTerms", true);
        return "footerProc/terms";
    }
    // 개인정보 방침
    @GetMapping("/policy/privacy")
    public String privacy(Model model) {
        model.addAttribute("isPrivacy", true);
        return "footerProc/privacy";
    }
    // 광고방침
    @GetMapping("/policy/ad")
    public String ad(Model model) {
        model.addAttribute("isAd", true);
        return "footerProc/ad";
    }
    // 운영정책
    @GetMapping("/policy/operation")
    public String operation(Model model) {
        model.addAttribute("isOperation", true);
        return "footerProc/operation";
    }

}
