package com.example.SevMerge.charge;

public enum ChargeStatus {
    PENDING,  // 결제 시도 중 (Toss 창 열린 상태)
    DONE,     // 충전 완료
    FAILED    // 충전 실패
}
