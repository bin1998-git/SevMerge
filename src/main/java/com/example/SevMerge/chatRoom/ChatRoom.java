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
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // 프로젝트 ID (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Member client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private Member expert;

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

}
