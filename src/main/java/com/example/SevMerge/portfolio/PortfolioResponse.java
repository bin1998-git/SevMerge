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
            this.id = id;
            this.title = title;
            this.description = description;
            this.imgUrl = imgUrl;
            this.expertName = expertName;
            this.createdAt = createdAt;
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
            this.title = title;
            this.description = description;
            this.expertName = expertName;
            this.projectUrl = projectUrl;
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
