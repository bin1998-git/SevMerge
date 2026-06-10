package com.example.SevMerge.message;

import lombok.Data;

public class MessageRequest {

    @Data
    public static class SendDTO {
        private Long receiverId;
        private Long projectId;
        private String title;
        private String content;
    }

}
