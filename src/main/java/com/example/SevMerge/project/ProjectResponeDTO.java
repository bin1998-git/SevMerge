package com.example.SevMerge.project;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


public class ProjectResponeDTO {

    // 목록 조회 응답 DTO
    @Data
    @NoArgsConstructor
    public static class ListDTO {
        private Long id;
        private String memberName;
        private String title;
        private String categoryName;
        private String description;
        private Integer budgetMin;
        private Integer budgetMax;
        private Timestamp deadline;
        private String projectStatus;
        private Integer viewCount;
        private Integer bidCount;
        private Integer dDay;
        private boolean isCertifiedOnly;
        private boolean isUrgent;
        private Timestamp createdAt;
        private boolean isOpen;

        public ListDTO(Project project) {
            this.id = project.getId();
            this.memberName = project.getMember().getName();
            this.title = project.getTitle();
            this.categoryName = project.getCategory().name();
            this.description = project.getDescription();
            this.budgetMin = project.getBudgetMin();
            this.budgetMax = project.getBudgetMax();
            this.deadline = project.getDeadline();
            this.projectStatus = project.getProjectStatus().name();
            this.viewCount = project.getViewCount();
            this.bidCount = 0;
            this.isCertifiedOnly = project.getBidFilter() == BidFilter.CERTIFIED_ONLY;
            this.createdAt = project.getCreatedAt();
            long diff = project.getDeadline().getTime() - System.currentTimeMillis();
            this.dDay = (int) (diff / (1000 * 60 * 60 * 24));
            this.isUrgent = this.dDay <= 3;
            this.isOpen = project.getProjectStatus() == ProjectStatus.OPEN;
        }

    }

    // 상세 조회 응답 DTO
    @Data
    @NoArgsConstructor
    public static class DetailDTO {
        private Long id;
        private Long memberId;
        private String memberName;
        private String title;
        private String categoryName;
        private String description;
        private Integer budgetMin;
        private Integer budgetMax;
        private Timestamp deadline;
        private String bidFilter;
        private String projectStatus;
        private Integer viewCount;
        private Integer bidCount;
        private Integer dDay;
        private boolean isCertifiedOnly;
        private Timestamp createdAt;

        public DetailDTO(Project project) {
            this.id = project.getId();
            this.memberId = project.getMember().getId();
            this.memberName = project.getMember().getName();
            this.title = project.getTitle();
            this.categoryName = project.getCategory().name();
            this.description = project.getDescription();
            this.budgetMin = project.getBudgetMin();
            this.budgetMax = project.getBudgetMax();
            this.deadline = project.getDeadline();
            this.bidFilter = project.getBidFilter().name();
            this.projectStatus = project.getProjectStatus().name();
            this.viewCount = project.getViewCount();
            this.bidCount = 0;
            this.isCertifiedOnly = project.getBidFilter() == BidFilter.CERTIFIED_ONLY;
            this.createdAt = project.getCreatedAt();
            // dDay 계산
            long diff = project.getDeadline().getTime() - System.currentTimeMillis();
            this.dDay = (int) (diff / (1000 * 60 * 60 * 24));
        }
    }
}
