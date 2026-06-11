package com.example.SevMerge.charge;

import com.example.SevMerge.core.exception.BadRequestException;
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

/**
 * ChargeService — 지갑 충전
 *
 * [흐름]
 * 1. 프론트: TossPayments SDK → successUrl(/charge/success?paymentKey=..&orderId=..&amount=..)
 * 2. confirmCharge(): Toss confirm API → Charge 저장 → member.balance += amount
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChargeService {

    private final ChargeRepository chargeRepository;

    @PersistenceContext
    private EntityManager em;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    // ── 충전 승인 ──

    /**
     * Toss 결제 성공 콜백 처리
     * 1) Toss confirm API 호출
     * 2) Charge 레코드 저장 (DONE)
     * 3) member.balance += amount (JPQL 업데이트)
     */
    @Transactional
    public ChargeResponse confirmCharge(Long memberId,
                                        String paymentKey,
                                        String orderId,
                                        Integer amount) {

        // 중복 처리 방지 (네트워크 재시도 등)
        chargeRepository.findByOrderId(orderId).ifPresent(existing -> {
            if (existing.getStatus() == ChargeStatus.DONE) {
                throw new BadRequestException("이미 처리된 충전입니다.");
            }
        });

        // Toss confirm API
        callTossConfirmApi(paymentKey, orderId, amount);

        // Charge 저장
        Charge charge = Charge.builder()
                .memberId(memberId)
                .amount(amount)
                .orderId(orderId)
                .paymentKey(paymentKey)
                .status(ChargeStatus.DONE)
                .build();
        chargeRepository.save(charge);

        // 잔액 증가 (JPQL — Member 엔티티 직접 import 없이 처리)
        em.createQuery("UPDATE Member m SET m.balance = m.balance + :amount WHERE m.id = :id")
                .setParameter("amount", amount)
                .setParameter("id", memberId)
                .executeUpdate();

        log.info("[Charge] 충전 완료 — memberId={}, amount={}, orderId={}", memberId, amount, orderId);
        return ChargeResponse.from(charge);
    }

    // ── 충전 내역 조회 ──

    public List<ChargeResponse> getMyCharges(Long memberId) {
        return chargeRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream().map(ChargeResponse::from).toList();
    }

    /**
     * 현재 잔액 조회 (Mustache에서 모델 주입용)
     */
    public Integer getBalance(Long memberId) {
        return (Integer) em.createQuery("SELECT m.balance FROM Member m WHERE m.id = :id")
                .setParameter("id", memberId)
                .getSingleResult();
    }

    // ── Toss API ──

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
            log.error("[Charge] Toss 충전 승인 실패 — {}", e.getMessage());
            throw new BadRequestException("결제 승인 실패: " + e.getMessage());
        }
    }
}
