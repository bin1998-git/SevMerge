package com.example.SevMerge.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    // PENDING 상태 전문가 목록
    List<Member> findByRoleAndStatus(Role role, Status status);

    // 이름 또는 이메일로 검색 (관리자용)
    @Query("SELECT m FROM Member m WHERE (m.name LIKE %:keyword% OR m.email LIKE %:keyword%) AND (m.isDeleted = false OR m.isDeleted IS NULL) ORDER BY m.name ASC")
    Page<Member> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 관리자 전체 회원 목록을 페이지 단위로 조회하기
    @Query("SELECT m FROM Member m WHERE m.isDeleted = false OR m.isDeleted IS NULL ORDER BY m.name ASC")
    Page<Member> findByIsDeletedFalse(Pageable pageable);

    // 최근 가입한 회원 5명 조회
    List<Member> findTop5ByIsDeletedFalseOrIsDeletedIsNullOrderByCreatedAtDesc();

    // 이름/ 이멜 검색 + 가입일 과거순 정렬 전용
    @Query("SELECT m FROM Member m WHERE (m.name LIKE %:keyword% OR m.email LIKE %:keyword%) AND (m.isDeleted = false OR m.isDeleted IS NULL)")
    Page<Member> searchByKeywordOrderByCreatedAt(@Param("keyword") String keyword, Pageable pageable);

    // 전체 회원 목록 + 가입일 과거순 정렬전용
    @Query("SELECT m FROM Member m WHERE m.isDeleted = false OR m.isDeleted IS NULL")
    Page<Member> findByIsDeletedFalseOrderByCreatedAt(Pageable pageable);

    // 소셜 로그인 회원 조회 (provider + providerId 조합)
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);

    // 이번달 신규 회원 조회 기능
    @Query(value = "SELECT COUNT(*) FROM member_tb WHERE YEAR(created_at) = YEAR(NOW()) AND MONTH(created_at) = MONTH(NOW())", nativeQuery = true)
    long countNewMembersThisMonth();

    // 승인 대기 전문가 조회하기
    @Query("""
        SELECT count(m) FROM Member m WHERE status = 'PENDING'
        """)
    Long pendingProjectsCount();

    // 소프트삭제
    Optional<Member> findByEmailAndIsDeletedFalse(String email);
    List<Member> findAllByIsDeletedFalse();

    // 최근 7일간 가입자 수 조회
    @Query(value = """
        SELECT DATE_FORMAT(created_at, '%m-%d') as date, COUNT(*) as count 
        FROM member_tb 
        WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY)
        GROUP BY DATE_FORMAT(created_at, '%m-%d')
        ORDER BY date ASC
        """, nativeQuery = true)
    List<Object[]> findRecent7DaysRegistrationCount();

    // 역할Role 조건으로 회원 조회
    List<Member> findByRoleAndIsDeletedFalse(Role role);

    // 최근 가입 회원 5명 (대시보드용)
    @Query("SELECT m FROM Member m WHERE m.isDeleted = false OR m.isDeleted IS NULL ORDER BY m.createdAt DESC")
    List<Member> findTop5RecentMembers(Pageable pageable);

    // 역할과 검색어 조건으로 탈퇴하지 않은 회원 목록 페이징 조회
    @Query("SELECT m FROM Member m WHERE m.role = :role " +
            "AND (:keyword IS NULL OR :keyword = '' OR m.name LIKE %:keyword% OR m.email LIKE %:keyword%) " +
            "AND (m.isDeleted = false OR m.isDeleted IS NULL)")
    Page<Member> findByRoleAndKeyword(@Param("role") Role role, @Param("keyword") String keyword, Pageable pageable);

    // 시작일부터 종료일까지의 일자별 신규 회원 가입 수 조회
    @Query(value = """
            SELECT DATE_FORMAT(created_at, '%m-%d') as date_str, COUNT(*) as cnt 
            FROM member_tb 
            WHERE created_at BETWEEN :startDate AND :endDate + INTERVAL 1 DAY 
            GROUP BY DATE_FORMAT(created_at, '%m-%d') 
            ORDER BY date_str ASC
            """, nativeQuery = true)
    List<Object[]> findMemberCountByPeriod(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    // 특정 권한 조건으로 시작일부터 종료일까지 일자별 가입 수 조회
    @Query(value = """
            SELECT DATE_FORMAT(created_at, '%m-%d') as date_str, COUNT(*) as cnt 
            FROM member_tb 
            WHERE LOWER(role) = LOWER(:role) 
              AND created_at BETWEEN :startDate AND :endDate + INTERVAL 1 DAY 
            GROUP BY DATE_FORMAT(created_at, '%m-%d') 
            ORDER BY date_str ASC
            """, nativeQuery = true)
    List<Object[]> findMemberCountByRoleAndPeriod(@Param("role") String role, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}