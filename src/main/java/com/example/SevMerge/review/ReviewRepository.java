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



}
