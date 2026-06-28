package com.example.SevMerge.adbid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdBidRepository extends JpaRepository<AdBid, Long> {

    // 슬롯의 최고가 입찰 조회
    @Query("SELECT b FROM AdBid b WHERE b.slotId = :slotId AND b.status = 'PENDING' ORDER BY b.bidPrice DESC")
    List<AdBid> findBySlotIdOrderByBidPriceDesc(@Param("slotId") Long slotId);

    // 전문가의 특정 슬롯 입찰 여부
    Optional<AdBid> findBySlotIdAndExpertId(Long slotId, Long expertId);

    // 슬롯의 모든 입찰 조회
    List<AdBid> findBySlotId(Long slotId);

    // 전문가 입찰 내역
    List<AdBid> findByExpertIdOrderByCreatedAtDesc(Long expertId);

    @Query("SELECT b FROM AdBid b WHERE b.status = 'WINNER' AND b.reviewStatus = 'APPROVED'")
    List<AdBid> findApprovedWinners();

    // 슬롯의 낙찰자 조회
    @Query("SELECT b FROM AdBid b WHERE b.slotId = :slotId AND b.status = 'WINNER'")
    Optional<AdBid> findWinnerBySlotId(@Param("slotId") Long slotId);

    List<AdBid> findByReviewStatus(AdBidReviewStatus reviewStatus);

    List<AdBid> findByReviewStatusIn(List<AdBidReviewStatus> statuses);
}