package com.example.SevMerge.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio,Long> {

    @Query("""
        SELECT po FROM Portfolio po JOIN FETCH po.expertProfile WHERE po.expertProfile.member.id= :expertId AND po.isActive=true
""")
    List<Portfolio> findByExpertIdIsActive(@Param("expertId") Long expertId);


@Query("""
    SELECT COUNT(po) FROM Portfolio po JOIN po.expertProfile WHERE po.expertProfile.member.id = :memberId AND po.isActive=true
""")
    Long countPortfolioByMemberId(@Param("memberId") Long memberId);
}






