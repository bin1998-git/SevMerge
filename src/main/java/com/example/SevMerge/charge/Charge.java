package com.example.SevMerge.charge;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * Charge Entity — 지갑 충전 내역
 *
 * [역할]
 * - TossPayments 결제를 통해 사용자가 잔액(balance)을 충전한 기록
 * - 실제 돈이 오가는 유일한 Toss 연동 지점
 *
 * [충돌 방지 전략]
 * - Member Entity 직접 참조 없이 memberId(Long)만 보관
 */
@Entity
@Table(name = "charge_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 충전 요청 회원 ID
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // 충전 금액
    @Column(nullable = false)
    private Integer amount;

    // 주문번호 — Toss가 요구하는 고유값 (중복 방지)
    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId;

    // 토스 결제키 (승인 완료 후 저장)
    @Column(name = "payment_key", length = 255)
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChargeStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    // ── 비즈니스 메서드 ──

    public void complete(String paymentKey) {
        if (this.status == ChargeStatus.DONE) {
            throw new IllegalStateException("이미 완료된 충전입니다.");
        }
        this.paymentKey = paymentKey;
        this.status = ChargeStatus.DONE;
    }

    public void fail() {
        this.status = ChargeStatus.FAILED;
    }
}
