package com.example.SevMerge.expertprofile;

import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "expert_review_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpertReviewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 심사 대상 전문가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 심사 결과 (승인 / 거절)
    @Column(nullable = false, length = 20)
    private String result;

    // 거절 사유 (승인이면 null)
    @Column(columnDefinition = "TEXT")
    private String reason;

    // 처리 시간
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp reviewedAt;
}