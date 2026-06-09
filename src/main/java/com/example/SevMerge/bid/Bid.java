package com.example.SevMerge.bid;

import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "bid_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private Member expert; // 입찰한 전문가

    @Column(nullable = false, columnDefinition = "TEXT")
    private String coverLetter; // 자기소개


    @Column(columnDefinition = "TEXT")
    private String approach; // 작업 접근 방식

    @Column(nullable = false)
    private Long estimatedDays; // 예상 작업 기간

    @Column(nullable = false)
    private Long proposedPrice; // 희망금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp createdAt;

    // DB에 저장되기 직전에 자동으로 실행되는 메서드
    @PrePersist
    public void prePersist() {
        if (this.status == null) this.status = BidStatus.PENDING;
    }

    public void update(BidRequestDTO.UpdateDTO req) {
        if (req.getCoverLetter() != null) this.coverLetter = req.getCoverLetter();
        if (req.getApproach() != null) this.approach = req.getApproach();
        if (req.getEstimatedDays() != null) this.estimatedDays = req.getEstimatedDays();
        if (req.getProposedPrice() != null) this.proposedPrice = req.getProposedPrice();
    }

    public void select() {
        this.status = BidStatus.SELECTED;
    }

    public void fail() {
        this.status = BidStatus.REJECTED;
    }
    // 소프트 딜리트 삭제 메서드

    public void delete() {
        this.isDeleted = true;
    }

    // 제안서 거절
    public void reject() {
        this.status = BidStatus.REJECTED;
    }
}
