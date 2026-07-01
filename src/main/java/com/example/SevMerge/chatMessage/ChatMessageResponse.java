package com.example.SevMerge.chatMessage;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class ChatMessageResponse {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private Long id;
    private Long senderId;
    private String senderName;
    private String text;
    private String imageUrl;
    private String type;
    private Timestamp createdAt;
    private String date;   // yyyy/MM/dd (날짜 구분선용)
    private String time;   // HH:mm (메시지 옆 시간)

    public static ChatMessageResponse from(ChatMessage message) {
        LocalDateTime dt = message.getCreatedAt() != null ? message.getCreatedAt().toLocalDateTime() : null;
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .text(message.getText() != null ? message.getText() : "")
                .imageUrl(message.getImageUrl() != null ? message.getImageUrl() : "")
                .createdAt(message.getCreatedAt())
                .date(dt != null ? dt.format(DATE_FMT) : "")
                .time(dt != null ? dt.format(TIME_FMT) : "")
                .build();
    }
}
