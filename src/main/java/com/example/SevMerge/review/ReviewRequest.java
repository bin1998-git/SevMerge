package com.example.SevMerge.review;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import lombok.Builder;
import lombok.Data;

public class ReviewRequest {


    @Data
    public static class SaveReviewDTO {

        private Member member;
        private Integer countStar;
        private Double totalStar;
        private String content;
        private ExpertProfile expert;

        @Builder
        public SaveReviewDTO(Member member,ExpertProfile expert,Integer countStar, String content) {
            this.member = member;
            this.countStar = countStar;
            this.content = content;
            this.expert = expert;
        }

        public void validate(ReviewRequest.SaveReviewDTO review) {

            if (review == null){
                throw new BadRequestException("리뷰를 작성 하지 않았습니다");
            }

        }

    }



}
