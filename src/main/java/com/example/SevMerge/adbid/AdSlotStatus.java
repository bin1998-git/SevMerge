package com.example.SevMerge.adbid;

public enum AdSlotStatus {
    OPEN,    // 입찰 진행중
    CLOSED,  // 입찰 마감 (낙찰자 선정 완료)
    AWARDED, // 광고 노출중
    EXPIRED  // 종료
}