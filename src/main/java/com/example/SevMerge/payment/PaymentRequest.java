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
     * 낙찰 처리 후 결제 페이지 진입 시 사용 (팀장 BidController → /payment/form 리다이렉트)
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
     * PAY-003: 포트원 결제 완료 확인 DTO
     * 포트원 SDK 결제 후 프론트에서 서버로 전달하는 데이터
     *
     * merchantUid 형식: "sev-project-{projectId}"
     */
    @Data
    public static class ConfirmRequest {

        @NotBlank(message = "imp_uid는 필수입니다.")
        private String impUid;          // 포트원 결제 고유번호

        @NotBlank(message = "merchant_uid는 필수입니다.")
        private String merchantUid;     // 주문번호: "sev-project-{projectId}"

        @NotNull(message = "결제 금액은 필수입니다.")
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        private Integer paidAmount;

        @NotNull(message = "전문가 ID는 필수입니다.")
        private Long expertId;

        private String payMethod;       // 결제 수단 (card, kakaopay 등)
    }

    /**
     * PAY-005: 환불 요청 DTO
     */
    @Data
    public static class RefundRequest {
        private String reason;          // 환불 사유 (선택)
    }
}
