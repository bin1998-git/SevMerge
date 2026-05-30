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
        private String category;
        private Integer budgetMin;
        private Integer budgetMax;
        private Timestamp deadline;
        private String bidFilter;
        private String projectStatus;
        private Timestamp createdAt;

        public ListDTO(Project project) {
            this.id = project.getId();
            this.memberName = project.getMember().getName();
            this.title = project.getTitle();
            this.category = project.getCategory().name();
            this.budgetMin = project.getBudgetMin();
            this.budgetMax = project.getBudgetMax();
            this.deadline = project.getDeadline();
            this.bidFilter = project.getBidFilter().name();
            this.projectStatus = project.getProjectStatus().name();
            this.createdAt = project.getCreatedAt();
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
        private String category;
        private String description;
        private Integer budgetMin;
        private Integer budgetMax;
        private Timestamp deadline;
        private String bidFilter;
        private String projectStatus;
        private Timestamp createdAt;

        public DetailDTO(Project project) {
            this.id = project.getId();
            this.memberId = project.getMember().getId();
            this.memberName = project.getMember().getName();
            this.title = project.getTitle();
            this.category = project.getCategory().name();
            this.description = project.getDescription();
            this.budgetMin = project.getBudgetMin();
            this.budgetMax = project.getBudgetMax();
            this.deadline = project.getDeadline();
            this.bidFilter = project.getBidFilter().name();
            this.projectStatus = project.getProjectStatus().name();
            this.createdAt = project.getCreatedAt();
        }
    }
}
