package com.example.SevMerge.bid;

import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "product_id", nullable = false)
    private Project projectId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private Member expert;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String coverLetter;


    @Column(columnDefinition = "TEXT")
    private String approach;

    @Column(nullable = false)
    private Long estimatedDays;

    @Column(nullable = false)
    private Long proposedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;

    @Column(nullable = false)
    private Timestamp createdAt;

    // DB에 저장되기 직전에 자동으로 실행되는 메서드
    @PrePersist
    public void prePersist() {
        if (this.status == null) this.status = BidStatus.PENDING;
    }
}
