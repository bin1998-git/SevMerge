package com.example.SevMerge.payment;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * PaymentService
 * 요구사항: PAY-001 ~ PAY-006
 *
 * [충돌 방지 전략]
 * - 타 도메인 Service 직접 주입 없음
 * - 프로젝트 상태 변경은 EntityManager 네이티브 쿼리로 처리
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

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    // ===================== PAY-003: 토스 결제 승인 =====================

    /**
     * PAY-003: 토스 결제 승인 처리
     * successUrl 콜백 → 토스 confirm API 호출 → DB 저장 → 프로젝트 상태 변경
     */
    @Transactional
    public PaymentResponse confirmTossPayment(Long clientId,
                                              String paymentKey,
                                              String orderId,
                                              Integer amount,
                                              Long expertId,
                                              String method) {

        // 1. orderId에서 projectId 파싱 ("sev-project-{id}")
        Long projectId = parseProjectId(orderId);

        // 2. 중복 결제 방지
        if (paymentRepository.existsByProjectId(projectId)) {
            throw new BadRequestException("이미 결제가 완료된 프로젝트입니다.");
        }

        // 3. 토스 서버에 결제 승인 요청
        callTossConfirmApi(paymentKey, orderId, amount);

        // 4. 수수료 계산
        int platformFee = (int) (amount * PLATFORM_FEE_RATE);
        int netAmount   = amount - platformFee;

        // 5. DB 저장
        Payment payment = Payment.builder()
                .projectId(projectId)
                .clientId(clientId)
                .expertId(expertId)
                .amount(amount)
                .platformFee(platformFee)
                .netAmount(netAmount)
                .paymentKey(paymentKey)
                .method(method)
                .status(PaymentStatus.PAID)
                .build();
        paymentRepository.save(payment);

        // 6. 프로젝트 상태 → IN_PROGRESS
        em.createNativeQuery("UPDATE project SET status = 'IN_PROGRESS' WHERE id = :pid")
                .setParameter("pid", projectId)
                .executeUpdate();

        log.info("[Payment] 토스 결제 완료 - projectId={}, clientId={}, amount={}", projectId, clientId, amount);
        return PaymentResponse.from(payment);
    }

    /**
     * 토스 결제 승인 API 호출
     * POST https://api.tosspayments.com/v1/payments/confirm
     */
    private void callTossConfirmApi(String paymentKey, String orderId, Integer amount) {
        RestTemplate restTemplate = new RestTemplate();

        String auth = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + auth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId",    orderId);
        body.put("amount",     amount);

        try {
            restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/confirm",
                    new HttpEntity<>(body, headers),
                    String.class
            );
        } catch (Exception e) {
            log.error("[Payment] 토스 결제 승인 실패 - {}", e.getMessage());
            throw new BadRequestException("토스 결제 승인 실패: " + e.getMessage());
        }
    }

    // ===================== PAY-004: 정산 처리 =====================

    /**
     * PAY-004: PAID → SETTLED
     */
    @Transactional
    public PaymentResponse settle(Long paymentId, Long requesterId) {
        Payment payment = findPaymentById(paymentId);

        if (!Objects.equals(payment.getClientId(), requesterId)) {
            throw new ForbiddenException("정산 권한이 없습니다.");
        }

        try {
            payment.settle();
        } catch (IllegalStateException e) {
            throw new BadRequestException(e.getMessage());
        }

        log.info("[Payment] 정산 처리 - paymentId={}, expertId={}, netAmount={}",
                paymentId, payment.getExpertId(), payment.getNetAmount());
        return PaymentResponse.from(payment);
    }

    // ===================== PAY-005: 환불 처리 =====================

    /**
     * PAY-005: 토스 환불 API 호출 → PAID → REFUNDED
     */
    @Transactional
    public PaymentResponse refund(Long paymentId, Long requesterId) {
        Payment payment = findPaymentById(paymentId);

        if (!Objects.equals(payment.getClientId(), requesterId)) {
            throw new ForbiddenException("환불 권한이 없습니다.");
        }

        if (payment.getStatus() == PaymentStatus.SETTLED) {
            throw new BadRequestException("이미 정산이 완료된 건입니다. 관리자에게 문의해주세요.");
        }

        // 토스 환불 API 호출
        callTossCancelApi(payment.getPaymentKey(), "의뢰인 환불 요청");

        try {
            payment.refund();
        } catch (IllegalStateException e) {
            throw new BadRequestException(e.getMessage());
        }

        // 프로젝트 상태 → CANCELLED
        em.createNativeQuery("UPDATE project SET status = 'CANCELLED' WHERE id = :pid")
                .setParameter("pid", payment.getProjectId())
                .executeUpdate();

        log.info("[Payment] 환불 완료 - paymentId={}, amount={}", paymentId, payment.getAmount());
        return PaymentResponse.from(payment);
    }

    /**
     * 토스 결제 취소 API 호출
     * POST https://api.tosspayments.com/v1/payments/{paymentKey}/cancels
     */
    private void callTossCancelApi(String paymentKey, String cancelReason) {
        RestTemplate restTemplate = new RestTemplate();

        String auth = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + auth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", cancelReason);

        try {
            restTemplate.postForEntity(
                    "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancels",
                    new HttpEntity<>(body, headers),
                    String.class
            );
        } catch (Exception e) {
            log.error("[Payment] 토스 환불 실패 - {}", e.getMessage());
            throw new BadRequestException("토스 환불 실패: " + e.getMessage());
        }
    }

    // ===================== PAY-006: 결제 내역 조회 =====================

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

    // ===================== private =====================

    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
    }

    private Long parseProjectId(String orderId) {
        try {
            // "sev-project-123" → index 2
            String[] parts = orderId.split("-");
            return Long.parseLong(parts[2]);
        } catch (Exception e) {
            throw new BadRequestException("유효하지 않은 주문번호입니다: " + orderId);
        }
    }
}
