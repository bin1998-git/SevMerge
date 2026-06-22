package com.example.SevMerge.notification;

public enum NotificationType {
                                              ///  수신자 | 알림 발생 시점
    NEW_BID("새 제안서 도착"),          ///  의뢰인 | 전문가가 내 프로젝트에 제안서 제출
    BID_SELECTED("제안 낙찰"),          ///  전문가 | 내 제안서가 낙찰됨
    BID_REJECTED("제안 거절"),          ///  전문가 | 내 제안서가 거부됨
    EXPERT_APPROVED("전문가 승인"),     ///  전문가 | 관리자가 전문가 신청 승인
    EXPERT_REJECTED("전문가 거절"),     ///  전문가 | 관리자가 전문가 신청 거부
    PAYMENT_COMPLETED("결제 완료"),     ///  전문가 | 결제 완료 (작업 시작 가능)
    MESSAGE_RECEIVED("새 쪽지 도착"),   ///  수신자 | 새 쪽지 도착
    CHAT_RECEIVED("새 채팅 도착"),      ///  수신자 | 새 채팅 메시지 도착
    EXPERT_SUSPENDED("전문가 계정 정지"),///  의뢰인 | 진행 중인 전문가 계정 정지됨
    REFUND_APPROVED("환불 승인 완료"),  /// 전문가 | 의뢰인의 환불 신청이 관리자 승인되어 처리됨
    REFUND_REJECTED("환불 거절");      /// 의뢰인 | 내 환불 신청이 관리자에 의해 거절됨

    private final String label;
    NotificationType(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
