package com.example.SevMerge.review;

import com.example.SevMerge.core.exception.BadRequestException;
import lombok.Builder;
import lombok.Data;

public class ReviewRequest {


    @Data
    public static class SaveReviewDTO {


        private Integer rating;
        private String content;

        private Long expertId;


        @Builder
        public SaveReviewDTO(Integer rating, String content , Long expertProfileId) {
            this.rating = rating;
            this.content = content;

            this.expertId = expertProfileId;
        }

        public void validate(ReviewRequest.SaveReviewDTO review) {

            if (review.getContent() == null || review.getContent().trim().isEmpty()){
                throw new BadRequestException("리뷰를 작성해 주세요");
            }

        }

    }

    @Data
    public static class UpdateRequestDTO {
        private Integer rating;
        private String content;

    }


}
