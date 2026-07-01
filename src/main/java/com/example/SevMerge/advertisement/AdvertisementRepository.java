package com.example.SevMerge.advertisement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Query("SELECT a FROM Advertisement a " +
            "WHERE a.placement = :placement " +
            "AND a.status = 'ACTIVE' " +
            "AND a.startDate <= :now AND a.endDate >= :now " +
            "ORDER BY a.createdAt DESC")
    List<Advertisement> findActiveByPlacement(@Param("placement") AdvertisementPlacement placement,
                                              @Param("now") Timestamp now);

    List<Advertisement> findByExpertIdOrderByCreatedAtDesc(Long expertId);

    @Query("SELECT a FROM Advertisement a WHERE a.status = 'ACTIVE' AND a.endDate < :now")
    List<Advertisement> findExpiredButStillActive(@Param("now") Timestamp now);

    // 승인 대기 광고 조회
    @Query("SELECT a FROM Advertisement a WHERE a.status = 'PENDING' ORDER BY a.createdAt DESC")
    List<Advertisement> findPendingAds();

    @Query("SELECT a FROM Advertisement a WHERE a.status IN ('ACTIVE','REJECTED','EXPIRED') ORDER BY a.createdAt DESC")
    List<Advertisement> findProcessedAds();

    // 기간별 광고 수익 조회
    @Query("SELECT FUNCTION('DATE_FORMAT', a.createdAt, '%m-%d') as date, SUM(a.price) as revenue " +
            "FROM Advertisement a " +
            "WHERE a.status IN ('ACTIVE', 'EXPIRED') " +
            "AND a.createdAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE_FORMAT', a.createdAt, '%m-%d') " +
            "ORDER BY date ASC")
    List<Object[]> findRevenueByPeriod(@Param("start") Timestamp start, @Param("end") Timestamp end);

    // 전체 광고 수익 합계
    @Query("SELECT COALESCE(SUM(a.price), 0) FROM Advertisement a WHERE a.status IN ('ACTIVE', 'EXPIRED')")
    Long findTotalRevenue();

    // 이번달 광고 수익
    @Query("SELECT COALESCE(SUM(a.price), 0) FROM Advertisement a " +
            "WHERE a.status IN ('ACTIVE', 'EXPIRED') " +
            "AND FUNCTION('YEAR', a.createdAt) = FUNCTION('YEAR', CURRENT_DATE) " +
            "AND FUNCTION('MONTH', a.createdAt) = FUNCTION('MONTH', CURRENT_DATE)")
    Long findThisMonthRevenue();

    // 중복 신청 방지용
    boolean existsByExpertIdAndPlacementAndStatusIn(
            Long expertId,
            AdvertisementPlacement placement,
            List<AdvertisementStatus> statuses
    );
}