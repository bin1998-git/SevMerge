package com.example.SevMerge.notification;

public enum NotificationType {
                        ///  수신자 | 알림 발생 시점
    NEW_BID,            ///  의뢰인 | 전문가가 내 프로젝트에 제안서 제출
    BID_SELECTED,       ///  전문가 | 내 제안서가 낙찰됨
    BID_REJECTED,       ///  전문가 | 내 제안서가 거부됨
    EXPERT_APPROVED,    ///  전문가 | 관리자가 전문가 신청 승인
    EXPERT_REJECTED,    ///  전문가 | 관리자가 전문가 신청 거부
    PAYMENT_COMPLETED   ///  전문가 | 결제 완료 (작업 시작 가능)
}
