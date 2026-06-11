package com.example.SevMerge.expertprofile;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.portfolio.PortfolioResponse;
import com.example.SevMerge.portfolio.PortfolioService;
import com.example.SevMerge.project.ProjectResponeDTO;
import com.example.SevMerge.project.ProjectService;
import com.example.SevMerge.review.Review;
import com.example.SevMerge.review.ReviewResponse;
import com.example.SevMerge.review.ReviewService;
import com.example.SevMerge.member.Role;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * ExpertProfile 뷰 컨트롤러 (Mustache 렌더링 전용)
 *
 * [엔드포인트 목록]
 * GET /experts          - 전문가 목록 페이지
 * GET /experts/{id}     - 전문가 프로필 상세 페이지
 * GET /experts/my/form  - 내 프로필 등록/수정 폼
 */
@Slf4j
@Controller
@RequestMapping("/experts")
@RequiredArgsConstructor
public class ExpertProfileViewController {

    private final ExpertProfileService expertProfileService;
    private final PortfolioService portfolioService;
    private final ReviewService reviewService;
    private final ProjectService projectService;

    /**
     * 전문가 목록 페이지
     * GET /experts
     * → templates/expertProfile/expertProfile-list.mustache
     */
    @GetMapping
    public String list(HttpSession session ,Model model) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("expertProfiles", expertProfileService.getAll());
        model.addAttribute("doneProject",projectService.getDoneProjectsCount());
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);

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

        model.addAttribute("avgRating", String.format("%.1f", reviewService.avgRating(memberId)));
        model.addAttribute("reviews",reviewService.findMyReviews(memberId));
        model.addAttribute("doneProject",projectService.getDoneProjectsCount());
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);

        return "expertProfile/expertProfile-detail";
    }

    /**
     * 전문가 대시보드
     * GET /experts/dashboard?keyword=&category=
     * → templates/expertProfile/expert-dashboard.mustache
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            HttpSession session, Model model) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) {
            throw new BadRequestException("전문가만 이용할수 있습니다.");
        }

        // 전문가 프로필 (없으면 빈 채로)
        try {
            ExpertProfileResponse profile = expertProfileService.getByMemberId(sessionUser.getId());
            model.addAttribute("expertProfile", profile);
        } catch (Exception e) {
            // 프로필 미등록 상태일 수 있음
        }

        // 프로젝트 목록 — keyword/category 조건에 따라 분기
        List<ProjectResponeDTO.ListDTO> projects;
        if (keyword != null && !keyword.isBlank()) {
            projects = projectService.findByKeyword(keyword);
        } else if (category != null && !category.isBlank()) {
            projects = projectService.findByCategory(category);
        } else {
            projects = projectService.findAllProjects();
        }

        // 최신 6건만 대시보드 미니 그리드에 표시
        List<ProjectResponeDTO.ListDTO> recentProjects = projects.stream()
                .limit(6)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("projects", recentProjects);
        model.addAttribute("totalProjectCount", projects.size());
        model.addAttribute("keyword", keyword != null ? keyword : "");

        // 카테고리 탭 활성화 플래그
        model.addAttribute("isAll",   category == null && (keyword == null || keyword.isBlank()));
        model.addAttribute("isWeb",   "WEB".equals(category));
        model.addAttribute("isApp",   "APP".equals(category));
        model.addAttribute("isUiux",  "UI_UX".equals(category));
        model.addAttribute("isData",  "DATA".equals(category));
        model.addAttribute("isVideo", "VIDEO".equals(category));
        model.addAttribute("isEtc",   "ETC".equals(category));

        return "expertProfile/expert-dashboard";
    }

    /**
     * 내 프로필 등록/수정 폼
     * GET /experts/my/form
     * → templates/expertProfile/expertProfile-form.mustache
     */
    @GetMapping("/my/form")
    public String form(HttpSession session, Model model) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

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

    /**
     * 내 프로필 등록/수정 처리
     * POST /experts/my/form
     * → 성공 시 대시보드로 리다이렉트
     */
    @PostMapping("/my/form")
    public String submitForm(ExpertProfileRequest.SaveRequest req, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        boolean hasProfile = false;
        try {
            expertProfileService.getByMemberId(sessionUser.getId());
            hasProfile = true;
        } catch (Exception e) {
            // 프로필 없음
        }

        if (hasProfile) {
            expertProfileService.update(sessionUser.getId(), req);
        } else {
            expertProfileService.save(sessionUser, req);
        }

        return "redirect:/experts/dashboard";
    }
}
