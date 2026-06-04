//package com.example.SevMerge.chatMessage;
//
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
//
//    // 특정 채팅방의 메시지 목록 - 페이지네이션 적용
//    // → 삭제된 메시지 제외, 최신순 조회
//    // → ChatMessageService.getMessages() 에서 호출
//    // → Page<T> 반환으로 페이지네이션 자동 처리
//    @Query("""
//           SELECT cm FROM ChatMessage cm
//           WHERE cm.chatRoom.id = :roomId AND cm.isDeleted = false
//           ORDER BY cm.createdAt ASC
//           """)
//    Page<ChatMessage> findByRoomId(@Param("roomId") Long roomId, Pageable pageable);
//}
