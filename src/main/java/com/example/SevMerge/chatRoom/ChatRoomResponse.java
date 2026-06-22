package com.example.SevMerge.chatRoom;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ChatRoomResponse {

    private Long id;           // 채팅방 ID
    private Long projectId;       // 프로젝트 ID
    private String projectTitle;  // 프로젝트 제목 (목록에서 표시용)
    private Long clientId;        // 의뢰인 ID
    private String clientName;    // 의뢰인 이름
    private Long expertId;        // 전문가 ID
    private String expertName;    // 전문가 이름
    private String displayName;   // 목록 표시명 (보는 사람 기준: 프로젝트명 또는 상대 이름)
    private String lastMessage;   // 마지막 메시지 미리보기
    private long unreadCount;     // 안 읽은 메시지 수
    private boolean hasUnread;    // 안 읽은 메시지 존재 여부 (뱃지 표시용)
    private Timestamp createdAt;   // 채팅방 생성 시각

    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return ChatRoomResponse
                .builder()
                .id(chatRoom.getId())
                // 프로필 문의 방은 project가 null → null 가드
                .projectId(chatRoom.getProject() != null ? chatRoom.getProject().getId() : null)
                .projectTitle(chatRoom.getProject() != null ? chatRoom.getProject().getTitle() : null)
                .clientId(chatRoom.getClient().getId())
                .clientName(chatRoom.getClient().getName())
                .expertId(chatRoom.getExpert().getId())
                .expertName(chatRoom.getExpert().getName())
                .createdAt(chatRoom.getCreatedAt())
                .build();

    }

}
