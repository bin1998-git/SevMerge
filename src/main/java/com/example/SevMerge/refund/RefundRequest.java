package com.example.SevMerge.refund;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * Entity
 *
 * 충돌 방지
 *  Payment, Member 등 다른 도메인 Entity를 직접 참조하지 않고
 *   FK를 Long 타입으로만 보관함. @ManyToOne 미사용
 */
@Entity
@Table(name = "refund_application_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefundRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "expert_id", nullable = false)
    private Long expertId;

    @Column(nullable = false, length = 500)
    private String reason;          // 의뢰인이 작성한 환불 사유

    @Column(name = "admin_comment", length = 500)
    private String adminComment;    // 관리자 검토 의견(승인/거절 사유)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RefundApplicationStatus status = RefundApplicationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    // 환불사유 카테고리
    @Column(name = "reason_category", length = 50)
    private String reasonCategory;

    // 비즈니스 메서드

    public void approve(String comment) {
        if (this.status != RefundApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }
        this.status = RefundApplicationStatus.APPROVED;
        this.adminComment = comment;
    }

    public void reject(String comment) {
        if (this.status != RefundApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 신청입니다.");
        }
        this.status = RefundApplicationStatus.REJECTED;
        this.adminComment = comment;
    }

    public boolean isPending() {
        return this.status == RefundApplicationStatus.PENDING;
    }
}