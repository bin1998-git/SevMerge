package com.example.SevMerge.project;

import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    // 프로젝트 전체조회 서비스
    public List<ProjectResponeDTO.ListDTO> findAllProjects() {
        log.info("project 전체 조회 서비스 시작");
        List<Project> projectList = projectRepository.findAllProjects();
        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 프로젝트 상세조회 서비스(id로 조회)
    public ProjectResponeDTO.DetailDTO findProjectById(Long id) {
        log.info("project 상세 서비스 시작");
        Project project = projectRepository.findByProjectId(id).orElseThrow(() ->
                new NotFoundException("없는 프로젝트 입니다"));
        return new ProjectResponeDTO.DetailDTO(project);
    }

    // 카테고리별 조회
    public List<ProjectResponeDTO.ListDTO> findByCategory(String category) {
        log.info("project 카테고리별 조회 서비스 시작");

        List<Project> projectList = projectRepository.findByCategory(Category.valueOf(category));
        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 내가 받은 프로젝트 목록 조회
    public List<ProjectResponeDTO.ListDTO> myProjects(Member sessionMember) {
        log.info("내가 받은 project 서비스 조회");
        List<Project> projectList = projectRepository.findAllProjectByMemberId(sessionMember.getId());
        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 키워드별로 검색
    public List<ProjectResponeDTO.ListDTO> findByKeyword(String keyword) {
        log.info("project 키워드별로 조회 시작");
        List<Project> projectList = projectRepository.findByKeyword(keyword);
        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());
    }


    // 프로젝트 등록서비스
    @Transactional
    public void saveProject(ProjectRequestDTO.SaveDTO req, Member sessionMember) {
        log.info("project 등록 서비스 시작");
        // 유효성 검사
        req.validate();

        Project project = Project.builder()
                .member(sessionMember)
                .title(req.getTitle())
                .category(Category.valueOf(req.getCategory()))
                .description(req.getDescription())
                .budgetMin(req.getBudgetMin())
                .budgetMax(req.getBudgetMax())
                .deadline(req.getDeadline())
                .build();
        projectRepository.save(project);
    }

    // 프로젝트 수정서비스
    @Transactional
    public void updateProject(Long id, ProjectRequestDTO.UpdateDTO req, Member sessionMember) {
        log.info("project 수정 서비스 시작");
        Project project = projectRepository.findByProjectId(id).orElseThrow(() ->
                new NotFoundException("존재하지 않는 프로젝트 입니다"));
        if (!project.getMember().getId().equals(sessionMember.getBids())) {
            throw new ForbiddenException("수정 권한이 없습니다");
        }
        req.validate();
        project.update(req);
    }

    // 프로젝트 삭제서비스
    @Transactional
    public void deleteProject(Long id, Member sessionMember) {
        log.info("project 삭제서비스 시작");
        Project project = projectRepository.findByProjectId(id).orElseThrow(() ->
                new ForbiddenException("리스트에 없는 프로젝트 입니다"));
        if (!project.getMember().getId().equals(sessionMember.getId())) {
            throw new ForbiddenException("삭제 권한이 없습니다");
        }
        projectRepository.delete(project);
    }
}
