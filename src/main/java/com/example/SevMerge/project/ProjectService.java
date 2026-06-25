package com.example.SevMerge.project;

import com.example.SevMerge.bid.BidRepository;
import com.example.SevMerge.bid.BidStatus;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public List<ProjectResponseDTO.ListDTO> findAllProjects(Pageable pageable) {
        log.info("project 전체 조회 서비스 시작");
        Page<Project> projectPage = projectRepository.findAllProjects(pageable);
        return projectPage.getContent().stream()
                .map(ProjectResponseDTO.ListDTO::new)
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
    public ProjectResponseDTO.DetailDTO findProjectById(Long id) {
        log.info("project 상세 서비스 시작");
        Project project = projectRepository.findByProjectId(id)
                .orElseThrow(() -> new NotFoundException("없는 프로젝트 입니다"));
        project.increaseViewCount(); // 조회수 증가
        return new ProjectResponseDTO.DetailDTO(project);
    }

    // 카테고리별 조회
    // 리턴 타입을 Page<ProjectResponseDTO.ListDTO>로 변경
    public Page<ProjectResponseDTO.ListDTO> findByCategory(String category, Pageable pageable) {
        log.info("project 카테고리별 조회 서비스 시작");

        Page<Project> projectPage = projectRepository.findByCategory(Category.valueOf(category), pageable);


        return projectPage.map(ProjectResponseDTO.ListDTO::new);
    }


    // 파라미터가 5개인 이 메서드 하나만 존재해야 합니다!
    public Page<ProjectResponseDTO.ListDTO> findByFilters(String keyword, String category, String statusFilter, String bidFilter, Pageable pageable) {

        // 여기서 레포지토리 호출할 때 반드시 5개 다 던져주세요.
        Page<Project> projectPage = projectCustomRepository.findByFilters(keyword, category, statusFilter, bidFilter, pageable);

        return projectPage.map(ProjectResponseDTO.ListDTO::new);
    }


    // 내 프로젝트 목록 조회
    public List<ProjectResponseDTO.ListDTO> myProjects(Member sessionMember) {
        log.info("내 project 조회 서비스 시작");
        List<Project> projectList = projectRepository.findAllProjectByMemberId(sessionMember.getId());
        return projectList.stream()
                .map(p -> {
                    ProjectResponseDTO.ListDTO dto = new ProjectResponseDTO.ListDTO(p);
                    //입찰 수 카운트 추가
                    dto.setBidCount((int) bidRepository.countByProjectId(p.getId()));
                    bidRepository.findSelectedBidByProjectId(p.getId(), BidStatus.SELECTED)
                            .ifPresent(bid -> dto.setSelectedExpertId(bid.getExpert().getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 입찰 필터 조회
    public List<ProjectResponseDTO.ListDTO> findByBidFilter(String bidFilter, Pageable pageable) {
        log.info("findByBidFilter 서비스 시작 - bidFilter: {}", bidFilter);
        Page<Project> projectList = projectRepository.findByBidFilter(BidFilter.valueOf(bidFilter), pageable);
        return projectList.stream()
                .map(ProjectResponseDTO.ListDTO::new)
                .collect(Collectors.toList());

    }

    // 키워드 검색
    public List<ProjectResponseDTO.ListDTO> findByKeyword(String keyword, Pageable pageable) {
        log.info("project 키워드 검색 서비스 시작");
        Page<Project> projectList = projectRepository.findByKeyword(keyword, pageable);
        return projectList.stream()
                .map(ProjectResponseDTO.ListDTO::new)
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
    public ProjectResponseDTO.DetailDTO getMyDraft(Long memberId) {
        log.info("임시저장 프로젝트 조회 서비스 시작 - memberId: {}", memberId);
        // 엔티티 조회
        Project draft = projectRepository.findByMemberIdAndProjectStatus(memberId, ProjectStatus.DRAFT).orElse(null);

        // 데이터가 없으면 null값
        if (draft == null) {
            return null;
        }

        return new ProjectResponseDTO.DetailDTO(draft);
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
        if (project.getProjectStatus() != ProjectStatus.COMPLETED) {
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
    public List<ProjectResponseDTO.ListDTO> findAdminProjectsByKeyword(String keyword) {
        log.info("관리자 전용 - 프로젝트 키워드 검색 서비스 시작 - 검색어 : [{}] ", keyword);

        List<Project> projectList = projectRepository.findAdminProjectsByKeyword(keyword);

        return projectList.stream()
                .map(ProjectResponseDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 최근 프로젝트 5개 조회
    public List<ProjectResponseDTO.ListDTO> getRecentProjects() {
        List<Project> projects = projectRepository.findTop5ByIsDeletedFalseOrIsDeletedIsNullOrderByCreatedAtDesc();

        return projects.stream()
                .map(ProjectResponseDTO.ListDTO::new)
                .collect(Collectors.toList());
    }

    // 관리자 전용 삭제기능
    @Transactional
    public void deleteProjectByAdmin(Long id) {
        log.info("관리자 프로젝트 소프트삭제 서비스 시작 - 대상 ID : {}", id);
        projectRepository.deleteProjectByAdmin(id);
    }

    // 관리자 실시간 그래프통계
    public List<Integer> getPast7DaysProjectTrend() {
        List<Object[]> rawData = projectRepository.findRecent7DaysProjectCount();
        Map<String, Integer> dateCountMap = new HashMap<>();

        if (rawData != null) {
            for (Object[] row : rawData) {
                if (row != null && row.length >= 2) {
                    dateCountMap.put(String.valueOf(row[0]), Integer.parseInt(String.valueOf(row[1])));
                }
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        // 일단 7일동안 발생한 총 등록수 합을 구함
        int sumInPast7Days = 0;
        for (int i = 0; i < 7; i++) {
            String targetDate = today.minusDays(i).format(formatter);
            sumInPast7Days += dateCountMap.getOrDefault(targetDate, 0);
        }

        int cumulativeCount = (int) getDoneProjectsCount() - sumInPast7Days;

        // 6일전부터 오늘까지 그날의 등록수를 더해줌
        List<Integer> trendData = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String targetDate = today.minusDays(i).format(formatter);
            // 그날 새로 등록된만큼 누적 합산함
            cumulativeCount += dateCountMap.getOrDefault(targetDate, 0);
            trendData.add(cumulativeCount);
        }
        return trendData;
    }

    // 최근 7일간 일자별 프로젝트 실시간 완료 띄우기
    public List<Integer> getPast7DaysCompletedTrend() {
        List<Object[]> rawData = projectRepository.findRecent7DaysCompletedCount();
        Map<String, Integer> dateCountMap = new HashMap<>();

        if (rawData != null) {
            for (Object[] row : rawData) {
                if (row != null && row.length >= 2) {
                    dateCountMap.put(String.valueOf(row[0]), Integer.parseInt(String.valueOf(row[1])));
                }
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        int sumInPast7Days = 0;
        for (int i = 0; i < 7; i++) {
            String targetDate = today.minusDays(i).format(formatter);
            sumInPast7Days += dateCountMap.getOrDefault(targetDate, 0);
        }

        int cumulativeCount = (int) getDoneProjectsCount() - sumInPast7Days;

        List<Integer> trendData = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String targetDate = today.minusDays(i).format(formatter);
            cumulativeCount += dateCountMap.getOrDefault(targetDate, 0);
            trendData.add(cumulativeCount);
        }
        return trendData;
    }

    // 사용자가 선택한 기간 동안 일자별 프로젝트 누적 등록 조회
    public List<Integer> getProjectTrendByPeriod(LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = projectRepository.findProjectCountByPeriod(startDate, endDate);
        Map<String, Integer> dateCountMap = new HashMap<>();

        if (rawData != null) {
            for (Object[] row : rawData) {
                if (row != null && row.length >= 2) {
                    dateCountMap.put(String.valueOf(row[0]), Integer.parseInt(String.valueOf(row[1])));
                }
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        int sumInPeriod = 0;
        for (int i = 0; i <= daysBetween; i++) {
            String targetDate = startDate.plusDays(i).format(formatter);
            sumInPeriod += dateCountMap.getOrDefault(targetDate, 0);
        }

        int totalProjects = (int) projectRepository.count(); // 전체 개수 조회
        int cumulativeCount = totalProjects - sumInPeriod;

        List<Integer> trendData = new ArrayList<>();
        for (int i = 0; i <= daysBetween; i++) {
            String targetDate = startDate.plusDays(i).format(formatter);
            trendData.add(dateCountMap.getOrDefault(targetDate, 0));
        }
        return trendData;
    }

    // 사용자가 선택한 기간 동안의 일자별 프로젝트 누적 완료 트렌드 조회
    public List<Integer> getCompletedTrendByPeriod(LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = projectRepository.findCompletedCountByPeriod(startDate, endDate);
        Map<String, Integer> dateCountMap = new HashMap<>();

        if (rawData != null) {
            for (Object[] row : rawData) {
                if (row != null && row.length >= 2) {
                    dateCountMap.put(String.valueOf(row[0]), Integer.parseInt(String.valueOf(row[1])));

                }
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        int sumInPeriod = 0;
        for (int i = 0; i <= daysBetween; i++) {
            String targetDate = startDate.plusDays(i).format(formatter);
            sumInPeriod += dateCountMap.getOrDefault(targetDate, 0);
        }

        int cumulativeCount = (int) getDoneProjectsCount() - sumInPeriod;

        List<Integer> trendData = new ArrayList<>();
        for (int i = 0; i <= daysBetween; i++) {
            String targetDate = startDate.plusDays(i).format(formatter);
            trendData.add(dateCountMap.getOrDefault(targetDate, 0));
        }
        return trendData;
    }

    // 관리자 전용 >> 필터로 상태별 목록 조회하기
    public List<ProjectResponseDTO.ListDTO> getAdminProjectsByStatusAndKeyword(String statusFilter, String keyword) {
        try {
            List<Project> projects;

            if ("ALL".equalsIgnoreCase(statusFilter)) {
                projects = projectRepository.findAdminProjectsByKeyword(keyword);
            } else {
                ProjectStatus status = ProjectStatus.valueOf(statusFilter.toUpperCase());
                projects = projectRepository.findAdminProjectsByStatusAndKeyword(status, keyword);
            }
            return projects.stream().map(ProjectResponseDTO.ListDTO::new).toList();
        } catch (BadRequestException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public boolean isReviewSkipped(Long projectId) {
        return projectRepository.findById(projectId)
                .map(Project::isReviewSkipped)
                .orElse(false);
    }

    @Transactional
    public void skipReview(Long projectId, Member member) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));
        project.setReviewSkipped(true);
    }

    // 특정 프로젝트 종류 일자별 등록 수 조회하는거
    public List<Integer> getProjectCountByPeriodAndType(LocalDate startDate, LocalDate endDate, String projectType) {
        List<Object[]> rawData = projectRepository.findProjectCountByPeriodAndType(startDate, endDate, projectType);
        Map<String, Integer> dateCountMap = new HashMap<>();

        if (rawData != null) {
            for (Object[] row : rawData) {
                if (row != null && row.length >= 2) {
                    dateCountMap.put(String.valueOf(row[0]), Integer.parseInt(String.valueOf(row[1])));
                }
            }
        }

        List<Integer> trendData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        for (int i = 0; i <= daysBetween; i++) {
            String targetDate = startDate.plusDays(i).format(formatter);
            trendData.add(dateCountMap.getOrDefault(targetDate, 0));
        }
        return trendData;
    }

    // 상태별 + 선택기간 동안 일자별 프로젝트 등록조회
    public List<Integer> getProjectTrendByStatusAndPeriod(String statusFilter, LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = projectRepository.findProjectCountByStatusAndPeriod(statusFilter, startDate, endDate);
        Map<String, Integer> dateCountMap = new HashMap<>();

        if (rawData != null) {
            for (Object[] row : rawData) {
                if (row != null && row.length >= 2) {
                    dateCountMap.put(String.valueOf(row[0]), Integer.parseInt(String.valueOf(row[1])));
                }
            }
        }

        List<Integer> trendData = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        for (int i = 0; i <= daysBetween; i++) {
            String targetDate = startDate.plusDays(i).format(formatter);
            trendData.add(dateCountMap.getOrDefault(targetDate, 0));
        }
        return trendData;
    }

    public long getTotalProjectCount() {
        return projectRepository.count(); // 카운트쿼리 용
    }


    public List<ProjectResponseDTO.ListDTO> findAllProjectsList() {
        return projectRepository.findAll().stream() // 전체 조회
                .map(ProjectResponseDTO.ListDTO::new)
                .collect(Collectors.toList());
    }
}
