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
    @GetMapping("/project/save-form")
    public String saveForm() {
        log.info("project 등록폼 요청");
        return "project/project-save";
    }

    // 프로젝트 등록
    @PostMapping("/project/save")
    public String save(ProjectRequestDTO.SaveDTO req, HttpSession session) {
        log.info("project 등록 요청");
        req.validate();
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        projectService.saveProject(req, sessionUser);
        return "redirect:/project/list";
    }

    // 프로젝트 목록 조회
    @GetMapping("/project/list")
    public String list(Model model,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category) {
        log.info("project 목록 조회 요청");

        List<ProjectResponeDTO.ListDTO> projects;

        if (keyword != null && !keyword.isBlank()) {
            projects = projectService.findByKeyword(keyword);
        } else if (category != null && !category.isBlank()) {
            projects = projectService.findByCategory(category);
        } else {
            projects = projectService.findAllProjects();
        }

        model.addAttribute("projects", projects);
        model.addAttribute("totalCount", projects.size());
        model.addAttribute("keyword", keyword != null ? keyword : "");

        // 카테고리 탭 활성화
        model.addAttribute("isAll", category == null && keyword == null);
        model.addAttribute("isWeb", "WEB".equals(category));
        model.addAttribute("isApp", "APP".equals(category));
        model.addAttribute("isUiux", "UI_UX".equals(category));
        model.addAttribute("isData", "DATA".equals(category));
        model.addAttribute("isVideo", "VIDEO".equals(category));
        model.addAttribute("isEtc", "ETC".equals(category));

        return "project/project-list";
    }

    // 프로젝트 상세조회(id)
    @GetMapping("/project/{id}/detail")
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
    @GetMapping("/project/{id}/update-form")
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
    @PostMapping("/project/{id}/update")
    public String update(@PathVariable Long id,
                         ProjectRequestDTO.UpdateDTO req,
                         HttpSession session) {
        log.info("project 수정 요청");
        req.validate();
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        projectService.updateProject(id, req, sessionUser);
        return "redirect:/project/" + id + "/detail";
    }

    // 카테고리별 조회
    @GetMapping("/project/category")
    public String findByCategory(@RequestParam String category, Model model) {
        log.info("카테고리별 조회 요청");
        model.addAttribute("projects", projectService.findByCategory(category));
        return "project/project-list";
    }

    // 키워드 검색
    @GetMapping("/project/search")
    public String search(@RequestParam String keyword, Model model) {
        log.info("키워드별 검색 요청");
        model.addAttribute("projects", projectService.findByKeyword(keyword));
        return "project/project-list";
    }

    // 프로젝트 삭제
    @PostMapping("/project/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        log.info("project 삭제 요청");
        Member sessionUser = (Member)session.getAttribute(Define.SESSION_USER);
        projectService.deleteProject(id,sessionUser);
        return "redirect:/project/list";
    }

}
