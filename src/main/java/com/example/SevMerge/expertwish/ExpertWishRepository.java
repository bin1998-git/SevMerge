package com.example.SevMerge.expertwish;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpertWishRepository extends JpaRepository<ExpertWish, Long> {

    // 찜여부 확인
    boolean existsByMemberIdAndExpertId(Long memberId, Long expertId);

    // 찜 취소
    void deleteByMemberIdAndExpertId(Long MemberId, Long expertId);

    // 찜 갯수 카운트
    Long countByExpertId(Long expertId);

    // 찜을 가장 많이 받은 전문가 순으로 MemberId 리스트를 가져오는 쿼리
    @Query("SELECT w.expert.id FROM ExpertWish w GROUP BY w.expert.id ORDER BY COUNT(w) DESC")
    List<Long> findTopExpertIds();

    // 내가 찜한 전문가 목록 (페이지네이션)
    @Query("SELECT w FROM ExpertWish w JOIN FETCH w.expert WHERE w.member.id = :memberId ORDER BY w.id DESC")
    Page<ExpertWish> findAllByMemberIdOrderByIdDesc(@Param("memberId") Long memberId, Pageable pageable);

    // 이름으로 필터링 (페이지네이션)
    @Query("SELECT w FROM ExpertWish w JOIN FETCH w.expert WHERE w.member.id = :memberId AND w.expert.name LIKE %:keyword%")
    Page<ExpertWish> findByMemberIdAndExpertNameContaining(
            @Param("keyword") String keyword,
            @Param("memberId") Long memberId,
            Pageable pageable);
}
