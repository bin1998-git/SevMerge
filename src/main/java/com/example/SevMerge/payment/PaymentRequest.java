package com.example.SevMerge.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Payment 관련 요청 DTO 모음
 * 요구사항 ID: PAY-001, PAY-003, PAY-005
 */
public class PaymentRequest {

    /**
     * PAY-001: 결제 페이지 요청 DTO
     */
    @Data
    public static class PayRequest {

        @NotNull(message = "프로젝트 ID는 필수입니다.")
        private Long projectId;

        @NotNull(message = "전문가 ID는 필수입니다.")
        private Long expertId;

        @NotNull(message = "결제 금액은 필수입니다.")
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        private Integer amount;
    }

    /**
     * PAY-003: 토스페이먼츠 결제 승인 DTO
     * 토스 JS SDK 결제 완료 후 successUrl 쿼리파라미터로 전달되는 값
     *
     * orderId 형식: "sev-project-{projectId}"
     */
    @Data
    public static class TossConfirmRequest {

        @NotBlank(message = "paymentKey는 필수입니다.")
        private String paymentKey;   // 토스 결제 고유키

        @NotBlank(message = "orderId는 필수입니다.")
        private String orderId;      // 주문번호: "sev-project-{projectId}"

        @NotNull(message = "결제 금액은 필수입니다.")
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        private Integer amount;

        @NotNull(message = "전문가 ID는 필수입니다.")
        private Long expertId;

        private String method;       // 결제 수단 (카드, 간편결제 등)
    }

    /**
     * PAY-005: 환불 요청 DTO
     */
    @Data
    public static class RefundRequest {
        private String reason;       // 환불 사유 (선택)
    }
}
