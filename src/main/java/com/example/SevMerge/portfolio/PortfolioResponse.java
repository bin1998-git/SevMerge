package com.example.SevMerge.portfolio;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

public class PortfolioResponse {
    @Data
    public static class ListDTO {
        private Long id;
        private String title;
        private String description;
        private String imageUrl;
        private String expertName;
        private String createdAt;

        private Long portfolioCount;

        @Builder
        public ListDTO(Portfolio portfolio , Long portfolioCount) {
            this.id = portfolio.getId();
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.imageUrl = portfolio.getImageUrl();
            this.expertName = portfolio.getExpertProfile().getMember().getName();
            this.createdAt = portfolio.getCreatedAt().toString();

            this.portfolioCount = portfolioCount;


        }
    }

    @Data
    public static class DetailDTO {
        private String title;
        private String description;
        private String expertName;
        private String projectUrl;
        private Timestamp createdAt;
        private Long expertId;
        private Long id; // 포트폴리오 아이디

        @Builder
        public DetailDTO(Portfolio portfolio) {
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.expertName = portfolio.getExpertProfile().getMember().getName();
            this.projectUrl = portfolio.getProjectUrl();
            this.createdAt = portfolio.getCreatedAt();
            this.expertId = portfolio.getExpertProfile().getMember().getId();
            this.id = portfolio.getId();
        }
    }


    // 일단 form 태그에서 포프폴리오 id 넘겨줌
    @Data
    public static class UpdateDTO {

        private Long id; // 포트폴리오 아이디
        private String title;
        private String description;
        private String imageUrl;
        private String projectUrl;

        public UpdateDTO(Portfolio portfolio) {
            this.id = portfolio.getId();
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.imageUrl = portfolio.getImageUrl();
            this.projectUrl = portfolio.getProjectUrl();
        }
    }
}
