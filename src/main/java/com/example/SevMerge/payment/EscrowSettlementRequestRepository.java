package com.example.SevMerge.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EscrowSettlementRequestRepository extends JpaRepository<EscrowSettlementRequest, Long> {

    boolean existsByPaymentIdAndStatus(Long paymentId, EscrowRequestStatus status);

    Optional<EscrowSettlementRequest> findByPaymentIdAndStatus(Long paymentId, EscrowRequestStatus status);

    List<EscrowSettlementRequest> findByExpertId(Long expertId);

    List<EscrowSettlementRequest> findAllByStatusOrderByCreatedAtDesc(EscrowRequestStatus status);

    List<EscrowSettlementRequest> findAllByOrderByCreatedAtDesc();

    @Query("SELECT r.paymentId FROM EscrowSettlementRequest r WHERE r.expertId = :expertId AND r.status = 'PENDING'")
    Set<Long> findPendingPaymentIdsByExpertId(@Param("expertId") Long expertId);
}
