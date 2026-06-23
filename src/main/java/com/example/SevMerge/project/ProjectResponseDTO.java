package com.example.SevMerge.project;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


public class ProjectResponseDTO {

    // 목록 조회 응답 DTO
    @Data
    @NoArgsConstructor
    public static class ListDTO {
        private Long id;
        private Long memberId;
        private Long selectedExpertId;
        private String memberName;
        private String title;
        private String categoryName;
        private String description;
        private Integer budgetMin;
        private Integer budgetMax;
        private Timestamp deadline;
        private boolean hasReview;
        private String projectStatus; // // OPEN, CLOSED
        private boolean isDone;
        private boolean isClosed; // 머스태치 파일용 불리언타(낙찰완료 확인용)
        private boolean isCompleted;

        private boolean privateProject;
        private Integer viewCount;
        private Integer bidCount;
        private Integer dDay;
        private boolean isCertifiedOnly;
        private boolean isUrgent;
        private Timestamp createdAt;
        private boolean isOpen;
        private boolean reviewSkipped;
        private boolean isProjectClosed;
        private boolean isCancelled;
        private boolean isDraft;

        public ListDTO(Project project) {
            this.id = project.getId();
            this.memberId = project.getMember().getId();
            this.memberName = project.getMember().getName();
            this.title = project.getTitle();
            this.categoryName = project.getCategory().name();
            this.description = project.getDescription();
            this.budgetMin = project.getBudgetMin();
            this.budgetMax = project.getBudgetMax();
            this.deadline = project.getDeadline();
            this.projectStatus = project.getProjectStatus().name();
            this.isDone = project.getProjectStatus() == ProjectStatus.DONE;
            this.privateProject = project.isPrivate();
            this.isClosed = project.getProjectStatus() == ProjectStatus.IN_PROGRESS; // 프로젝트 상태가 CLOSED(낙찰 완료)인지 확인하는 용도
            this.viewCount = project.getViewCount();
            this.bidCount = 0;
            this.isCertifiedOnly = project.getBidFilter() == BidFilter.CERTIFIED_ONLY;
            this.createdAt = project.getCreatedAt();
            long diff = project.getDeadline().getTime() - System.currentTimeMillis();
            this.dDay = (int) (diff / (1000 * 60 * 60 * 24));
            this.isUrgent = this.dDay <= 3;
            this.isOpen = project.getProjectStatus() == ProjectStatus.OPEN;
            this.isCompleted = project.getProjectStatus() == ProjectStatus.COMPLETED;
            this.isProjectClosed = "CLOSED".equals(this.projectStatus);
            this.isCancelled = "CANCELLED".equals(this.projectStatus);
            this.isDraft = "DRAFT".equals(this.projectStatus);
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
        private String deadlineDate;
        private boolean privateProject;
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
            this.privateProject = project.isPrivate();
            this.deadlineDate = project.getDeadline().toString().substring(0, 10);
            this.bidFilter = project.getBidFilter().name();
            this.projectStatus = project.getProjectStatus().name();
            this.viewCount = project.getViewCount();
            this.bidCount = 0;
            this.isCertifiedOnly = project.getBidFilter() == BidFilter.CERTIFIED_ONLY;
            this.createdAt = project.getCreatedAt();
            long diff = project.getDeadline().getTime() - System.currentTimeMillis();
            this.dDay = (int) (diff / (1000 * 60 * 60 * 24));
        }
    }
}
