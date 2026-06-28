package com.example.SevMerge.adbid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface AdSlotRepository extends JpaRepository<AdSlot, Long> {

    // 입찰 마감됐지만 아직 OPEN인 슬롯 (스케줄러용)
    @Query("SELECT s FROM AdSlot s WHERE s.status = 'OPEN' AND s.bidEndAt < :now")
    List<AdSlot> findExpiredOpenSlots(@Param("now") Timestamp now);

    // 현재 노출중인 슬롯
    @Query("SELECT s FROM AdSlot s WHERE s.status = 'AWARDED' AND s.displayStartAt <= :now AND s.displayEndAt >= :now")
    List<AdSlot> findCurrentlyDisplaying(@Param("now") Timestamp now);

    // 노출 종료된 슬롯 (스케줄러용)
    @Query("SELECT s FROM AdSlot s WHERE s.status = 'AWARDED' AND s.displayEndAt < :now")
    List<AdSlot> findExpiredDisplaying(@Param("now") Timestamp now);

    // 관리자용 전체 조회
    List<AdSlot> findAllByOrderByCreatedAtDesc();
}