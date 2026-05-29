package com.example.SevMerge.payment;

public enum PaymentStatus {
    PAID,       // 결제 완료 (에스크로 보관 중)
    SETTLED,    // 정산 완료 (전문가에게 지급)
    REFUNDED    // 환불 완료
}
