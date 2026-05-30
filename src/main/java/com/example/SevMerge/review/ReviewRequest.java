package com.example.SevMerge.review;

import com.example.SevMerge.core.exception.BadRequestException;
import lombok.Builder;
import lombok.Data;

public class ReviewRequest {


    @Data
    public static class SaveReviewDTO {


        private Integer Rating;
        private String content;
        private Long projectId;
        private Long expertId;


        @Builder
        public SaveReviewDTO(Integer rating, String content,Long projectId, Long expertProfileId) {
            Rating = rating;
            this.content = content;
            this.projectId = projectId;
            this.expertId = expertProfileId;
        }

        public void validate(ReviewRequest.SaveReviewDTO review) {

            if (review.getContent() == null || review.getContent().trim().isEmpty()){
                throw new BadRequestException("리뷰를 작성해 주세요");
            }

        }

    }



}
