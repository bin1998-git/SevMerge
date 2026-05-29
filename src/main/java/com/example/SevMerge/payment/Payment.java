package com.example.SevMerge.payment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * Payment Entity
 * ERD 기준 결제 테이블
 *
 * [충돌 방지 전략]
 * - Project(팀장), Member(팀원B) 등 타 도메인 Entity를 직접 참조하지 않고
 *   FK를 Long 타입으로만 보관합니다. (@ManyToOne 미사용)
 * - 타 도메인 Entity가 수정되어도 이 파일에는 영향이 없습니다.
 */
@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Project 1:1 FK (팀장 담당 - 직접 참조 없이 ID만 보관)
    @Column(name = "project_id", nullable = false, unique = true)
    private Long projectId;

    // 의뢰인 Member FK (팀원B 담당 - 직접 참조 없이 ID만 보관)
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    // 전문가 Member FK (팀원B 담당 - 직접 참조 없이 ID만 보관)
    @Column(name = "expert_id", nullable = false)
    private Long expertId;

    // 총 결제 금액
    @Column(nullable = false)
    private Integer amount;

    // 플랫폼 수수료 (amount * 10%)
    @Column(name = "platform_fee", nullable = false)
    private Integer platformFee;

    // 전문가 수령액 (amount - platformFee)
    @Column(name = "net_amount", nullable = false)
    private Integer netAmount;

    // 포트원 imp_uid (결제 고유번호)
    @Column(name = "payment_key", length = 255)
    private String paymentKey;

    // 결제 수단 (card, kakaopay 등)
    @Column(length = 50)
    private String method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @CreationTimestamp
    @Column(name = "paid_at", nullable = false, updatable = false)
    private Timestamp paidAt;

    // ===================== 비즈니스 메서드 =====================

    /**
     * PAY-004: 정산 처리 - PAID → SETTLED
     */
    public void settle() {
        if (this.status != PaymentStatus.PAID) {
            throw new IllegalStateException("PAID 상태에서만 정산이 가능합니다.");
        }
        this.status = PaymentStatus.SETTLED;
    }

    /**
     * PAY-005: 환불 처리 - PAID → REFUNDED
     */
    public void refund() {
        if (this.status != PaymentStatus.PAID) {
            throw new IllegalStateException("PAID 상태에서만 환불이 가능합니다.");
        }
        this.status = PaymentStatus.REFUNDED;
    }
}
