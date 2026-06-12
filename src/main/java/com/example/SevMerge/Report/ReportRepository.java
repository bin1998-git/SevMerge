package com.example.SevMerge.Report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 관리자 전용 모든 신고내역 조회
    @Query("""
        SELECT r FROM Report r 
        JOIN FETCH r.comment c 
        JOIN FETCH c.board b 
        JOIN FETCH r.reporter m 
        WHERE r.isProcessed = false 
          AND (:keyword IS NULL OR :keyword = '' 
               OR c.content LIKE %:keyword% 
               OR m.name LIKE %:keyword% 
               OR r.reason LIKE %:keyword%)
        ORDER BY r.id DESC
    """)
    List<Report> findAllWithDetails(@Param("keyword") String keyword);

    // 동일한 댓글을 한번 더 신고하는 것 방지
    Optional<Report> findByCommentIdAndReporterId(Long commentId, Long reporterId);

    // 댓글 모든 신고내역 조회 (소프트삭제 처리용)
    List<Report> findByCommentId(Long commentId);

}
