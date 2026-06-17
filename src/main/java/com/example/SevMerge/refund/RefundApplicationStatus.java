package com.example.SevMerge.refund;

public enum RefundApplicationStatus {
    PENDING,    // 의뢰인 신청, 관리자 검토 대기
    APPROVED,   // 관리자 승인 -> 환불 처리 완료
    REJECTED    // 관리자 거절
}