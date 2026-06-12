package com.example.SevMerge.payment;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * PaymentService — 에스크로 관리
 *
 * [역할]
 * - 클라이언트의 잔액(balance)을 에스크로로 묶어두고,
 *   프로젝트 완료 시 전문가에게 지급하는 흐름을 담당.
 * - TossPayments API는 이 서비스에서 직접 호출하지 않음.
 *   (실제 결제는 ChargeService에서만 처리)
 *
 * [에스크로 상태]
 * PAID     → 클라이언트 balance 차감 완료, 에스크로 보관 중
 * SETTLED  → 프로젝트 완료, 전문가 balance 증가
 * REFUNDED → 환불 완료, 클라이언트 balance 복구
 *
 * [충돌 방지 전략]
 * - 타 도메인 Service 직접 주입 없음
 * - Member/Project 상태 변경은 JPQL/네이티브 쿼리로 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private static final double PLATFORM_FEE_RATE = 0.10;

    private final PaymentRepository paymentRepository;

    @PersistenceContext
    private EntityManager em;

    // ── 에스크로 생성 ──

    /**
     * 에스크로 생성
     * - 클라이언트 잔액에서 amount 차감
     * - Payment 레코드 PAID 상태로 저장 (= 에스크로 보관)
     * - 프로젝트 상태 IN_PROGRESS 전환
     */
    @Transactional
    public PaymentResponse createEscrow(Long clientId,
                                        Long projectId,
                                        Long expertId,
                                        Integer amount) {

        // 중복 계약 방지
        if (paymentRepository.existsByProjectId(projectId)) {
            throw new BadRequestException("이미 계약이 체결된 프로젝트입니다.");
        }

        // 클라이언트 잔액 확인
        Integer clientBalance = (Integer) em
                .createQuery("SELECT m.balance FROM Member m WHERE m.id = :id")
                .setParameter("id", clientId)
                .getSingleResult();

        if (clientBalance == null || clientBalance < amount) {
            throw new BadRequestException(
                    "잔액이 부족합니다. (현재 잔액: " + (clientBalance == null ? 0 : clientBalance) + "원)");
        }

        // 클라이언트 잔액 차감 (balance >= amount 조건으로 동시성 보호)
        int updated = em
                .createQuery("UPDATE Member m SET m.balance = m.balance - :amount " +
                             "WHERE m.id = :id AND m.balance >= :amount")
                .setParameter("amount", amount)
                .setParameter("id", clientId)
                .executeUpdate();

        if (updated == 0) {
            throw new BadRequestException("잔액 차감에 실패했습니다. 잔액을 확인해주세요.");
        }

        // 수수료 계산
        int platformFee = (int) (amount * PLATFORM_FEE_RATE);
        int netAmount = amount - platformFee;

        // 에스크로 레코드 저장
        Payment payment = Payment.builder()
                .projectId(projectId)
                .clientId(clientId)
                .expertId(expertId)
                .amount(amount)
                .platformFee(platformFee)
                .netAmount(netAmount)
                .status(PaymentStatus.PAID)
                .build();
        paymentRepository.save(payment);

        // 프로젝트 상태 → IN_PROGRESS
        em.createNativeQuery(
                "UPDATE project_tb SET project_status = 'IN_PROGRESS' WHERE id = :pid")
                .setParameter("pid", projectId)
                .executeUpdate();

        log.info("[Escrow] 생성 — projectId={}, clientId={}, amount={}", projectId, clientId, amount);
        return PaymentResponse.from(payment);
    }

    // ── 정산 ──

    /**
     * 에스크로 → 전문가 지급
     * PAID → SETTLED, expert.balance += netAmount
     * 의뢰인이 프로젝트 완료 확인 시 호출
     */
    @Transactional
    public PaymentResponse settle(Long paymentId, Long requesterId) {
        Payment payment = findById(paymentId);

        if (!Objects.equals(payment.getClientId(), requesterId)) {
            throw new ForbiddenException("정산 권한이 없습니다.");
        }

        try {
            payment.settle();
        } catch (IllegalStateException e) {
            throw new BadRequestException(e.getMessage());
        }

        // 전문가 잔액 증가
        em.createQuery(
                "UPDATE Member m SET m.balance = m.balance + :amount WHERE m.id = :id")
                .setParameter("amount", payment.getNetAmount())
                .setParameter("id", payment.getExpertId())
                .executeUpdate();

        // 프로젝트 상태 → DONE
        em.createNativeQuery(
                "UPDATE project_tb SET project_status = 'DONE' WHERE id = :pid")
                .setParameter("pid", payment.getProjectId())
                .executeUpdate();

        log.info("[Escrow] 정산 완료 — paymentId={}, expertId={}, netAmount={}",
                paymentId, payment.getExpertId(), payment.getNetAmount());
        return PaymentResponse.from(payment);
    }

    // ── 환불 ──

    /**
     * 에스크로 → 클라이언트 환불
     * PAID → REFUNDED, client.balance += amount
     * - 클라이언트 직접 요청 또는 관리자 분쟁 처리 시 호출
     * - Toss API 호출 없음 (잔액으로 직접 환원)
     */
    @Transactional
    public PaymentResponse refund(Long paymentId, Long requesterId) {
        Payment payment = findById(paymentId);

        boolean isClient = Objects.equals(payment.getClientId(), requesterId);
        boolean isAdmin  = isAdminMember(requesterId);

        if (!isClient && !isAdmin) {
            throw new ForbiddenException("환불 권한이 없습니다.");
        }

        if (payment.getStatus() == PaymentStatus.SETTLED) {
            throw new BadRequestException("이미 정산 완료된 건입니다. 관리자에게 문의하세요.");
        }

        try {
            payment.refund();
        } catch (IllegalStateException e) {
            throw new BadRequestException(e.getMessage());
        }

        // 클라이언트 잔액 복구
        em.createQuery(
                "UPDATE Member m SET m.balance = m.balance + :amount WHERE m.id = :id")
                .setParameter("amount", payment.getAmount())
                .setParameter("id", payment.getClientId())
                .executeUpdate();

        // 프로젝트 상태 → CANCELLED
        em.createNativeQuery(
                "UPDATE project_tb SET project_status = 'CANCELLED' WHERE id = :pid")
                .setParameter("pid", payment.getProjectId())
                .executeUpdate();

        log.info("[Escrow] 환불 — paymentId={}, clientId={}, amount={}",
                paymentId, payment.getClientId(), payment.getAmount());
        return PaymentResponse.from(payment);
    }

    // ── 조회 ──

    public List<PaymentResponse> getClientPayments(Long clientId) {
        return paymentRepository.findByClientId(clientId)
                .stream().map(PaymentResponse::from).toList();
    }

    public List<PaymentResponse> getExpertPayments(Long expertId) {
        return paymentRepository.findByExpertId(expertId)
                .stream().map(PaymentResponse::from).toList();
    }

    public PaymentResponse getByProjectId(Long projectId) {
        return paymentRepository.findByProjectId(projectId)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
    }

    // ── private ──

    private Payment findById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
    }

    private boolean isAdminMember(Long memberId) {
        String role = (String) em
                .createNativeQuery("SELECT role FROM member_tb WHERE id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
        return "ADMIN".equals(role);
    }
}
