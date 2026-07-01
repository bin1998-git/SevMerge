package com.example.SevMerge.project;

import com.example.SevMerge.bid.BidRepository;
import com.example.SevMerge.bid.BidResponseDTO;
import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.Role;
import com.example.SevMerge.member.SessionUser;
import com.example.SevMerge.payment.PaymentResponse;
import com.example.SevMerge.payment.PaymentService;
import com.example.SevMerge.review.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final BidService bidService;
    private final BidRepository bidRepository;
    private final PaymentService paymentService;
    private final ReviewService reviewService;
    private final com.example.SevMerge.deliverable.DeliverableService deliverableService;
    private final MemberRepository memberRepository;

    // 프로젝트 등록 폼
    @GetMapping("/projects/save-form")
    public String saveForm() {
        log.info("project 등록폼 요청");
        return "project/project-save";
    }

    // 프로젝트 등록
    @PostMapping("/projects/save")
    public String save(ProjectRequestDTO.SaveDTO req, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login-form";
        req.validate();
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        projectService.saveProject(req, member);
        return "redirect:/projects";
    }

    @GetMapping("/projects")
    public String list(Model model,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String statusFilter,
                       @RequestParam(required = false) String bidFilter,
                       // 1. Pageable 추가: @PageableDefault로 기본값 설정
                       @PageableDefault(size = 6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                       HttpSession session) {

        // 2. 파라미터 null 처리
        if (keyword != null && keyword.isBlank()) keyword = null;
        if (category != null && category.isBlank()) category = null;
        if (statusFilter != null && statusFilter.isBlank()) statusFilter = null;
        if (bidFilter != null && bidFilter.isBlank()) bidFilter = null;

        // 3. 서비스 호출 (Page 객체 반환)
        Page<ProjectResponseDTO.ListDTO> projectPage =
                projectService.findByFilters(keyword, category, statusFilter, bidFilter, pageable);

        // 4. 모델 세팅 (핵심: .getContent()로 리스트 추출)
        model.addAttribute("projects", projectPage.getContent());
        model.addAttribute("page", projectPage); // 페이지 정보 전달
        model.addAttribute("totalCount", projectPage.getTotalElements()); // 전체 개수


        // 페이징
        model.addAttribute("currentPage", projectPage.getNumber());
        model.addAttribute("displayPage", projectPage.getNumber() + 1);
        model.addAttribute("totalPages", projectPage.getTotalPages() == 0 ? 1 : projectPage.getTotalPages());
        model.addAttribute("isFirst", projectPage.isFirst());
        model.addAttribute("isLast", projectPage.isLast());
        model.addAttribute("prevPage", projectPage.getNumber() - 1);
        model.addAttribute("nextPage", projectPage.getNumber() + 1);

        // 기존 필터 상태 모델들 그대로 유지
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("isAll", category == null && keyword == null && statusFilter == null && bidFilter == null);
        model.addAttribute("isClosedFilter", "CLOSED".equals(statusFilter));
        model.addAttribute("isWeb", "WEB".equals(category));
        model.addAttribute("isApp", "APP".equals(category));
        model.addAttribute("isUiux", "UI_UX".equals(category));
        model.addAttribute("isData", "DATA".equals(category));
        model.addAttribute("isVideo", "VIDEO".equals(category));
        model.addAttribute("isEtc", "ETC".equals(category));
        model.addAttribute("isCertifiedOnly", "CERTIFIED_ONLY".equals(bidFilter));

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser != null) {
            model.addAttribute("sessionUser", sessionUser);
        }

        return "project/project-list";
    }

    // 프로젝트 상세조회(id)
    @GetMapping("/projects/{id}")
    public String detail(@PathVariable("id") Long id, Model model, HttpSession session) {
        log.info("프로젝트 상세조회 요청 - projectId: {}", id);

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = sessionUser != null ?
                memberRepository.findById(sessionUser.getId()).orElse(null) : null;

        ProjectResponseDTO.DetailDTO project = projectService.findProjectById(id);
        model.addAttribute("project", project);

        List<BidResponseDTO.ListDTO> bids = bidService.findAllByProjectId(id, member);
        model.addAttribute("bids", bids);

        boolean isOwner = sessionUser != null && sessionUser.getId().equals(project.getMemberId());
        model.addAttribute("isOwner", isOwner);

        model.addAttribute("deliverables", deliverableService.getByProject(id));

        boolean hasAlreadyBid = false;
        if (member != null && member.isExpert()) {
            hasAlreadyBid = bidRepository.findByProjectIdAndExpertId(id, member.getId()).isPresent();
        }
        model.addAttribute("hasAlreadyBid", hasAlreadyBid);

        int bidCount = (bids != null) ? bids.size() : 0;
        model.addAttribute("bidCount", bidCount);

        bidService.findSelectedBidByProjectId(id).ifPresent(bid -> {
            model.addAttribute("expertCard", true);
            model.addAttribute("expertName", bid.getExpert().getName());
            model.addAttribute("expertEmail", bid.getExpert().getEmail());
            model.addAttribute("taskTitle", project.getTitle());

            if (bid.getCreatedAt() != null) {
                String startStr = bid.getCreatedAt().toString();
                model.addAttribute("startDate", startStr.length() >= 10 ? startStr.substring(0, 10) : startStr);
            }
            if (project.getDeadline() != null) {
                String deadlineStr = String.valueOf(project.getDeadline());
                model.addAttribute("endDate", deadlineStr.length() >= 10 ? deadlineStr.substring(0, 10) : deadlineStr);
            }
        });

        return "project/project-detail";
    }

    // 프로젝트 수정 폼
    @GetMapping("/projects/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {
        log.info("project 수정 폼 요청");

        // 데이터 조회
        ProjectResponseDTO.DetailDTO project = projectService.findProjectById(id);
        log.info("categoryName 확인: {}", project.getCategoryName()); // ← 추가
        model.addAttribute("project", project);
        model.addAttribute("isWeb", "WEB".equals(project.getCategoryName()));
        model.addAttribute("isApp", "APP".equals(project.getCategoryName()));
        model.addAttribute("isUiux", "UI_UX".equals(project.getCategoryName()));
        model.addAttribute("isData", "DATA".equals(project.getCategoryName()));
        model.addAttribute("isVideo", "VIDEO".equals(project.getCategoryName()));
        model.addAttribute("isEtc", "ETC".equals(project.getCategoryName()));
        return "project/project-update";
    }

    // 프로젝트 수정 (PUT - JS 비동기)
    @PutMapping("/projects/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ProjectRequestDTO.UpdateDTO req,
                                    HttpSession session
    ) {
        log.info("project 수정 요청");

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        req.validate();
        // 세션유저 검증이 필요없음으로 null
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        projectService.updateProject(id, req, member);
        return ResponseEntity.ok().build();
    }

    // 프로젝트 삭제 (DELETE - JS 비동기)
    @DeleteMapping("/projects/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    HttpSession session) {
        log.info("project 삭제 요청");
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        projectService.deleteProject(id, member);
        return ResponseEntity.ok().build();
    }

    // 검토확인
    @PostMapping("/projects/{id}/done")
    public String done(@PathVariable Long id, HttpSession session) {
        log.info("project 검토 확인 요청");
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            PaymentResponse payment = paymentService.getByProjectId(id);
            if (payment.isPaid()) {  // PAID 상태일 때만 정산 시도
                paymentService.settle(payment.getId(), sessionUser.getId());
            }
            if (!"DONE".equals(projectService.findProjectById(id).getProjectStatus())) {
                Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
                projectService.doneProject(id, member);
            }
        } catch (Exception e) {
            log.warn("결제 승인 처리 중 - {}", e.getMessage());
        }
        return "redirect:/my-pages?tab=projects&filter=done";
    }


    // 프로젝트 임시저장(비동기)
    @PostMapping("/projects/draft")
    @ResponseBody
    public ResponseEntity<?> saveDraft(@RequestBody ProjectRequestDTO.UpdateDTO req, HttpSession session) {
        log.info("프로젝트 임시저장 요청");
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        Long draftId = projectService.saveDraft(sessionUser.getId(), req);
        return ResponseEntity.ok(draftId);
    }

    // 임시저장 데이터 조회 (프로젝트 등록 시 호출)
    @GetMapping("/projects/draft")
    @ResponseBody
    public ResponseEntity<?> getDraft(HttpSession session) {
        log.info("project 임시저장 조회");
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        ProjectResponseDTO.DetailDTO dto = projectService.getMyDraft(sessionUser.getId());
        return ResponseEntity.ok(dto);
    }

    // 관리자용 프로젝트 관리 목록전체조회
    @GetMapping("/admin/projects")
    public String adminProjects(@RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(value = "statusFilter", defaultValue = "ALL") String statusFilter,
                                @RequestParam(defaultValue = "1") int page,
                                HttpSession session, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);

        String searchKeyword = (keyword != null) ? keyword.trim() : "";
        List<ProjectResponseDTO.ListDTO> all = projectService.getAdminProjectsByStatusAndKeyword(statusFilter, searchKeyword);
        int ps = 15, total = all.size(), tp = Math.max(1, (int) Math.ceil((double) total / ps));
        int s = (page - 1) * ps, e = Math.min(s + ps, total);
        model.addAttribute("projects", s < total ? all.subList(s, e) : new java.util.ArrayList<>());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tp);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < tp ? page + 1 : null);
        model.addAttribute("isFree", false);
        model.addAttribute("isNotice", false);
        model.addAttribute("isInquiry", false);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("isStatusAll", "ALL".equals(statusFilter));
        model.addAttribute("isStatusOpen", "OPEN".equals(statusFilter));
        model.addAttribute("isStatusInProgress", "IN_PROGRESS".equals(statusFilter));
        model.addAttribute("isStatusCompleted", "COMPLETED".equals(statusFilter));
        model.addAttribute("isStatusDone", "DONE".equals(statusFilter));
        model.addAttribute("isStatusClosed", "CLOSED".equals(statusFilter));
        model.addAttribute("isStatusCancelled", "CANCELLED".equals(statusFilter));
        model.addAttribute("isStatusDraft", "DRAFT".equals(statusFilter));
        return "admin/admin-project";
    }

    // 관리자 전용 삭제
    @PostMapping("/admin/projects/{id}/delete")
    public String deleteProjectByAdmin(@PathVariable("id") Long id, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);

        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        projectService.deleteProjectByAdmin(id);

        return "redirect:/admin/projects";

    }

    @PostMapping("/projects/{id}/skip-review")
    public String skipReview(@PathVariable Long id, HttpSession session) {
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return "redirect:/login";
        Member member = memberRepository.findById(loginMember.getId()).orElseThrow();
        projectService.skipReview(id, member);
        return "redirect:/my-pages?tab=projects";
    }
}
