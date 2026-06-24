package com.example.SevMerge.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>{

    // 프로젝트 전체 조회(최신순 조회, DRAFT 제외)
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.isDeleted = false AND p.projectStatus <> 'DRAFT' ORDER BY p.createdAt DESC")
    List<Project> findAllProjects();

    @Query("SELECT p FROM Project p WHERE p.member.id = :memberId AND p.projectStatus = :status")
    Optional<Project> findByMemberIdAndProjectStatus(@Param("memberId") Long memberId,
                                                     @Param("status") ProjectStatus status);
    // 프로젝트 상세조회
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.id = :id AND p.isDeleted = false")
    Optional<Project> findByProjectId(@Param("id") Long id);

    // 진행중인 프로젝트 조회
    @Query("""
            SELECT count(p) FROM Project p WHERE p.isDeleted = false AND p.projectStatus = 'IN_PROGRESS'
            """)
    Long countActiveProjects();

    // 완료 프로젝트 조회
    @Query("""
            SELECT count(p) FROM Project p WHERE p.isDeleted = false AND p.projectStatus = 'DONE'
            """)
    Long doneProjects();

    // 이번달에 완료한 프로젝트 조회
    @Query("""
            SELECT COUNT(p) 
            FROM Project p 
            WHERE p.isDeleted = false 
              AND p.projectStatus = 'DONE'
              AND FUNCTION('YEAR', p.createdAt) = FUNCTION('YEAR', CURRENT_DATE) 
              AND FUNCTION('MONTH', p.createdAt) = FUNCTION('MONTH', CURRENT_DATE)
            """)
    Long monthDoneProjects();

    // 입찰조회
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.bidFilter = :bidFilter AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Project> findByBidFilter(@Param("bidFilter") BidFilter bidFilter);


    // 프로젝트 카테고리별 조회
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.category = :category AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Project> findByCategory(@Param("category") Category category);

    // 키워드 검색
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE (p.title LIKE %:keyword% OR p.description LIKE %:keyword%) AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Project> findByKeyword(@Param("keyword") String keyword);

    // 의뢰인이 등록한 프로젝트 목록
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.member.id = :memberId AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Project> findAllProjectByMemberId(@Param("memberId") Long memberId);

    // 상태별 조회
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.projectStatus = :status AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Project> findByStatus(@Param("status") ProjectStatus status);

    // 뷰카운트 증가
    @Modifying
    @Query("""
                UPDATE Project p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id
            """)
    void increaseCount(@Param("id") Long id);

    // 관리자 전용 키워드 검색
    @Query("""
    SELECT p 
    FROM Project p 
    JOIN FETCH p.member 
    WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
       OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR LOWER(p.member.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) 
      AND p.isDeleted = false 
    ORDER BY p.createdAt DESC
""")
    List<Project> findAdminProjectsByKeyword(@Param("keyword") String keyword);

    // 관리자 전용 삭제
    @Modifying
    @Query("UPDATE Project p SET p.isDeleted = true WHERE p.id = :id")
    void deleteProjectByAdmin(@Param("id") Long id);

    // 최근 7일간 일자별 프로젝트 등록 수 조회
    @Query(value = "SELECT DATE_FORMAT(created_at, '%m-%d') as date_str, COUNT(*) as cnt " +
            "FROM project_tb " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE_FORMAT(created_at, '%m-%d') " +
            "ORDER BY date_str ASC", nativeQuery = true)
    List<Object[]> findRecent7DaysProjectCount();

    // 최근 7일간 일자별 프로젝트 완료 수 조회
    @Query(value = "SELECT DATE_FORMAT(created_at, '%m-%d') as date_str, COUNT(*) as cnt " +
            "FROM project_tb " +
            "WHERE project_status = 'DONE' AND created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE_FORMAT(created_at, '%m-%d') " +
            "ORDER BY date_str ASC", nativeQuery = true)
    List<Object[]> findRecent7DaysCompletedCount();

    // 관리자 전용 - 특정 프로젝트 상태랑 검색 조건으로 목록 조회
    @Query("""
        SELECT p 
        FROM Project p 
        JOIN FETCH p.member 
        WHERE p.projectStatus = :status 
          AND (:keyword IS NULL OR :keyword = '' 
               OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
               OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.member.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND p.isDeleted = false 
        ORDER BY p.createdAt DESC
    """)
    List<Project> findAdminProjectsByStatusAndKeyword(@Param("status") ProjectStatus status, @Param("keyword") String keyword);

    // 시작일부터 종료일까지의 일자별 프로젝트 등록 수 조회
    @Query(value = """
            SELECT DATE_FORMAT(created_at, '%m-%d') as date_str, COUNT(*) as cnt 
            FROM project_tb 
            WHERE created_at BETWEEN :startDate AND :endDate + INTERVAL 1 DAY 
            GROUP BY DATE_FORMAT(created_at, '%m-%d') 
            ORDER BY date_str ASC
            """, nativeQuery = true)
    List<Object[]> findProjectCountByPeriod(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    // 시작일부터 종료일까지의 일자별 프로젝트 완료 수 조회
    @Query(value = """
            SELECT DATE_FORMAT(created_at, '%m-%d') as date_str, COUNT(*) as cnt 
            FROM project_tb 
            WHERE project_status = 'DONE' AND created_at BETWEEN :startDate AND :endDate + INTERVAL 1 DAY 
            GROUP BY DATE_FORMAT(created_at, '%m-%d') 
            ORDER BY date_str ASC
            """, nativeQuery = true)
    List<Object[]> findCompletedCountByPeriod(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    // 시작일부터 종료일까지 특정 프로젝트 일자별 등록 수 조회 (차트용)
    @Query(value = """
            SELECT DATE_FORMAT(created_at, '%m-%d') as date_str, COUNT(*) as cnt 
            FROM project_tb 
            WHERE LOWER(category) = LOWER(TRIM(:projectType)) 
              AND created_at BETWEEN :startDate AND :endDate + INTERVAL 1 DAY 
            GROUP BY DATE_FORMAT(created_at, '%m-%d') 
            ORDER BY date_str ASC
            """, nativeQuery = true)
    List<Object[]> findProjectCountByPeriodAndType(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("projectType") String projectType
    );

    // 상태 일자별 등록수 조회
    @Query(value = """
            SELECT DATE_FORMAT(created_at, '%m-%d') as date_str, COUNT(*) as cnt 
            FROM project_tb 
            WHERE project_status = :status 
              AND created_at BETWEEN :startDate AND :endDate + INTERVAL 1 DAY 
            GROUP BY DATE_FORMAT(created_at, '%m-%d') 
            ORDER BY date_str ASC
            """, nativeQuery = true)
    List<Object[]> findProjectCountByStatusAndPeriod(
            @Param("status") String status,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );
}
