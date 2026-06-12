package com.example.SevMerge.portfolio;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
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
        @Builder
        public ListDTO(Portfolio portfolio) {
            this.id = portfolio.getId();
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.imageUrl = (portfolio.getImageUrl() == null || portfolio.getImageUrl().isEmpty())
                    ? null
                    : portfolio.getImageUrl();
            this.expertName = portfolio.getExpertProfile().getMember().getName();
            this.createdAt = portfolio.getCreatedAt().toString();

        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String title;
        private String description;
        private String expertName;
        private Long expertId;
        private String projectUrl;
        private String imageUrl;
        private Timestamp createdAt;


        @Builder
        public DetailDTO(Portfolio portfolio) {
            this.id = portfolio.getId();
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.expertName = portfolio.getExpertProfile().getMember().getName();
            this.expertId = portfolio.getExpertProfile().getMember().getId();
            this.projectUrl = portfolio.getProjectUrl();
            this.imageUrl = (portfolio.getImageUrl() == null || portfolio.getImageUrl().isEmpty())
                    ? null
                    : portfolio.getImageUrl();
            this.createdAt = portfolio.getCreatedAt();
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
        private Long expertId; // 전문가의멤버의 아이디

        public UpdateDTO(Portfolio portfolio) {
            this.id = portfolio.getId();
            this.title = portfolio.getTitle();
            this.description = portfolio.getDescription();
            this.imageUrl = portfolio.getImageUrl();
            this.projectUrl = portfolio.getProjectUrl();
            this.expertId = portfolio.getExpertProfile().getMember().getId();
        }
    }
}
