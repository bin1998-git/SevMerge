package com.example.SevMerge.payment;

import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;


@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Member client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private Member expert;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "platform_fee", nullable = false)
    private Integer platformFee;

    @Column(name = "net_amount", nullable = false)
    private Integer netAmount;

    @Column(name = "payment_key", length = 255)
    private String paymentKey;

    @Column(length = 50)
    private String method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @CreationTimestamp
    @Column(name = "paid_at", nullable = false, updatable = false)
    private Timestamp paidAt;

    public void settle() {
        if (this.status != PaymentStatus.PAID) {
            throw new IllegalStateException("PAID 상태에서만 정산이 가능합니다.");
        }
        this.status = PaymentStatus.SETTLED;
    }

    public void refund() {
        if (this.status != PaymentStatus.PAID) {
            throw new IllegalStateException("PAID 상태에서만 환불이 가능합니다.");
        }
        this.status = PaymentStatus.REFUNDED;
    }
}