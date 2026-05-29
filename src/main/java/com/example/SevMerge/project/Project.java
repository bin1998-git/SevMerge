package com.example.SevMerge.project;


import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
// member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, created_at
@Entity
@Table(name = "project_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer budgetMin; // 최소예산

    @Column(nullable = false)
    private Integer budgetMax; // 최대예산

    @Column(nullable = false)
    private Timestamp deadline; // 마감일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidFilter bidFilter; // 어떤 전문가 제안서를 받을지 설정

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus projectStatus;

    private Timestamp createdAt;

    // DB에 저장되기 직전에 자동으로 실행되는 메서드
    @PrePersist
    public void prePersist() {
        if (this.bidFilter == null) this.bidFilter = BidFilter.ALL;
        if (this.projectStatus == null) this.projectStatus = ProjectStatus.OPEN;
    }


}
