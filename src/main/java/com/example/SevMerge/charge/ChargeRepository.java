package com.example.SevMerge.charge;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long> {

    // 중복 처리 방지 — orderId로 완료 건 조회
    Optional<Charge> findByOrderId(String orderId);

    // 회원 충전 내역 (최신순)
    List<Charge> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
