package com.example.SevMerge.bid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Integer> {

    // 프로젝트에 들어온 제안서 조회
    @Query("""
            SELECT b FROM Bid b JOIN FETCH b.expert
             WHERE b.project.id = :projectId ORDER BY b.createdAt DESC
            """)
    List<Bid> findByProjectId(@Param("projectId") Long projectId);

    // 전문가가 제출 제안서 조회
    @Query("""
            SELECT b FROM Bid b JOIN FETCH b.project
            WHERE b.expert.id = :expertId ORDER BY b.createdAt DESC
            """)
    List<Bid> findByExpertId(@Param("expertId") Long expertId);

    // 중복된 입찰 체크
    @Query("""
            SELECT b FROM Bid b
            WHERE b.project.id = :projectId AND b.expert.id = :expertId
            """)
    Optional<Bid> findByProjectIdAndExpertId(@Param("projectId") Long projectId,
                                             @Param("expertId") Long expertId);

    // 낙찰받은 제안서 조회
    @Query("""
            SELECT b FROM Bid b JOIN FETCH b.expert
            WHERE b.project.id = :projectId AND b.status = :status
            """)
    Optional<Bid> findSelectedBidByProjectId(@Param("projectId") Long projectId,
                                             @Param("status") BidStatus status);

}
