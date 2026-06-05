package com.example.SevMerge.expertprofile;

import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ExpertProfile 뷰 컨트롤러 (Mustache 렌더링 전용)
 *
 * [엔드포인트 목록]
 * GET /experts          - 전문가 목록 페이지
 * GET /experts/{id}     - 전문가 프로필 상세 페이지
 * GET /experts/my/form  - 내 프로필 등록/수정 폼
 */
@Controller
@RequestMapping("/experts")
@RequiredArgsConstructor
public class ExpertProfileViewController {

    private final ExpertProfileService expertProfileService;

    /**
     * 전문가 목록 페이지
     * GET /experts
     * → templates/expertProfile/expertProfile-list.mustache
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("expertProfiles", expertProfileService.getAll());
        return "expertProfile/expertProfile-list";
    }

    /**
     * 전문가 프로필 상세 페이지
     * GET /experts/{memberId}
     * → templates/expertProfile/expertProfile-detail.mustache
     */
    @GetMapping("/{memberId}")
    public String detail(@PathVariable Long memberId,
                         HttpSession session,
                         Model model) {

        ExpertProfileResponse profile = expertProfileService.getByMemberId(memberId);
        model.addAttribute("expertProfile", profile);

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        boolean isOwner = sessionUser != null && sessionUser.getId().equals(memberId);
        model.addAttribute("isOwner", isOwner);

        return "expertProfile/expertProfile-detail";
    }

    /**
     * 전문가 대시보드
     * GET /experts/dashboard
     * → templates/expert-dashboard.mustache
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Member sessionUser = (Member) session.getAttribute("sessionUser");
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";
        return "expert-dashboard";
    }

    /**
     * 내 프로필 등록/수정 폼
     * GET /experts/my/form
     * → templates/expertProfile/expertProfile-form.mustache
     */
    @GetMapping("/my/form")
    public String form(HttpSession session, Model model) {
        Member sessionUser = (Member) session.getAttribute("sessionUser");

        // 기존 프로필이 있으면 데이터 채워서 수정 폼으로, 없으면 빈 등록 폼으로
        try {
            ExpertProfileResponse profile = expertProfileService.getByMemberId(sessionUser.getId());
            model.addAttribute("expertProfile", profile);
            model.addAttribute("isUpdate", true);
        } catch (Exception e) {
            model.addAttribute("isUpdate", false);
        }

        return "expertProfile/expertProfile-form";
    }
}
