package com.example.SevMerge.chatRoom;

import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name = "chat_room_tb")
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // 프로젝트 ID (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Member client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private Member expert;

    @Column(nullable = false)
    private boolean deletedByClient = false;  // 의뢰인이 방 삭제

    @Column(nullable = false)
    private boolean deletedByExpert = false;  // 전문가가 방 삭제

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public ChatRoom(Long id, Project project, Member client, Member expert, Timestamp createdAt) {
        this.id = id;
        this.project = project;
        this.client = client;
        this.expert = expert;
        this.createdAt = createdAt;
    }

    public boolean validate(Member sessionMember) {
        if (this.getClient().getId().equals(sessionMember.getId()) || this.getExpert().getId().equals(sessionMember.getId())) {
            return true;
        } else {
            return false;
        }
    }

    // 나에게서 방 삭제 (보는 사람 쪽 플래그)
    public void deleteFor(Member viewer) {
        if (this.client.getId().equals(viewer.getId())) this.deletedByClient = true;
        else this.deletedByExpert = true;
    }

    public boolean isFullyDeleted() { return this.deletedByClient && this.deletedByExpert; }

    // 새 메시지 도착 시 양쪽 삭제 플래그 해제 (방 다시 보이게)
    public void restore() {
        this.deletedByClient = false;
        this.deletedByExpert = false;
    }

}
