package com.example.SevMerge.project;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {


    // 프로젝트 전체 조회(최신순 조회)
    @Query("SELECT p FROM Project p JOIN FETCH p.member ORDER BY p.createdAt DESC")
    List<Project> findAllProjects();

    // 프로젝트 상세조회
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.id = :id")
    Optional<Project> findByProjectId(@Param("id") Long id);

    // 프로젝트 카테고리별 조회
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.category = :category ORDER BY p.createdAt DESC")
    List<Project> findByCategory(@Param("category") Category category);

    // 키워드 검색
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword% ORDER BY p.createdAt DESC")
    List<Project> findByKeyword(@Param("keyword") String keyword);

    // 의뢰인이 등록한 프로젝트 목록
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.member.id = :memberId ORDER BY p.createdAt DESC")
    List<Project> findAllProjectByMemberId(@Param("memberId") Long memberId);

    // 상태별 조회
    @Query("SELECT p FROM Project p JOIN FETCH p.member WHERE p.projectStatus = :status ORDER BY p.createdAt DESC")
    List<Project> findByStatus(@Param("status") ProjectStatus status);
}
