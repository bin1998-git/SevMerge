package com.example.SevMerge.expertprofile;

import com.example.SevMerge.bookmark.BookMarkService;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertwish.ExpertWishRepository;
import com.example.SevMerge.expertwish.ExpertWishService;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.ProjectResponseDTO;
import com.example.SevMerge.member.MemberRequest;
import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.project.ProjectService;
import com.example.SevMerge.review.ReviewService;
import com.example.SevMerge.member.Role;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final ReviewService reviewService;
    private final ProjectService projectService;
    private final ExpertWishRepository expertWishRepository;
    private final ExpertWishService expertWishService;
    private final MemberService memberService;
    private final BookMarkService bookMarkService;
    /**
     * 전문가 목록 페이지
     * GET /experts
     * → templates/expertProfile/expertProfile-list.mustache
     */
    @GetMapping
    public String list(HttpSession session ,Model model) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        List<ExpertProfileResponse> profiles = expertProfileService.getAll(); // 전체 전문가 가져오기
        if (sessionUser != null) {
            for (ExpertProfileResponse profile : profiles) { // 전체 전문가 리스트
                // 의뢰인이 전문가 북마크 했는지 확인 여부
                boolean currentStatus = bookMarkService.isBookmarked(sessionUser.getId(), profile.getId());
                profile.setBookmarked(currentStatus); // 체크했으면 true 로 셋팅
            }
        }
        model.addAttribute("expertProfiles", profiles); // true 셋팅된 북마크 리스트
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

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        // 추가된 찜여부 확인
        boolean isWished = false;
        if (sessionUser != null) {
            isWished = expertWishRepository.existsByMemberIdAndExpertId(sessionUser.getId(), memberId);
            boolean currentStatus = bookMarkService.isBookmarked(sessionUser.getId(), profile.getId());
            profile.setBookmarked(currentStatus);
        }


        model.addAttribute("isWished", isWished);

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
        if (!sessionUser.isExpert()) return "redirect:/";

        // 전문가 프로필 (없으면 빈 채로)
        try {
            ExpertProfileResponse profile = expertProfileService.getByMemberId(sessionUser.getId());
            model.addAttribute("expertProfile", profile);
        } catch (Exception e) {
            // 프로필 미등록 상태일 수 있음
        }

        // 프로젝트 목록 — keyword/category 조건에 따라 분기
        List<ProjectResponseDTO.ListDTO> projects;
        if (keyword != null && !keyword.isBlank()) {
            projects = projectService.findByKeyword(keyword);
        } else if (category != null && !category.isBlank()) {
            projects = projectService.findByCategory(category);
        } else {
            projects = projectService.findAllProjects();
        }

        // 최신 6건만 대시보드 미니 그리드에 표시
        List<ProjectResponseDTO.ListDTO> recentProjects = projects.stream()
                .limit(6)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("projects", recentProjects);
        model.addAttribute("totalProjectCount", projects.size());
        model.addAttribute("keyword", keyword != null ? keyword : "");

        // 카테고리 탭 활성화 플래그
        model.addAttribute("reviews",reviewService.findMyReviews(sessionUser.getId()));
        Double avg = reviewService.avgRating(sessionUser.getId());
        model.addAttribute("avgRating", avg != null ? String.format("%.1f", avg) : "0.0");
        model.addAttribute("isAll",   category == null && (keyword == null || keyword.isBlank()));
        model.addAttribute("isWeb",   "WEB".equals(category));
        model.addAttribute("isApp",   "APP".equals(category));
        model.addAttribute("isUiux",  "UI_UX".equals(category));
        model.addAttribute("isData",  "DATA".equals(category));
        model.addAttribute("isVideo", "VIDEO".equals(category));
        model.addAttribute("isEtc",   "ETC".equals(category));

        return "expertProfile/expert-dashboard";
    }

    // ──────────────────────────────────────────────────
    // 전문가 프로필 수정 (GET /experts/my/form)
    // ──────────────────────────────────────────────────
    @GetMapping("/my/form")
    public String form(HttpSession session, Model model) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        Long memberId = sessionUser.getId();
        if (expertProfileService.existsByMemberId(memberId)) {
            model.addAttribute("expertProfile", expertProfileService.getByMemberId(memberId));
            model.addAttribute("isUpdate", true);
        } else {
            model.addAttribute("isUpdate", false);
        }
        return "expertProfile/expertProfile-form";
    }

    // ──────────────────────────────────────────────────
    // 전문가 프로필 수정 처리 (POST /experts/my/form)
    // ──────────────────────────────────────────────────
    @PostMapping("/my/form")
    public String submitForm(@ModelAttribute ExpertProfileRequest.SaveRequest req,
                             HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        Long memberId = sessionUser.getId();
        if (expertProfileService.existsByMemberId(memberId)) {
            expertProfileService.update(memberId, req);
        } else {
            expertProfileService.save(sessionUser, req);
        }
        return "redirect:/experts/dashboard";
    }

    // ──────────────────────────────────────────────────
    // 개인정보 수정 폼 (GET /experts/my/info-edit)
    // ──────────────────────────────────────────────────
    @GetMapping("/my/info-edit")
    public String infoEditForm(HttpSession session, Model model) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        model.addAttribute("member", memberService.getMyInfo(sessionUser.getId()));
        return "expertProfile/expert-info-edit";
    }

    // ──────────────────────────────────────────────────
    // 개인정보 수정 처리 (POST /experts/my/info-edit)
    // ──────────────────────────────────────────────────
    @PostMapping("/my/info-edit")
    public String infoEditSubmit(@ModelAttribute MemberRequest.Update req,
                                 HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        try {
            memberService.updateMyInfo(sessionUser.getId(), req, null);
            session.setAttribute(Define.SESSION_USER,
                    memberService.findMemberById(sessionUser.getId()));
        } catch (BadRequestException e) {
            log.warn("개인정보 수정 실패 (memberId={}): {}", sessionUser.getId(), e.getMessage());
        }
        return "redirect:/experts/dashboard";
    }


    // POST /experts/wish/{expertId}

    @PostMapping("/wish/{expertId}")
    @ResponseBody
    public ResponseEntity<?> toggleWish(@PathVariable Long expertId, HttpSession session) {
        // 1. 세션에서 로그인한 유저 확인
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        // 2. 로그인되지 않았다면 401 Unauthorized 응답 반환
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 3. 서비스 호출
        expertWishService.toggleWish(sessionUser.getId(), expertId);

        // 4. 성공 응답 반환
        return ResponseEntity.ok("success");
    }
}
