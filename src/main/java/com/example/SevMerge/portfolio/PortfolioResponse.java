package com.example.SevMerge.portfolio;

import lombok.Builder;
import lombok.Data;

public class PortfolioResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private String title;
        private String description;
        private String imgUrl;
        private String expertName;
        private String createdAt;

        @Builder
        public ListDTO(Portfolio portfolio) {
            this.id = portfolio.getId();
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.imgUrl = portfolio.getImageUrl();
            this.expertName = portfolio.getExpertProfile().getMember().getName();
            this.createdAt = portfolio.getCreatedAt().toString();
        }
    }

    @Data
    public static class DetailDTO {
        private String title;
        private String description;
        private String expertName;
        private String projectUrl;

        @Builder
        public DetailDTO(Portfolio portfolio) {
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.expertName = portfolio.getExpertProfile().getMember().getName();
            this.projectUrl = portfolio.getProjectUrl();
        }
    }


    // 일단 form 태그에서 포프폴리오 id 넘겨줌
    @Data
    public static class UpdateDTO {

        private Long id;
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
