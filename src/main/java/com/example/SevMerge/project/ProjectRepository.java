package com.example.SevMerge.project;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {


    // 프로젝트 전체 조회(최신순 조회)
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.isDeleted = false AND p.projectStatus = 'OPEN' ORDER BY p.createdAt DESC")
    List<Project> findAllProjects();

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
          AND p.projectStatus = DONE
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
}
