package com.example.SevMerge.revenue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PlatformRevenueRepository extends JpaRepository<PlatformRevenue, Long> {

    // 전체 확정 수익 합계
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM PlatformRevenue r WHERE r.status = 'CONFIRMED'")
    Long findTotalConfirmedRevenue();

    // 타입별 수익 합계
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM PlatformRevenue r WHERE r.type = :type AND r.status = 'CONFIRMED'")
    Long findTotalByType(@Param("type") PlatformRevenueType type);

    // 이번달 수익
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM PlatformRevenue r " +
            "WHERE r.status = 'CONFIRMED' " +
            "AND FUNCTION('YEAR', r.createdAt) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', r.createdAt) = FUNCTION('MONTH', CURRENT_DATE)")
    Long findThisMonthRevenue();

    // referenceId로 조회 (광고 거절 시 취소용)
    Optional<PlatformRevenue> findByReferenceIdAndType(Long referenceId, PlatformRevenueType type);

    // 최근 수익 목록
    List<PlatformRevenue> findAllByOrderByCreatedAtDesc();

    // 기간별 수익
    @Query("SELECT r FROM PlatformRevenue r WHERE r.createdAt BETWEEN :start AND :end ORDER BY r.createdAt DESC")
    List<PlatformRevenue> findByPeriod(@Param("start") Timestamp start, @Param("end") Timestamp end);
}