package com.example.SevMerge.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // PAY-003: 중복 결제 방지 (프로젝트당 1건)
    boolean existsByProjectId(Long projectId);

    // 프로젝트 ID로 결제 단건 조회
    Optional<Payment> findByProjectId(Long projectId);

    // PAY-006: 의뢰인 결제 내역 조회
    List<Payment> findByClientId(Long clientId);

    // PAY-006: 전문가 정산 내역 조회
    List<Payment> findByExpertId(Long expertId);

    // 두 회원 간 완료된 거래 여부 확인 (리뷰 작성 검증용)
    boolean existsByClientIdAndExpertIdAndStatus(Long clientId, Long expertId, PaymentStatus status);
}
