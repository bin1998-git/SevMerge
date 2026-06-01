package com.example.SevMerge.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio,Long> {

    @Query("""
        SELECT po FROM Portfolio po JOIN FETCH po.expertProfile WHERE po.expertProfile.member.id= :expertId AND isActive=true
""")
    List<PortfolioResponse.ListDTO> findByExpertIdIsActive(Long expertId);
}




