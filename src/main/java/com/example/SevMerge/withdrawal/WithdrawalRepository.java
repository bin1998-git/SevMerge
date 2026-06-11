package com.example.SevMerge.withdrawal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    List<Withdrawal> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Withdrawal w WHERE w.memberId = :memberId AND w.status = 'COMPLETED'")
    Integer sumCompletedAmountByMemberId(@Param("memberId") Long memberId);
}
