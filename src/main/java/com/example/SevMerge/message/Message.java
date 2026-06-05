package com.example.SevMerge.message;

import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name="message_tb")
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    // null 허용, 관리자 쪽지 발송 시 null 허용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String content;

    @ColumnDefault("false")
    private Boolean isRead = false;

    @ColumnDefault("false")
    private Boolean deletedBySender = false;

    @ColumnDefault("false")
    private Boolean deletedByReceiver = false;

    @CreationTimestamp
    private Timestamp createdAt;
}

