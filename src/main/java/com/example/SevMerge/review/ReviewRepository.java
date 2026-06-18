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

    // 작성자 이름이 보여야되기 때문에 reviewer 조인
    @Query("SELECT r FROM Review r JOIN FETCH r.reviewer WHERE r.targeter.id = :memberId AND r.isDelete = false")
    List<Review> findMyReviews(@Param("memberId") Long memberId);

    @Query("""
                SELECT r FROM Review r JOIN FETCH r.targeter WHERE r.reviewer.id = :reviewerId AND r.isDelete = false
            """)
    List<Review> findMySaveReviews(@Param("reviewerId") Long reviewerId);


    // 평균값 계산
    @Query("""
        SELECT AVG(r.countStar) FROM Review r WHERE r.targeter.id = :targetId AND r.isDelete = false
    """)
    Double avgRating(@Param("targetId") Long targetId);

    // 전문가 등급 계산에서 리뷰수가 적은 전문가의 신뢰성을 보장하기위한 전체 전문가의 평균
    @Query(value = """
    SELECT AVG(avg_star) FROM (
        SELECT ep.id, COALESCE(AVG(r.count_star), 0.0) as avg_star
        FROM expert_profile ep
        LEFT JOIN review_tb r ON r.targeter_id = ep.member_id
        GROUP BY ep.id
    ) sub
    """, nativeQuery = true)
    Optional<Double> globalRating();

    // 리뷰 건수 — ExpertProfile.totalReviews 갱신용
    @Query("SELECT COUNT(r) FROM Review r WHERE r.targeter.id = :targetId AND r.isDelete = false")
    int countByTargeterId(@Param("targetId") Long targetId);

    // 중복 리뷰 여부 확인
    @Query("SELECT COUNT(r) > 0 FROM Review r WHERE r.reviewer.id = :reviewerId AND r.targeter.id = :targeterId AND r.isDelete = false")
    boolean existsByReviewerAndTargeter(@Param("reviewerId") Long reviewerId, @Param("targeterId") Long targeterId);
}
