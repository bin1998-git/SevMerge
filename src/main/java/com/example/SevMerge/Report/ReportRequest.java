package com.example.SevMerge.Report;

import lombok.Data;

public class ReportRequest {
    @Data
    public static class SaveDTO {
        private String reason;
        private String contentDetail;

        public void validate() {
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("신고 사유 분류를 반드시 선택해 주세요.");
            }
            if (contentDetail == null || contentDetail.trim().isEmpty()) {
                throw new IllegalArgumentException("상세 신고 내용을 최소 1글자 이상 입력해주세요.");
            }
        }

    }
}
