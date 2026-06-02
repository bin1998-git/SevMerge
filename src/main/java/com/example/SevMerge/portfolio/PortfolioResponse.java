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

}
