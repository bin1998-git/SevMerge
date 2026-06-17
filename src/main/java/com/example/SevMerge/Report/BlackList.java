package com.example.SevMerge.Report;

import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "blacklist_tb")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 500)
    private String reason; // 차단 사유 >> 신고 3회 누적되서 자동 차단

    @Column(length = 255)
    private String reportIds; // 누적된 신고 ID 기록


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime bannedAt; // 차단 일시

    private LocalDateTime expiredAt; // 차단 만료일

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true; // 현재 차단유지 여부 해제되면 false임

    public void release(String releaseReason) {
        this.isActive = false;
        this.reason += "[관리자 해제 사유 : " + releaseReason + "]";

    }
}
