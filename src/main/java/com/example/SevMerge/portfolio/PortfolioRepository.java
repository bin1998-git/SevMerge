package com.example.SevMerge.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio,Long> {

    @Query("""
        SELECT po FROM Portfolio po 
        JOIN FETCH po.expertProfile e 
        JOIN FETCH e.member m 
        WHERE m.id = :expertId AND po.isActive = true
    """)
    List<Portfolio> findByExpertIdIsActive(@Param("expertId") Long expertId);




    // 포트 폴리오 카운트
    @Query("SELECT COUNT(po) FROM Portfolio po JOIN po.expertProfile e WHERE e.member.id = :expertId")
    Long portfolioCountByExpertId(@Param("expertId") Long expertId);

}






