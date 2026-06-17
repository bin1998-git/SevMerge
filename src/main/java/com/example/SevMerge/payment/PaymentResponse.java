package com.example.SevMerge.payment;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * Payment 응답 DTO
 * PAY-006: 결제/정산 내역 조회 응답, 결제 완료 응답에 사용
 */
@Data
@Builder
public class PaymentResponse {

    private Long id;
    private Long projectId;
    private Long clientId;
    private Long expertId;
    private Integer amount;
    private Integer platformFee;
    private Integer netAmount;
    private String paymentKey;
    private String method;
    private String status;
    private Timestamp paidAt;

    // Mustache boolean 조건 ({{#isPaid}}, {{#isSettled}}, {{#isRefunded}})
    private boolean isPaid;
    private boolean isSettled;
    private boolean isRefunded;


    public static PaymentResponse from(Payment payment) {
        String statusName = payment.getStatus().name();
        return PaymentResponse.builder()
                .id(payment.getId())
                .projectId(payment.getProjectId())
                .clientId(payment.getClientId())
                .expertId(payment.getExpertId())
                .amount(payment.getAmount())
                .platformFee(payment.getPlatformFee())
                .netAmount(payment.getNetAmount())
                .paymentKey(payment.getPaymentKey())
                .method(payment.getMethod())
                .status(statusName)
                .paidAt(payment.getPaidAt())
                .isPaid("PAID".equals(statusName))
                .isSettled("SETTLED".equals(statusName))
                .isRefunded("REFUNDED".equals(statusName))
                .build();
    }
    public String getDisplayAmount() {
        return amount == null ? "0" : String.format("%,d", amount);
    }
}
