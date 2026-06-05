package com.example.SevMerge.project;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // 프로젝트 등록 폼
    @GetMapping("/projects/save-form")
    public String saveForm() {
        log.info("project 등록폼 요청");
        return "project/project-save";
    }

    // 프로젝트 등록
    @PostMapping("/projects/save")
    public String save(ProjectRequestDTO.SaveDTO req, HttpSession session) {
        log.info("project 등록 요청");
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        req.validate();
        projectService.saveProject(req, sessionUser);
        return "redirect:/projects/list";
    }

    // 프로젝트 목록 조회
    @GetMapping("/projects/list")
    public String list(Model model,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String statusFilter,
                       @RequestParam(required = false) String bidFilter,
                       HttpSession session) {
        log.info("project 목록 조회 요청 - category: {}, statusFilter: {}", category, statusFilter);

        List<ProjectResponeDTO.ListDTO> projects;

        Member sessionUser = (Member)session.getAttribute(Define.SESSION_USER);

        log.info("bidFilter 값: {}", bidFilter);
        log.info("keyword: {}, category: {}, statusFilter: {}", keyword, category, statusFilter);


        // 조건문 분기 처리 ( 낙찰 완료 조건 추가)
        if (keyword != null && !keyword.isBlank()) {
            projects = projectService.findByKeyword(keyword);
        } else if (statusFilter != null && "CLOSED".equals(statusFilter)) {
            projects = projectService.findByStatusClosed();
        } else if (bidFilter != null && !bidFilter.isBlank()) {  // ← 이 부분이 있나요?
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

        // 3. 카테고리 및 필터 탭 활성화 로직 업데이트
        // 다른 필터나 검색어가 없고, 낙찰완료 필터가 아닐떄 활성화
        model.addAttribute("isAll", category == null && keyword == null && statusFilter == null && bidFilter == null);

        // 낙찰완료건
        model.addAttribute("isClosedFilter", "CLOSED".equals(statusFilter));
        model.addAttribute("isWeb", "WEB".equals(category));
        model.addAttribute("isApp", "APP".equals(category));
        model.addAttribute("isUiux", "UI_UX".equals(category));
        model.addAttribute("isData", "DATA".equals(category));
        model.addAttribute("isVideo", "VIDEO".equals(category));
        model.addAttribute("isEtc", "ETC".equals(category));

        log.info("bidFilter 값: {}", bidFilter);
        return "project/project-list";
    }
    // 프로젝트 상세조회(id)
    @GetMapping("/projects/{id}/detail")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        ProjectResponeDTO.DetailDTO project = projectService.findProjectById(id);
        model.addAttribute("project", project);
        // 로그인한 사용자가 프로젝트 작성자인지 확인
        boolean isOwner = sessionUser != null && sessionUser.getId().equals(project.getMemberId());
        model.addAttribute("isOwner", isOwner);
        return "project/project-detail";
    }

    // 프로젝트 수정 폼
    @GetMapping("/projects/{id}/update-form")
    public String updateForm(@PathVariable Long id, Model model) {
        log.info("project 수정 폼 요청");
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

    // 프로젝트 수정
    @PostMapping("/projects/{id}/update")
    public String update(@PathVariable Long id,
                         ProjectRequestDTO.UpdateDTO req,
                         HttpSession session) {
        log.info("project 수정 요청");
        req.validate();
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        projectService.updateProject(id, req, sessionUser);
        return "redirect:/projects/" + id + "/detail";
    }

    // 카테고리별 조회
    @GetMapping("/projects/category")
    public String findByCategory(@RequestParam String category, Model model) {
        log.info("카테고리별 조회 요청");
        model.addAttribute("projects", projectService.findByCategory(category));
        return "project/project-list";
    }

    // 키워드 검색
    @GetMapping("/projects/search")
    public String search(@RequestParam String keyword, Model model) {
        log.info("키워드별 검색 요청");
        List<ProjectResponeDTO.ListDTO> projects = projectService.findByKeyword(keyword);
        model.addAttribute("projects", projects);
        model.addAttribute("totalCount", projects.size());
        model.addAttribute("keyword", keyword);
        model.addAttribute("isAll", false);
        model.addAttribute("isWeb", false);
        model.addAttribute("isApp", false);
        model.addAttribute("isUiux", false);
        model.addAttribute("isData", false);
        model.addAttribute("isVideo", false);
        model.addAttribute("isEtc", false);
        model.addAttribute("isClosedFilter", false);
        model.addAttribute("isCertifiedOnly", false);
        return "project/project-list";
    }

    @PostMapping("/projects/{id}/done")
    public String done(@PathVariable Long id, HttpSession session) {
        log.info("project 검토 확인 요청");
        Member sessionUser = (Member)session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "redirect:/login";
        }
        projectService.doneProject(id, sessionUser);
        return "redirect:/mypage?tab=projects";
    }

    // 프로젝트 삭제
    @PostMapping("/projects/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        log.info("project 삭제 요청");
        Member sessionUser = (Member)session.getAttribute(Define.SESSION_USER);
        projectService.deleteProject(id,sessionUser);
        return "redirect:/projects/list";
    }



}
