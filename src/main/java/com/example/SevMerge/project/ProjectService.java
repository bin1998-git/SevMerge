package com.example.SevMerge.project;

import com.example.SevMerge.bid.BidRepository;
import com.example.SevMerge.bid.BidStatus;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectCustomRepository projectCustomRepository;
    private final BidRepository bidRepository;
    private final MemberRepository memberRepository;

    // 프로젝트 전체조회 서비스
    public List<ProjectResponeDTO.ListDTO> findAllProjects() {
        log.info("project 전체 조회 서비스 시작");
        List<Project> projectList = projectRepository.findAllProjects();
        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 진행중인 프로젝트 개수 조회 (관리자용)
    @Transactional(readOnly = true)
    public Long getActiveProjectsCount() {
        log.info("진행중인 프로젝트 개수 조회 서비스 시작");
        return projectRepository.countActiveProjects();
    }

    // 완료한 프로젝트 조회
    public long getDoneProjectsCount() {
        log.info("완료된 프로젝트 개수 조회 서비스 시작");
        Long count = projectRepository.doneProjects();
        return count == null ? 0L : count;
    }

    // 이번달에 완료한 프로젝트 개수 조회
    public long getMonthDoneProjectsCount() {
        log.info("이번 달에 완료된 프로젝트 개수 조회 서비스 시작");
        Long count = projectRepository.monthDoneProjects();
        return count == null ? 0L : count;
    }


    // 프로젝트 상세조회 서비스
    public ProjectResponeDTO.DetailDTO findProjectById(Long id) {
        log.info("project 상세 서비스 시작");
        Project project = projectRepository.findByProjectId(id)
                .orElseThrow(() -> new NotFoundException("없는 프로젝트 입니다"));
        project.increaseViewCount(); // 조회수 증가
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



    // 중복 체크
    public List<ProjectResponeDTO.ListDTO> findByFilters(String keyword, String category, String statusFilter, String bidFilter) {

        List<Project> projectList = projectCustomRepository.findByFilters(keyword,category, statusFilter, bidFilter);

        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    //  낙찰 완료된 프로젝트만 필터링하여 조회하는 서비스 메서드 추가
    public List<ProjectResponeDTO.ListDTO> findByStatusClosed() {
        log.info("project 낙찰 완료(CLOSED) 상태별 조회 서비스 시작");

        // CLOSED 상태인 프로젝트 엔티티들을 조회
        List<Project> projectList = projectRepository.findByStatus(ProjectStatus.CLOSED);


        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 내 프로젝트 목록 조회
    public List<ProjectResponeDTO.ListDTO> myProjects(Member sessionMember) {
        log.info("내 project 조회 서비스 시작");
        List<Project> projectList = projectRepository.findAllProjectByMemberId(sessionMember.getId());
        return projectList.stream()
                .map(p -> {
                    ProjectResponeDTO.ListDTO dto = new ProjectResponeDTO.ListDTO(p);
                    //입찰 수 카운트 추가
                    dto.setBidCount((int) bidRepository.countByProjectId(p.getId()));
                    bidRepository.findSelectedBidByProjectId(p.getId(), BidStatus.SELECTED)
                            .ifPresent(bid -> dto.setSelectedExpertId(bid.getExpert().getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 입찰 필터 조회
    public List<ProjectResponeDTO.ListDTO> findByBidFilter(String bidFilter) {
        log.info("findByBidFilter 서비스 시작 - bidFilter: {}", bidFilter);
        List<Project> projectList = projectRepository.findByBidFilter(BidFilter.valueOf(bidFilter));
        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());

    }

    // 키워드 검색
    public List<ProjectResponeDTO.ListDTO> findByKeyword(String keyword) {
        log.info("project 키워드 검색 서비스 시작");
        List<Project> projectList = projectRepository.findByKeyword(keyword);
        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 프로젝트 등록
    @Transactional
    public void saveProject(ProjectRequestDTO.SaveDTO req, Member sessionMember) {
        log.info("project 등록 서비스 시작");
        req.validate();
        Project project = Project.builder()
                .member(sessionMember)
                .title(req.getTitle())
                .category(Category.valueOf(req.getCategory()))
                .description(req.getDescription())
                .budgetMin(req.getBudgetMin())
                .budgetMax(req.getBudgetMax())
                .deadline(Timestamp.valueOf(req.getDeadline() + " 00:00:00"))
                .isPrivate(req.getIsPrivate() != null ? req.getIsPrivate() : false)
                .projectStatus(ProjectStatus.OPEN)
                .viewCount(0)
                .build();
        log.info("deadline 값: {}", req.getDeadline());
        projectRepository.save(project);
    }

    // 프로젝트 임시등록
    @Transactional
    public Long saveDraft(Long memberId, ProjectRequestDTO.UpdateDTO dto) {
        Project project = projectRepository.findByMemberIdAndProjectStatus(memberId, ProjectStatus.DRAFT).orElseGet(
                () -> {
                    Member member = memberRepository.findById(memberId).orElseThrow();
                    return Project.builder()
                            .member(member)
                            .title("임시저장")
                            .category(Category.WEB)
                            .description("")
                            .budgetMin(0)
                            .budgetMax(0)
                            .deadline(new Timestamp(System.currentTimeMillis()))
                            .bidFilter(BidFilter.ALL)
                            .projectStatus(ProjectStatus.DRAFT)
                            .build();


                });
        project.updateDraft(dto);
        return projectRepository.save(project).getId();


    }

    //  임시저장 데이터 불러오기
    public ProjectResponeDTO.DetailDTO getMyDraft(Long memberId) {
        log.info("임시저장 프로젝트 조회 서비스 시작 - memberId: {}", memberId);
        // 엔티티 조회
        Project draft = projectRepository.findByMemberIdAndProjectStatus(memberId, ProjectStatus.DRAFT).orElse(null);

        // 데이터가 없으면 null값
        if (draft == null) {
            return null;
        }

        return new ProjectResponeDTO.DetailDTO(draft);
    }


    // 프로젝트 수정
    @Transactional
    public void updateProject(Long id, ProjectRequestDTO.UpdateDTO req, Member sessionMember) {
        log.info("project 수정 서비스 시작");
        Project project = projectRepository.findByProjectId(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 프로젝트 입니다"));
        if (!project.getMember().getId().equals(sessionMember.getId())) {
            throw new ForbiddenException("수정 권한이 없습니다");
        }
        req.validate();
        project.update(req);
    }

    // 낙찰된 프로젝트 검토확인 서비스
    @Transactional
    public void doneProject(Long id, Member session) {
        log.info("project 검토확인 서비스 시작");
        Project project = projectRepository.findByProjectId(id).orElseThrow(
                () -> new NotFoundException("존재하지 않는 프로젝트 입니다"));
        if (!project.getMember().getId().equals(session.getId())) {
            throw new ForbiddenException("검토확인 권한이 없습니다");
        }
        if (project.getProjectStatus() != ProjectStatus.CLOSED) {
            throw new BadRequestException("낙찰 완료된 프로젝트만 검토확인 가능합니다");
        }
        project.updateStatus(ProjectStatus.DONE);
    }

    // 프로젝트 삭제
    @Transactional
    public void deleteProject(Long id, Member sessionMember) {
        log.info("project 삭제 서비스 시작");
        Project project = projectRepository.findByProjectId(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 프로젝트 입니다"));
        if (!project.getMember().getId().equals(sessionMember.getId())) {
            throw new ForbiddenException("삭제 권한이 없습니다");
        }
        project.delete();
    }

    @Transactional
    public void increase(Long id) {
        projectRepository.increaseCount(id);
    }

    // 관리자 전용 프로젝트 키워드 검색
    public List<ProjectResponeDTO.ListDTO> findAdminProjectsByKeyword(String keyword) {
        log.info("관리자 전용 - 프로젝트 키워드 검색 서비스 시작 - 검색어 : [{}] ", keyword);

        List<Project> projectList = projectRepository.findAdminProjectsByKeyword(keyword);

        return projectList.stream()
                .map(ProjectResponeDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 관리자 전용 삭제기능
    @Transactional
    public void deleteProjectByAdmin(Long id) {
        log.info("관리자 프로젝트 소프트삭제 서비스 시작 - 대상 ID : {}", id);
        projectRepository.deleteProjectByAdmin(id);
    }
}
