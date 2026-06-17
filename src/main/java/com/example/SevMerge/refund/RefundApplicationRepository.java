package com.example.SevMerge.refund;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RefundApplicationRepository extends JpaRepository<RefundApplication, Long> {

    // 의뢰인 본인의 환불 신청 내역
    List<RefundApplication> findAllByClientIdOrderByCreatedAtDesc(Long clientId);

    // 관리자 - 대기중인 신청 목록
    List<RefundApplication> findAllByStatusOrderByCreatedAtAsc(RefundApplicationStatus status);

    // 관리자 - 전체 환불 신청 목록
    List<RefundApplication> findAllByOrderByCreatedAtDesc();

    // 동일 결제건에 대해 이미 대기중인 신청이 있는지 확인 (중복 신청 방지)
    boolean existsByPaymentIdAndStatus(Long paymentId, RefundApplicationStatus status);
}