package com.example.SevMerge.project;

import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
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

    // 프로젝트 등록 폼
    @GetMapping("/projects/save-form")
    public String saveForm() {
        log.info("project 등록폼 요청");
        return "project/project-save";
    }

    // 프로젝트 등록
    @PostMapping("/projects")
    public String save(ProjectRequestDTO.SaveDTO req, HttpSession session) {
        log.info("project 등록 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        req.validate();
        projectService.saveProject(req, sessionUser);
        return "redirect:/projects";
    }

    // 프로젝트 목록 조회 (client 용)
    @GetMapping("/projects")
    public String list(Model model,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String statusFilter,
                       @RequestParam(required = false) String bidFilter,
                       HttpSession session) {
        log.info("project 목록 조회 요청 - category: {}, statusFilter: {}", category, statusFilter);

        List<ProjectResponeDTO.ListDTO> projects;
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        log.info("bidFilter 값: {}", bidFilter);
        log.info("keyword: {}, category: {}, statusFilter: {}", keyword, category, statusFilter);


        // 조건문 분기 처리 ( 낙찰 완료 조건 추가)
        if (keyword != null && !keyword.isBlank()) {
            projects = projectService.findByKeyword(keyword);
        } else if (statusFilter != null && "CLOSED".equals(statusFilter)) {
            projects = projectService.findByStatusClosed();
        } else if (bidFilter != null && !bidFilter.isBlank()) {
            projects = projectService.findByBidFilter(bidFilter);
        } else if (category != null && !category.isBlank()) {
            projects = projectService.findByCategory(category);
        } else {
            projects = projectService.findAllProjects();
        }
        if (sessionUser != null) {
            model.addAttribute("sessionUser", sessionUser);
        }

        model.addAttribute("projects", projects);
        model.addAttribute("totalCount", projects.size());
        model.addAttribute("keyword", keyword != null ? keyword : "");

        // 카테고리 및 필터 탭 활성화 로직 업데이트
        // 다른 필터나 검색어가 없고, 낙찰완료 필터가 아닐때 활성화
        model.addAttribute("isAll", category == null && keyword == null && statusFilter == null && bidFilter == null);

        // 낙찰완료건
        model.addAttribute("isClosedFilter", "CLOSED".equals(statusFilter));
        model.addAttribute("isWeb", "WEB".equals(category));
        model.addAttribute("isApp", "APP".equals(category));
        model.addAttribute("isUiux", "UI_UX".equals(category));
        model.addAttribute("isData", "DATA".equals(category));
        model.addAttribute("isVideo", "VIDEO".equals(category));
        model.addAttribute("isEtc", "ETC".equals(category));
        model.addAttribute("isCertifiedOnly", "CERTIFIED_ONLY".equals(bidFilter));

        log.info("bidFilter 값: {}", bidFilter);
        return "project/project-list";
    }

    // 프로젝트 상세조회(id)
    @GetMapping("/projects/{id}")
    public String detail(@PathVariable("id") Long id, Model model, HttpSession session) {
        projectService.increase(id);
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        ProjectResponeDTO.DetailDTO project = projectService.findProjectById(id);
        model.addAttribute("project", project);
        // 로그인한 사용자가 프로젝트 작성자인지 확인
        boolean isOwner = sessionUser != null && sessionUser.getId().equals(project.getMemberId());

        // 로그인한 의뢰인일 때만 입찰 수 조회
        int bidCount = 0;
        if (sessionUser != null && sessionUser.isClient()) {
            bidCount = bidService.findByProjectId(id, sessionUser).size();
        }
        model.addAttribute("bidCount", bidCount);
        model.addAttribute("isOwner", isOwner);
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
                                    @RequestBody ProjectRequestDTO.UpdateDTO req
                                    ) {
        log.info("project 수정 요청");

        req.validate();
        // 세션유저 검증이 필요없음으로 null
        projectService.updateProject(id,req,null);
        return ResponseEntity.ok().build();
    }

    // 프로젝트 삭제 (DELETE - JS 비동기)
    @DeleteMapping("/projects/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.info("project 삭제 요청");

        projectService.deleteProject(id, null);
        return ResponseEntity.ok().build();
    }

    // 검토확인
    @PostMapping("/projects/{id}/done")
    public String done(@PathVariable Long id) {
        log.info("project 검토 확인 요청");

        projectService.doneProject(id, null);
        return "redirect:/mypage?tab=projects";
    }



}
