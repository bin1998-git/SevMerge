//package com.example.SevMerge.chatRoom;
//
//import lombok.Builder;
//import lombok.Data;
//
//import java.sql.Timestamp;
//
//@Data
//@Builder
//public class ChatRoomResponse {
//
//    private Long id;           // 채팅방 ID
//    private Long projectId;       // 프로젝트 ID
//    private String projectTitle;  // 프로젝트 제목 (목록에서 표시용)
//    private Long clientId;        // 의뢰인 ID
//    private String clientName;    // 의뢰인 이름
//    private Long expertId;        // 전문가 ID
//    private String expertName;    // 전문가 이름
//    private Timestamp createdAt;   // 채팅방 생성 시각
//
//    public static ChatRoomResponse from(ChatRoom chatRoom) {
//        return ChatRoomResponse
//                .builder()
//                .id(chatRoom.getId())
//                .projectId(chatRoom.getProject().getId())
//                .clientId(chatRoom.getClient().getId())
//                .clientName(chatRoom.getClient().getName())
//                .expertId(chatRoom.getExpert().getId())
//                .expertName(chatRoom.getExpert().getName())
//                .createdAt(chatRoom.getCreatedAt())
//                .build();
//
//    }
//
//}
