package com.example.SevMerge.expertprofile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExpertReviewLogRepository extends JpaRepository<ExpertReviewLog, Long> {

    // 가장 최근 심사 이력 1건
    @Query("SELECT l FROM ExpertReviewLog l WHERE l.member.id = :memberId ORDER BY l.reviewedAt DESC LIMIT 1")
    Optional<ExpertReviewLog> findLatest(@Param("memberId") Long memberId);

    // 가장 최근 '거절' 이력 1건
    @Query("SELECT l FROM ExpertReviewLog l WHERE l.member.id = :memberId AND l.result = 'REJECTED' ORDER BY l.reviewedAt DESC LIMIT 1")
    Optional<ExpertReviewLog> findLatestReject(@Param("memberId") Long memberId);
}