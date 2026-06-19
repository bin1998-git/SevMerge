package com.example.SevMerge.message;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MessageRequest {

    @Data
    public static class SendDTO {
        private Long receiverId;
        private Long projectId;
        private String title;
        private String content;
        private List<MultipartFile> files;
    }

}
