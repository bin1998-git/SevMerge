package com.example.SevMerge.review;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import lombok.Builder;
import lombok.Data;

public class ReviewRequest {


    @Data
    public static class SaveReviewDTO {
        private Long reviewerId;
        private Long targetId;
        private Long projectId;
        private Integer rating;
        private String content;

        public Review toEntity(Member reviewer, Member targeter, Project project) {
            return Review.builder()
                    .reviewer(reviewer)
                    .targeter(targeter)
                    .content(content)
                    .countStar(rating)
                    .project(project)  // project 세팅 추가
                    .build();
        }

        public void validate(ReviewRequest.SaveReviewDTO review) {

            if (review.getContent() == null || review.getContent().trim().isEmpty()){
                throw new BadRequestException("리뷰를 작성해 주세요");
            }

        }

    }

    @Data
    public static class UpdateRequestDTO {
        private Integer rating; // 별점
        private String content; // 내용

        public void validate() {
            if(content == null || content.trim().isEmpty()) {
                throw new BadRequestException("본문 내용 입력은 필수입니다.");
            }
        }

    }

    // 전문가 가 유저에게 리뷰달기
    // member expertProfile 두개 키값
    @Data
    public static class ExpertReviewToClientSaveDTO {

        private Long expertId; // 전문가  Id
        private Long memberId; // 유저 Id
        private String content;
        private Integer rating;
    }


}
