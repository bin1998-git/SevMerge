package com.example.SevMerge.payment;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static final double PLATFORM_FEE_RATE = 0.03;

    private final PaymentRepository paymentRepository;
    private final EscrowSettlementRequestRepository escrowRequestRepository;
    private final MemberRepository memberRepository;

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

        // 전문가 잔액 증가 (97%)
        em.createQuery(
                "UPDATE Member m SET m.balance = m.balance + :amount WHERE m.id = :id")
                .setParameter("amount", payment.getNetAmount())
                .setParameter("id", payment.getExpertId())
                .executeUpdate();

        // 관리자 수수료 지급 (3%) — ADMIN 역할 계정 전체에 동일 금액 지급
        int adminUpdated = em.createQuery(
                "UPDATE Member m SET m.balance = m.balance + :fee WHERE m.role = :role")
                .setParameter("fee", payment.getPlatformFee())
                .setParameter("role", com.example.SevMerge.member.Role.ADMIN)
                .executeUpdate();
        if (adminUpdated == 0) {
            log.warn("[Escrow] 관리자 계정 없음 — 수수료 {}원 미지급 (paymentId={})",
                    payment.getPlatformFee(), paymentId);
        }

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

    // ── 전문가 정산 요청 ──

    /**
     * 전문가 → 관리자 정산 요청
     * 프로젝트가 COMPLETED 상태이고 Payment가 PAID일 때만 가능
     */
    @Transactional
    public void requestSettlement(Long paymentId, Long expertId, String message) {
        Payment payment = findById(paymentId);

        if (!Objects.equals(payment.getExpertId(), expertId)) {
            throw new ForbiddenException("정산 요청 권한이 없습니다.");
        }
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new BadRequestException("에스크로 보관 중인 결제에만 정산 요청이 가능합니다.");
        }
        if (escrowRequestRepository.existsByPaymentIdAndStatus(paymentId, EscrowRequestStatus.PENDING)) {
            throw new BadRequestException("이미 정산 요청 중입니다. 관리자 처리를 기다려주세요.");
        }

        String projectStatus = (String) em
                .createNativeQuery("SELECT project_status FROM project_tb WHERE id = :pid")
                .setParameter("pid", payment.getProjectId())
                .getSingleResult();

        if (!"COMPLETED".equals(projectStatus)) {
            throw new BadRequestException("작업 완료(COMPLETED) 상태의 프로젝트에만 정산 요청이 가능합니다.");
        }

        EscrowSettlementRequest request = EscrowSettlementRequest.builder()
                .paymentId(paymentId)
                .expertId(expertId)
                .projectId(payment.getProjectId())
                .message(message)
                .build();
        escrowRequestRepository.save(request);

        log.info("[Escrow] 정산요청 — paymentId={}, expertId={}", paymentId, expertId);
    }

    // ── 관리자 에스크로 승인 정산 ──

    /**
     * 관리자 → 에스크로 강제 정산 승인
     * EscrowSettlementRequest를 APPROVED로 변경하고 settle() 실행
     */
    @Transactional
    public void adminApproveSettlement(Long requestId) {
        EscrowSettlementRequest req = escrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("정산 요청을 찾을 수 없습니다."));

        if (req.getStatus() != EscrowRequestStatus.PENDING) {
            throw new BadRequestException("이미 처리된 요청입니다.");
        }

        Payment payment = findById(req.getPaymentId());

        try {
            payment.settle();
        } catch (IllegalStateException e) {
            throw new BadRequestException(e.getMessage());
        }

        // 전문가 97% 지급
        em.createQuery("UPDATE Member m SET m.balance = m.balance + :amount WHERE m.id = :id")
                .setParameter("amount", payment.getNetAmount())
                .setParameter("id", payment.getExpertId())
                .executeUpdate();

        // 관리자 3% 수수료
        int adminUpdated = em.createQuery(
                "UPDATE Member m SET m.balance = m.balance + :fee WHERE m.role = :role")
                .setParameter("fee", payment.getPlatformFee())
                .setParameter("role", Role.ADMIN)
                .executeUpdate();
        if (adminUpdated == 0) {
            log.warn("[Escrow] 관리자 계정 없음 — 수수료 {}원 미지급", payment.getPlatformFee());
        }

        // 프로젝트 → DONE
        em.createNativeQuery("UPDATE project_tb SET project_status = 'DONE' WHERE id = :pid")
                .setParameter("pid", payment.getProjectId())
                .executeUpdate();

        req.approve();

        log.info("[Escrow] 관리자 정산승인 — requestId={}, paymentId={}, netAmount={}",
                requestId, req.getPaymentId(), payment.getNetAmount());
    }

    @Transactional
    public void adminRejectSettlement(Long requestId) {
        EscrowSettlementRequest req = escrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("정산 요청을 찾을 수 없습니다."));

        if (req.getStatus() != EscrowRequestStatus.PENDING) {
            throw new BadRequestException("이미 처리된 요청입니다.");
        }

        req.reject();
        log.info("[Escrow] 정산요청 반려 — requestId={}", requestId);
    }

    // ── 관리자 에스크로 목록 조회 ──

    public List<AdminEscrowDTO> getAdminEscrowList(String status) {
        List<EscrowSettlementRequest> list;
        if ("PENDING".equals(status)) {
            list = escrowRequestRepository.findAllByStatusOrderByCreatedAtDesc(EscrowRequestStatus.PENDING);
        } else if ("APPROVED".equals(status)) {
            list = escrowRequestRepository.findAllByStatusOrderByCreatedAtDesc(EscrowRequestStatus.APPROVED);
        } else if ("REJECTED".equals(status)) {
            list = escrowRequestRepository.findAllByStatusOrderByCreatedAtDesc(EscrowRequestStatus.REJECTED);
        } else {
            list = escrowRequestRepository.findAllByOrderByCreatedAtDesc();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");

        return list.stream().map(req -> {
            Payment payment = paymentRepository.findById(req.getPaymentId()).orElse(null);
            String expertName = memberRepository.findById(req.getExpertId())
                    .map(m -> m.getName()).orElse("(알 수 없음)");
            String expertEmail = memberRepository.findById(req.getExpertId())
                    .map(m -> m.getEmail()).orElse("");
            String statusLabel = switch (req.getStatus()) {
                case PENDING  -> "대기중";
                case APPROVED -> "승인";
                case REJECTED -> "반려";
            };
            return new AdminEscrowDTO(
                    req.getId(),
                    req.getPaymentId(),
                    req.getProjectId(),
                    expertName,
                    expertEmail,
                    payment != null ? String.format("%,d", payment.getAmount()) : "-",
                    payment != null ? String.format("%,d", payment.getNetAmount()) : "-",
                    sdf.format(java.sql.Timestamp.valueOf(req.getCreatedAt())),
                    req.getMessage(),
                    statusLabel,
                    req.getStatus() == EscrowRequestStatus.PENDING,
                    req.getStatus() == EscrowRequestStatus.APPROVED,
                    req.getStatus() == EscrowRequestStatus.REJECTED
            );
        }).collect(Collectors.toList());
    }

    // ── 전문가 정산요청 현황 조회 ──

    public Set<Long> getPendingSettlementPaymentIds(Long expertId) {
        return escrowRequestRepository.findPendingPaymentIdsByExpertId(expertId);
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

    // ── DTO ──

    public record AdminEscrowDTO(
            Long   requestId,
            Long   paymentId,
            Long   projectId,
            String expertName,
            String expertEmail,
            String amount,
            String netAmount,
            String requestDate,
            String message,
            String statusLabel,
            boolean isPending,
            boolean isApproved,
            boolean isRejected
    ) {}
}
