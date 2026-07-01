package com.example.SevMerge.chatMessage;

import com.example.SevMerge.chatRoom.ChatRoom;
import com.example.SevMerge.chatRoom.ChatRoomRepository;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    @Transactional
    public ChatMessageResponse saveMessage(ChatMessageRequest reqDTO, Member sessionMember) {
        ChatRoom chatRoom = chatRoomRepository.findById(reqDTO.getChatRoomId())
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));

        if (!chatRoom.validate(sessionMember)) {
            throw new ForbiddenException("채팅방에 접근할 권한이 없습니다.");
        }
        boolean hasText = reqDTO.getText() != null && !reqDTO.getText().isEmpty();
        boolean hasImage = reqDTO.getImageUrl() != null && !reqDTO.getImageUrl().isEmpty();
        if (!hasText && !hasImage) {
            throw new BadRequestException("메시지 내용을 입력하세요.");
        }

        // 수신자 = 상대방
        Member receiver = chatRoom.getClient().getId().equals(sessionMember.getId())
                ? chatRoom.getExpert() : chatRoom.getClient();

        // 알림: 이 방에 수신자의 안 읽은 메시지가 0일 때(= 새 대화 시작)만 1회
        boolean firstUnread = chatMessageRepository.countUnread(chatRoom.getId(), receiver.getId()) == 0;

        // 새 메시지 → 방 삭제 플래그 해제 (내가/상대가 나갔어도 다시 보이게)
        chatRoom.restore();

        ChatMessage message = chatMessageRepository.save(ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sessionMember)
                .text(reqDTO.getText() != null ? reqDTO.getText() : "")
                .imageUrl(reqDTO.getImageUrl())
                .isDeleted(false)
                .build());

        if (firstUnread) {
            notificationService.notifyChatReceived(receiver, sessionMember.getName(), chatRoom.getId());
        }

        return ChatMessageResponse.from(message);
    }

    // 메세지 목록 조회 (페이징 처리)
    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> getMessages(Long chatRoomId, int page, int size, Member sessionMember) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));

        // 권한 검증
        if (!chatRoom.validate(sessionMember)) {
            throw new ForbiddenException("채팅방 접근 권한이 없습니다.");
        }

        Pageable pageable = PageRequest.of(page, size);

        return chatMessageRepository.findByRoomId(chatRoomId, pageable).map(message -> ChatMessageResponse.from(message));
    }

    // 메시지 삭제. forAll=true: 모두에게서(보낸 사람만), false: 나에게서. 양쪽 다 삭제되면 하드 딜리트
    @Transactional
    public void deleteMessage(Long messageId, Member member, boolean forAll) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("메시지를 찾을 수 없습니다."));

        if (!message.getChatRoom().validate(member)) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        if (forAll) {
            if (!message.isSentBy(member)) {
                throw new ForbiddenException("보낸 메시지만 모두에게서 삭제할 수 있습니다.");
            }
            message.deleteForAll();
        } else {
            message.deleteFor(member);
        }

        // 보낸/받은 사람 모두 삭제 → 하드 딜리트
        if (message.isFullyDeleted()) {
            chatMessageRepository.delete(message);
        }
    }

    // 방 입장 시 내가 받은 메시지 읽음 처리
    @Transactional
    public void markRoomAsRead(Long chatRoomId, Member sessionMember) {
        chatMessageRepository.markRoomRead(chatRoomId, sessionMember.getId());
    }

    // 채팅방 입장 시 과거 메시지 (오래된 순, 내가 삭제한 건 제외)
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getRoomMessages(Long chatRoomId, Member sessionMember) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));
        if (!chatRoom.validate(sessionMember)) {
            throw new ForbiddenException("채팅방 접근 권한이 없습니다.");
        }
        return chatMessageRepository.findByRoomId(chatRoomId, Pageable.unpaged())
                .getContent().stream()
                .filter(m -> !m.isDeletedFor(sessionMember))
                .map(ChatMessageResponse::from)
                .toList();
    }

}
