package com.example.SevMerge.review;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review , Long> {

    // 평균값 계산
    @Query("""
        SELECT AVG(r.countStar) FROM Review r JOIN r.expertProfile WHERE r.expertProfile.id = :expertId
    """)
    public Optional<BigDecimal> avgRating(@Param("expertId") Long expertId);


    // 리뷰 카운트 레파지토리
    @Query("""
        SELECT COUNT(r.id) FROM Review r JOIN r.expertProfile WHERE r.expertProfile.id = :expertId
    """)
    public Optional<Integer> countReview(@Param("expertId") Long expertId);


    // 특정 전문가의 리뷰들
    @Query("""
        SELECT r FROM Review r JOIN r.expertProfile WHERE r.expertProfile.id = :expertId
    """)
    public Page<Review> findByExpertProfileReviewPage(@Param("expertId") Long expertId, Pageable pageable);

}
