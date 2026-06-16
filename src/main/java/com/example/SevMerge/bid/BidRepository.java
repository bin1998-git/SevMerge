package com.example.SevMerge.bid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {

    // 프로젝트에 들어온 제안서 조회
    @Query("""
            SELECT b FROM Bid b JOIN FETCH b.expert
             WHERE b.project.id = :projectId AND b.isDeleted = false
             ORDER BY b.createdAt DESC
            """)
    List<Bid> findByProjectId(@Param("projectId") Long projectId);

    // 전문가가 제출 제안서 조회
    @Query("""
            SELECT b FROM Bid b JOIN FETCH b.project p JOIN FETCH p.member
            WHERE b.expert.id = :expertId AND b.isDeleted = false
            ORDER BY b.createdAt DESC
            """)
    List<Bid> findByExpertId(@Param("expertId") Long expertId);

    // 중복된 입찰 체크
    @Query("""
            SELECT b FROM Bid b
            WHERE b.project.id = :projectId AND b.expert.id = :expertId AND b.isDeleted = false
            """)
    Optional<Bid> findByProjectIdAndExpertId(@Param("projectId") Long projectId,
                                             @Param("expertId") Long expertId);

    // 낙찰받은 제안서 조회
    @Query("""
            SELECT b FROM Bid b JOIN FETCH b.expert
            WHERE b.project.id = :projectId AND b.status = :status AND b.isDeleted = false
            """)
    Optional<Bid> findSelectedBidByProjectId(@Param("projectId") Long projectId,
                                             @Param("status") BidStatus status);

    // 클라이언트 입장에서 입찰한 전문가 조회 - 현재 쪽지 보낼사람 용도로 사용
    @Query("""                                                                                                                                                                                                   
           SELECT b FROM Bid b JOIN FETCH b.expert JOIN FETCH b.project
           WHERE b.project.member.id = :memberId AND b.isDeleted = false
           ORDER BY b.createdAt DESC
           """)
    List<Bid> findByProjectMemberId(@Param("memberId") Long memberId);

    // 쪽지 전송 관계 검증 쿼리
    @Query("""
           SELECT COUNT(b) > 0 
           FROM Bid b
           WHERE b.isDeleted = false
           AND(b.project.member.id = :memberId1 AND b.expert.id = :memberId2)
           OR (b.project.member.id = :memberId2 AND b.expert.id = :memberId1)
           """)
    boolean existsBidRelation(@Param("memberId1") Long memberId1, @Param("memberId2") Long memberId2);


    // 프로젝트별 입찰(제안서) 개수
    @Query("""
        SELECT COUNT(b) FROM Bid b
        WHERE b.project.id = :projectId AND b.isDeleted = false
        """)
    long countByProjectId(@Param("projectId") Long projectId);

    // 전문가의 낙찰된(SELECTED) 제안서 목록 — 계정 정지 알림용
    @Query("""
        SELECT b FROM Bid b
        JOIN FETCH b.project p
        JOIN FETCH p.member
        WHERE b.expert.id = :expertId AND b.status = 'SELECTED' AND b.isDeleted = false
        """)
    List<Bid> findSelectedBidsByExpertId(@Param("expertId") Long expertId);
}
