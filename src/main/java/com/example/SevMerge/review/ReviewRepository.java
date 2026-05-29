package com.example.SevMerge.review;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review , Long> {

    // 평균값 계산
    @Query("""
        SELECT AVG(r.countStar) FROM Review r JOIN r.expertProfile WHERE r.expertProfile.id = :expertId
    """)
    public Optional<BigDecimal> avgRating(@Param("expertId") Long expertId);

}
