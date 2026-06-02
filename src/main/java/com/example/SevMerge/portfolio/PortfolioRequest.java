package com.example.SevMerge.portfolio;

import lombok.Data;
import lombok.NoArgsConstructor;

public class PortfolioRequest {

    @Data
    @NoArgsConstructor
    public static class SaveDTO {

        private Long expertId;
        private String title;
        private String description;
        private String imageUrl;
        private String projectUrl;

        public void validate () {

            if(title == null || title.trim().isEmpty()) {


            }

        }

    }

    @Data
    @NoArgsConstructor
    public static class UpdateDTO {

        private Long id;
        private String title;
        private String description;
        private String imageUrl;
        private String projectUrl;


    }

}
