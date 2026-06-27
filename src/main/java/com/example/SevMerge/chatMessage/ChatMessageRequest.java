package com.example.SevMerge.chatMessage;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long messageId;
    private Long chatRoomId;
    private String text;
    private String imageUrl;
}
