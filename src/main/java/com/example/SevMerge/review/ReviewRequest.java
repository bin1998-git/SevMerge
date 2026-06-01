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

    // 전문가 가 유저에게 리뷰달기
    // member expertProfile 두개 키값
    @Data

    public static class ExpertSaveToClient {

        private Long expertId; // 전문가  Id
        private Long memberId; // 유저 Id
        private String title;
        private String content;
        private Integer rating;

        @Builder
        public ExpertSaveToClient(Integer rating,Long expertId , Long memberId, String title , String content) {
            this.expertId = expertId;
            this.memberId = memberId;
            this.title = title;
            this.content = content;
            this.rating = rating;

        }


    }


}
