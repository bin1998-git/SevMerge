package com.example.SevMerge.review;

import lombok.Data;

import java.sql.Timestamp;


public class ReviewResponse {

    @Data
    public static class ReviewListDTO {
        private Long id;
        private String targetName;
        private String reviewerName;
        private String content;
        private String star;
        private Integer countStar;
        private Timestamp createdAt;

        public ReviewListDTO(Review review) {
            this.id = review.getId();
            this.targetName = review.getTargeter().getName();
            this.reviewerName = review.getReviewer().getName();
            this.content = review.getContent();
            this.createdAt = review.getCreatedAt();
            this.countStar = review.getCountStar();
            this.star = ("★".repeat(review.getCountStar()) + "☆".repeat(5-review.getCountStar()));
        }
    }

    @Data
    public static class SaveDTO {
        private Long reviewerId;
        private Long targeterId;
        private String reviewerName;
        private String targeterName;

        public SaveDTO(Review review) {
            this.reviewerId = review.getReviewer().getId();
            this.targeterId = review.getTargeter().getId();
            this.reviewerName = review.getReviewer().getName();
            this.targeterName = review.getTargeter().getName();
        }
    }



    // 리뷰 상세화면
    @Data
    public static class ReviewDetailDTO {
        private Long id;
        private String reviewerName;
        private String targeterName;
        private Integer countStar;
        private String content;
        private Timestamp createdAt;

        public ReviewDetailDTO(Review review) {
            this.id = review.getId();
            this.reviewerName = review.getReviewer().getName();
            this.targeterName = review.getTargeter().getName();
            this.countStar = review.getCountStar();
            this.content = review.getContent();
            this.createdAt = review.getCreatedAt();
        }
    }


    @Data
    public static class UpdateDTO {

        private Long id; // review id
        private Integer rating;
        private String content;

        public UpdateDTO(Review review) {
            this.id = review.getId();
            this.rating = review.getCountStar();
            this.content = review.getContent();
        }
    }



}