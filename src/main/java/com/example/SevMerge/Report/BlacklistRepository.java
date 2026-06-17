package com.example.SevMerge.Report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<BlackList, Long> {
    @Query("SELECT b FROM BlackList b JOIN FETCH b.member ORDER BY b.id DESC")
    List<BlackList> findAllWithMemberOrderByIdDesc();

    // 정지해제 처리할려고 현재 차단상태인 회원 불러오기
    List<BlackList> findByMemberIdAndIsActiveTrue(Long memberId);

    // 계정정지화면에 신고 단건 최신조회
    Optional<BlackList> findFirstByMemberIdAndIsActiveTrueOrderByIdDesc(Long memberId);
}
