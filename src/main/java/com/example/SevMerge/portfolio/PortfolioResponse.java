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
        private String projectUrl;
        private String createdAt;

        @Builder
        public ListDTO(Long id, String title, String description, String imgUrl, String projectUrl, String createdAt) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.imgUrl = imgUrl;
            this.projectUrl = projectUrl;
            this.createdAt = createdAt;
        }
    }

}
