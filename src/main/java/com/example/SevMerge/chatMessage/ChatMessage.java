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

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isRead = false;            // 수신자 읽음 여부

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean deletedBySender = false;   // 보낸 사람이 삭제

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean deletedByReceiver = false; // 받은 사람이 삭제

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

    public void markRead() { this.isRead = true; }

    public boolean isSentBy(Member m) { return this.sender.getId().equals(m.getId()); }

    // 나에게서 삭제 (보는 사람 쪽 플래그만)
    public void deleteFor(Member viewer) {
        if (isSentBy(viewer)) this.deletedBySender = true;
        else this.deletedByReceiver = true;
    }

    // 모두에게서 삭제 (보낸 사람만 호출)
    public void deleteForAll() {
        this.deletedBySender = true;
        this.deletedByReceiver = true;
    }

    public boolean isDeletedFor(Member viewer) {
        return isSentBy(viewer) ? this.deletedBySender : this.deletedByReceiver;
    }

    public boolean isFullyDeleted() { return this.deletedBySender && this.deletedByReceiver; }
}
