package com.example.SevMerge.message;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

public class MessageResponse {

    // 쪽지 목록
    @Data
    @Builder
    public static class ListDTO {
        private Long id;
        private String senderName;
        private String title;
        private Boolean isRead;
        private Timestamp createdAt;
    }

    // 쪽지 상세
    @Data
    @Builder
    public static class DetailDTO {
        private Long id;
        private String senderName;
        private String receiverName;
        private String projectTitle;  // project nullable이니 String으로
        private String title;
        private String content;
        private Boolean isRead;
        private Timestamp createdAt;
    }

}
