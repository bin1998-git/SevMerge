package com.example.SevMerge.expertprofile;

import com.example.SevMerge.bookmark.BookMarkService;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertwish.ExpertWishRepository;
import com.example.SevMerge.expertwish.ExpertWishService;
import com.example.SevMerge.member.*;
import com.example.SevMerge.project.ProjectResponseDTO;
import com.example.SevMerge.project.ProjectService;
import com.example.SevMerge.review.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
import com.example.SevMerge.member.SessionUser;

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
    private final MemberRepository memberRepository;
    /**
     * 전문가 목록 페이지
     * GET /experts
     * → templates/expertProfile/expertProfile-list.mustache
     */
    @GetMapping
    public String list(@RequestParam(required = false) String career,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") int page,
                       HttpSession session, Model model) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        List<ExpertProfileResponse> profiles = expertProfileService.getAll();

        // career(기술 키워드) 필터링
        if (career != null && !career.isBlank()) {
            final String careerLower = career.toLowerCase();
            profiles = profiles.stream()
                    .filter(p -> p.getSpeciality() != null &&
                            p.getSpeciality().toLowerCase().contains(careerLower))
                    .collect(java.util.stream.Collectors.toList());
        }

        // keyword 검색
        if (keyword != null && !keyword.isBlank()) {
            final String kw = keyword.toLowerCase();
            profiles = profiles.stream()
                    .filter(p -> (p.getMemberName() != null && p.getMemberName().toLowerCase().contains(kw))
                            || (p.getSpeciality() != null && p.getSpeciality().toLowerCase().contains(kw))
                            || (p.getIntro() != null && p.getIntro().toLowerCase().contains(kw)))
                    .collect(java.util.stream.Collectors.toList());
        }

        if (sessionUser != null) {
            for (ExpertProfileResponse profile : profiles) {
                boolean currentStatus = bookMarkService.isBookmarked(sessionUser.getId(), profile.getId());
                profile.setBookmarked(currentStatus);
            }
        }

        // 인-메모리 페이징
        int pageSize = 9;
        int totalCount = profiles.size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (totalPages == 0) totalPages = 1;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalCount);
        java.util.List<ExpertProfileResponse> pagedProfiles = start < totalCount ? profiles.subList(start, end) : new java.util.ArrayList<>();

        model.addAttribute("expertProfiles", pagedProfiles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < totalPages ? page + 1 : null);
        model.addAttribute("doneProject", projectService.getDoneProjectsCount());
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);

        // 탭 활성화
        model.addAttribute("isAll", career == null && keyword == null);
        model.addAttribute("isFullstack", "java".equalsIgnoreCase(career));
        model.addAttribute("isBackend", "spring".equalsIgnoreCase(career));
        model.addAttribute("isFrontend", "react".equalsIgnoreCase(career));
        model.addAttribute("isApp", "flutter".equalsIgnoreCase(career));
        model.addAttribute("isUiux", "figma".equalsIgnoreCase(career));
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("currentCareer", career != null ? career : "");

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

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

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

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        model.addAttribute("isDashboard", true);

        // 전문가 프로필 (없으면 빈 채로)
        try {
            ExpertProfileResponse profile = expertProfileService.getByMemberId(sessionUser.getId());
            model.addAttribute("expertProfile", profile);
        } catch (Exception e) {
            // 프로필 미등록 상태일 수 있음
        }

        // 프로젝트 목록 — keyword/category 조건에 따라 분기
        List<ProjectResponseDTO.ListDTO> projects;
        // 2. 서비스 메서드들을 페이징 처리된 결과를 반환하도록 수정하거나,
//    Pageable.unpaged()를 넣어 호출하세요.
        if (keyword != null && !keyword.isBlank()) {
            // keyword 검색 결과가 Page라면 .getContent()로 List 추출
            projects = projectService.findByKeyword(keyword, Pageable.unpaged());
        } else if (category != null && !category.isBlank()) {
            // category 검색 결과가 Page라면 .getContent()로 List 추출
            projects = projectService.findByCategory(category, Pageable.unpaged()).getContent();
        } else {
            // 전체 조회도 Page를 반환한다면 .getContent()로 List 추출
            projects = projectService.findAllProjects(Pageable.unpaged());
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
        model.addAttribute("wishCount", expertWishRepository.countByExpertId(sessionUser.getId()));
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
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        Long memberId = sessionUser.getId();
        if (expertProfileService.existsByMemberId(memberId)) {
            model.addAttribute("expertProfile", expertProfileService.getByMemberId(memberId));
            model.addAttribute("isUpdate", true);
        } else {
            model.addAttribute("isUpdate", false);
        }
        model.addAttribute("isDashboard", true);
        return "expertProfile/expertProfile-form";
    }

    // ──────────────────────────────────────────────────
    // 전문가 프로필 수정 처리 (POST /experts/my/form)
    // ──────────────────────────────────────────────────
    @PostMapping("/my/form")
    public String submitForm(@ModelAttribute ExpertProfileRequest.SaveRequest req,
                             HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        Long memberId = sessionUser.getId();
        if (expertProfileService.existsByMemberId(memberId)) {
            expertProfileService.update(memberId, req);
        } else {
            Member member = memberRepository.findById(sessionUser.getId())
                    .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
            expertProfileService.save(member, req);
        }
        return "redirect:/experts/dashboard";
    }

    // ──────────────────────────────────────────────────
    // 개인정보 수정 폼 (GET /experts/my/info-edit)
    // ──────────────────────────────────────────────────
    @GetMapping("/my/info-edit")
    public String infoEditForm(HttpSession session, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        model.addAttribute("member", memberService.getMyInfo(sessionUser.getId()));
        model.addAttribute("isDashboard", true);
        return "expertProfile/expert-info-edit";
    }

    // ──────────────────────────────────────────────────
    // 개인정보 수정 처리 (POST /experts/my/info-edit)
    // ──────────────────────────────────────────────────
    @PostMapping("/my/info-edit")
    public String infoEditSubmit(@ModelAttribute MemberRequest.Update req,
                                 HttpSession session,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        try {
            memberService.updateMyInfo(sessionUser.getId(), req, null);
            session.setAttribute(Define.SESSION_USER,
                    memberService.findMemberById(sessionUser.getId()));
            redirectAttrs.addFlashAttribute("successMessage", "정보가 수정되었습니다.");
        } catch (BadRequestException e) {
            log.warn("개인정보 수정 실패 (memberId={}): {}", sessionUser.getId(), e.getMessage());
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/experts/my/info-edit";
        }
        return "redirect:/experts/dashboard";
    }


    // POST /experts/wish/{expertId}

    @PostMapping("/wish/{expertId}")
    @ResponseBody
    public ResponseEntity<?> toggleWish(@PathVariable Long expertId, HttpSession session) {
        // 1. 세션에서 로그인한 유저 확인
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

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
