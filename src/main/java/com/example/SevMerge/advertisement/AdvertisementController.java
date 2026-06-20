package com.example.SevMerge.advertisement;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * AdvertisementController
 *
 * [엔드포인트]
 * GET  /advertisements/form           — 전문가용 광고 구매 폼
 * POST /advertisements/purchase       — 광고 구매 처리
 * GET  /advertisements/my             — 전문가 본인 광고 내역
 */
@Controller
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    // 광고 구매 폼
    @GetMapping("/advertisements/form")
    public String purchaseForm(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return "redirect:/login";
        if (!loginMember.isExpert()) return "redirect:/";

        model.addAttribute("placements", AdvertisementPlacement.values());
        model.addAttribute("balance", loginMember.getBalance());
        return "advertisement/form";
    }

    // 광고 구매 처리
    @PostMapping("/advertisements/purchase")
    public String purchase(@RequestParam AdvertisementPlacement placement,
                           HttpSession session,
                           RedirectAttributes redirectAttrs) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return "redirect:/login";

        try {
            advertisementService.purchase(loginMember.getId(), placement);
            redirectAttrs.addFlashAttribute("successMsg", "광고가 등록되었습니다.");
            return "redirect:/advertisements/my";
        } catch (BadRequestException e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/advertisements/form";
        }
    }

    // 전문가 본인 광고 내역
    @GetMapping("/advertisements/my")
    public String myAds(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return "redirect:/login";

        List<AdvertisementResponse> ads = advertisementService.getMyAds(loginMember.getId());
        model.addAttribute("ads", ads);
        return "advertisement/my-ads";
    }
}