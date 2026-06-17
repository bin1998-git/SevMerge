package com.example.SevMerge.project;

import com.example.SevMerge.bid.Bid;
import com.example.SevMerge.bid.BidRepository;
import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.Role;
import com.example.SevMerge.review.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ReviewService reviewService;

    // 프로젝트 등록 폼
    @GetMapping("/projects/save-form")
    public String saveForm() {
        log.info("project 등록폼 요청");
        return "project/project-save";
    }

    // 프로젝트 등록
    @PostMapping("/projects/save")
    public String save(ProjectRequestDTO.SaveDTO req, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login-form";
        req.validate();
        projectService.saveProject(req, sessionUser);
        return "redirect:/projects";
    }

    @GetMapping("/projects")
    public String list(Model model,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String statusFilter,
                       @RequestParam(required = false) String bidFilter,
                       HttpSession session) {

        // 1. [핵심] 빈 문자열("")로 들어오는 파라미터를 null로 변경 (에러 방지)
        if (keyword != null && keyword.isBlank()) keyword = null;
        if (category != null && category.isBlank()) category = null;
        if (statusFilter != null && statusFilter.isBlank()) statusFilter = null;
        if (bidFilter != null && bidFilter.isBlank()) bidFilter = null;

        // 2. 서비스 호출
        List<ProjectResponeDTO.ListDTO> projects =
                projectService.findByFilters(keyword, category, statusFilter, bidFilter);

        // 3. 모델 세팅 (기존과 동일)
        model.addAttribute("projects", projects);
        model.addAttribute("totalCount", projects.size());
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

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser != null) {
            model.addAttribute("sessionUser", sessionUser);
        }

        log.info("요청된 statusFilter 값: {}", statusFilter);

        return "project/project-list";
    }

    // 프로젝트 상세조회(id)
    @GetMapping("/projects/{id}")
    public String detail(@PathVariable("id") Long id, Model model, HttpSession session) {
        log.info("프로젝트 상세조회 요청 - projectId: {}", id);
        projectService.increase(id);

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        ProjectResponeDTO.DetailDTO project = projectService.findProjectById(id);
        model.addAttribute("project", project);


        // 일반 전문가도 빈 리스트나 전체 리스트를 안전하게 가져옵니다.
        List<Bid> bids = bidRepository.findByProjectId(id);
        model.addAttribute("bids", bids);

        // 로그인한 사용자가 프로젝트 작성자인지 확인
        boolean isOwner = sessionUser != null && sessionUser.getId().equals(project.getMemberId());
        model.addAttribute("isOwner", isOwner);

        // 총 제안서 개수는 의뢰인이거나, 비공개 프로젝트가 아닐 때만 정확하게 전달합니다.
        int bidCount = (bids != null) ? bids.size() : 0;
        model.addAttribute("bidCount", bidCount);

        // 낙찰된 전문가 카드 (CLOSED 상태일 때 안전하게 활성화)
        bidService.findSelectedBidByProjectId(id).ifPresent(bid -> {
            model.addAttribute("expertCard", true);
            model.addAttribute("expertName", bid.getExpert().getName());
            model.addAttribute("expertEmail", bid.getExpert().getEmail());
            model.addAttribute("taskTitle", project.getTitle());

            // 날짜 포맷팅 에러 완벽 방어
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
        ProjectResponeDTO.DetailDTO project = projectService.findProjectById(id);
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

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        req.validate();
        // 세션유저 검증이 필요없음으로 null
        projectService.updateProject(id, req, sessionUser);
        return ResponseEntity.ok().build();
    }

    // 프로젝트 삭제 (DELETE - JS 비동기)
    @DeleteMapping("/projects/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    HttpSession session) {
        log.info("project 삭제 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        projectService.deleteProject(id, sessionUser);
        return ResponseEntity.ok().build();
    }

    // 검토확인
    @PostMapping("/projects/{id}/done")
    public String done(@PathVariable Long id, HttpSession session) {
        log.info("project 검토 확인 요청");
        Member sessionUser = (Member)session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        projectService.doneProject(id, sessionUser);
        return "redirect:/my-pages?tab=projects";
    }


    // 프로젝트 임시저장(비동기)
    @PostMapping("/projects/draft")
    @ResponseBody
    public ResponseEntity<?> saveDraft(@RequestBody ProjectRequestDTO.UpdateDTO req, HttpSession session) {
        log.info("프로젝트 임시저장 요청");
        Member sessionUser = (Member)session.getAttribute(Define.SESSION_USER);
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
        Member sessionUser = (Member)session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        ProjectResponeDTO.DetailDTO dto = projectService.getMyDraft(sessionUser.getId());
        return ResponseEntity.ok(dto);
    }





    // 관리자용 프로젝트 관리 목록전체조회
    @GetMapping("/admin/projects")
    public String adminProjects(@RequestParam(value = "keyword", required = false) String keyword, HttpSession session, Model model) {
        Member sessionUser = (Member)session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);

        List<ProjectResponeDTO.ListDTO> adminProjects;

        if (keyword != null && !keyword.trim().isEmpty()) {
            adminProjects = projectService.findAdminProjectsByKeyword(keyword.trim());
        } else {
            adminProjects = projectService.findAllProjects();
        }

        model.addAttribute("projects", adminProjects);
        model.addAttribute("isFree", false);
        model.addAttribute("isNotice", false);
        model.addAttribute("isInquiry", false);
        model.addAttribute("keyword", keyword != null ? keyword : "");

        return "admin/admin-project";
    }

    // 관리자 전용 삭제
    @PostMapping("/admin/projects/{id}/delete")
    public String deleteProjectByAdmin(@PathVariable("id") Long id, HttpSession session) {
        Member sessionUser = (Member)session.getAttribute(Define.SESSION_USER);

        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        projectService.deleteProjectByAdmin(id);

        return "redirect:/admin/projects";

    }
}
