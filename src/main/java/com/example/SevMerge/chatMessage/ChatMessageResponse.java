//package com.example.SevMerge.chatMessage;
//
//import lombok.Builder;
//import lombok.Data;
//
//import java.sql.Timestamp;
//
//@Data
//@Builder
//public class ChatMessageResponse {
//    private Long id;
//    private Long senderId;
//    private String senderName;
//    private String text;
//    private Timestamp createdAt;
//
//    public static ChatMessageResponse from(ChatMessage message) {
//        return ChatMessageResponse.builder()
//                 .id(message.getId())
//                 .senderId(message.getSender().getId())
//                 .senderName(message.getSender().getName())
//                 .text(message.getText())
//                 .createdAt(message.getCreatedAt())
//                 .build();
//    }
//}
