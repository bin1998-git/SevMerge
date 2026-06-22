package com.example.SevMerge.project;


import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

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
    private Timestamp deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidFilter bidFilter; // 어떤 전문가 제안서를 받을지 설정

    @Column(nullable = false)
    @Builder.Default
    private Integer viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus projectStatus;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;


    @Column(nullable = false)
    @Builder.Default
    private boolean isPrivate = false; // 비공개입찰여부

    @CreationTimestamp
    private Timestamp createdAt;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean reviewSkipped = false;



    // DB에 저장되기 직전에 자동으로 실행되는 메서드
    @PrePersist
    public void prePersist() {
        if (this.bidFilter == null) this.bidFilter = BidFilter.ALL;
        if (this.projectStatus == null) this.projectStatus = ProjectStatus.OPEN;
    }

    // update 메서드기능
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public void update(ProjectRequestDTO.UpdateDTO requestDTO) {
        if (requestDTO.getTitle() != null) this.title = requestDTO.getTitle();
        if (requestDTO.getDescription() != null) this.description = requestDTO.getDescription();
        if (requestDTO.getBudgetMin() != null) this.budgetMin = requestDTO.getBudgetMin();
        if (requestDTO.getBudgetMax() != null) this.budgetMax = requestDTO.getBudgetMax();
        if (requestDTO.getDeadline() != null)
            this.deadline = Timestamp.valueOf(requestDTO.getDeadline() + " 00:00:00");
        if (requestDTO.getBidFilter() != null) this.bidFilter = BidFilter.valueOf(requestDTO.getBidFilter());
        if (requestDTO.getIsPrivate() != null) {
            this.isPrivate = requestDTO.getIsPrivate();
        }
        
    }

        // 임시저장 업데이트 메서드
    public void updateDraft(ProjectRequestDTO.UpdateDTO dto) {
        if (dto.getTitle() != null) this.title = dto.getTitle();
        if (dto.getDescription() != null) this.description = dto.getDescription();
        if (dto.getBudgetMin() != null) this.budgetMin = dto.getBudgetMin();
        if (dto.getBudgetMax() != null) this.budgetMax = dto.getBudgetMax();
        if (dto.getDeadline() != null) this.deadline = Timestamp.valueOf(dto.getDeadline() + " 00:00:00");
        if (dto.getBidFilter() != null) this.bidFilter = BidFilter.valueOf(dto.getBidFilter());
        if (dto.getIsPrivate() != null) this.isPrivate = dto.getIsPrivate();
        // 상태를 임시저장으로 변경
        this.projectStatus = ProjectStatus.DRAFT;
    }

    // viewCount 증가 메서드
    public void increaseViewCount() {
        this.viewCount++;
    }

    public void updateStatus(ProjectStatus status) {
        this.projectStatus = status;
    }

    // 소프트 딜리트 사용 메서드
    public void delete() {
        this.isDeleted = true;
    }

}

