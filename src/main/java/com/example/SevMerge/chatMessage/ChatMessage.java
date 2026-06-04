package com.example.SevMerge.chatMessage;

import com.example.SevMerge.chatRoom.ChatRoom;
import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name = "chat_message_tb")
@Entity
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom; // 채팅방 ID (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender; // 메시지 발신자 (FK)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    // private String imageUrl; // 이미지 URL (선택 사항)

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Builder
    public ChatMessage(Long id, ChatRoom chatRoom, Member sender, String text, Boolean isDeleted, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.text = text;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
