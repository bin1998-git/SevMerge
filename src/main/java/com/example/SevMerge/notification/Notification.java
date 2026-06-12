package com.example.SevMerge.notification;

import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "notification_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;         // 수신자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;   // 알림 종류

    @Column(nullable = false)
    private String content;          // 완성된 알림 문구

    @Column(nullable = false)
    private String url;              // 클릭 시 이동 경로

    @Column(nullable = false)
    private boolean isRead;          // 읽음 여부

    @CreationTimestamp
    private Timestamp createdAt;

    public void read() {
        this.isRead = true;
    }
}
