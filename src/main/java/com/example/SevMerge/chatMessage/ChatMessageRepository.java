package com.example.SevMerge.chatMessage;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방의 메시지 목록 (오래된 순). 보는 사람 기준 삭제 필터는 서비스에서 처리.
    @Query("""
           SELECT cm FROM ChatMessage cm
           WHERE cm.chatRoom.id = :roomId
           ORDER BY cm.createdAt ASC
           """)
    Page<ChatMessage> findByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    // 방의 마지막 메시지 1건 (목록 미리보기용)
    ChatMessage findTopByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

    // 안 읽은 메시지 수 (내가 받은 = 내가 보낸 게 아닌 것 중 미열람, 내가 안 지운 것)
    @Query("""
           SELECT COUNT(cm) FROM ChatMessage cm
           WHERE cm.chatRoom.id = :roomId
             AND cm.sender.id <> :memberId
             AND cm.isRead = false
             AND cm.deletedByReceiver = false
           """)
    long countUnread(@Param("roomId") Long roomId, @Param("memberId") Long memberId);

    // 방 입장 시 내가 받은 메시지 일괄 읽음 처리
    @Modifying(clearAutomatically = true)
    @Query("""
           UPDATE ChatMessage cm SET cm.isRead = true
           WHERE cm.chatRoom.id = :roomId AND cm.sender.id <> :memberId AND cm.isRead = false
           """)
    void markRoomRead(@Param("roomId") Long roomId, @Param("memberId") Long memberId);

    // 방 하드 삭제 시 메시지 일괄 삭제
    @Modifying
    @Query("DELETE FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId")
    void deleteByRoomId(@Param("roomId") Long roomId);
}
