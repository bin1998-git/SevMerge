//package com.example.SevMerge.chatMessage;
//
//import com.example.SevMerge.chatRoom.ChatRoom;
//import com.example.SevMerge.chatRoom.ChatRoomRepository;
//import com.example.SevMerge.core.exception.BadRequestException;
//import com.example.SevMerge.core.exception.ForbiddenException;
//import com.example.SevMerge.core.exception.NotFoundException;
//import com.example.SevMerge.member.Member;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class ChatMessageService {
//
//    private final ChatMessageRepository chatMessageRepository;
//    private final ChatRoomRepository chatRoomRepository;
//
//    public ChatMessageResponse saveMessage(ChatMessageRequest reqDTO, Member sessionMember) {
//        // 1. 채팅방 존재 여부 확인
//        ChatRoom chatRoom = chatRoomRepository.findById(reqDTO.getChatRoomId())
//                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));
//
//        // 권한 검증
//        if (!chatRoom.validate(sessionMember)) {
//            throw new ForbiddenException("채팅방에 접근할 권한이 없습니다.");
//        }
//
//        if (reqDTO.getText() == null || reqDTO.getText().isEmpty()) {
//            throw new BadRequestException("메시지 내용을 입력하세요.");
//        }
//
//        ChatMessage message = ChatMessage.builder()
//                .chatRoom(chatRoom)
//                .sender(sessionMember)
//                .text(reqDTO.getText())
//                .isDeleted(false)
//                .build();
//
//        return ChatMessageResponse.from(chatMessageRepository.save(message));
//
//    }
//
//    // 메세지 목록 조회 (페이징 처리)
//    @Transactional(readOnly = true)
//    public Page<ChatMessageResponse> getMessages(Long chatRoomId, int page, int size, Member sessionMember) {
//
//        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
//                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));
//
//        // 권한 검증
//        if (!chatRoom.validate(sessionMember)) {
//            throw new ForbiddenException("채팅방 접근 권한이 없습니다.");
//        }
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        return chatMessageRepository.findByRoomId(chatRoomId, pageable).map(message -> ChatMessageResponse.from(message));
//    }
//
//    // 메시지 소프트 딜리트
//    // 본인 메시지만 삭제 가능
//    @Transactional
//    public void deleteMessage(Long messageId, Member loginUser) {
//
//        ChatMessage message = chatMessageRepository.findById(messageId)
//                .orElseThrow(() -> new NotFoundException("메시지를 찾을 수 없습니다."));
//
//        // 본인 메시지인지 검증
//        if (!message.getSender().getId().equals(loginUser.getId())) {
//            throw new ForbiddenException("본인 메시지만 삭제할 수 있습니다.");
//        }
//
//        // 소프트 딜리트
//        message.setIsDeleted(true);
//    }
//
//
//}
