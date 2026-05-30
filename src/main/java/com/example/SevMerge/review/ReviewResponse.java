package com.example.SevMerge.review;

import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;


public class ReviewResponse {

    @Data
    @Builder
    public static class ReviewSaveDTO {

        private String expertName;
        private String projectTitle;
        private String career;
        private Long expertId;

        public ReviewSaveDTO (ExpertProfile expertProfile , Project project){

            this.expertName = expertProfile.getMember().getName();
            this.projectTitle = project.getTitle();
            this.career = expertProfile.getCareer();
            this.expertId = expertProfile.getId();

        }


    }

    @Data
    public static class ReviewDetailDTO {

        private String content;
        private Timestamp createdAt;
        private Long id;

        private ExpertDTO expertProfile;
        private MemberDTO member;
        private ProjectDTO project;

        @Data
        @Builder
        public static class ExpertDTO {   // 머스테치 파일의 {{review.expertProfile}}
            private Long id;
            private String name;
            private String career;
        }

        @Data
        @Builder
        public static class MemberDTO {  // 머스테치 파일의 {{review.member}}

            private String name;


        }

        @Data
        @Builder
        public static class ProjectDTO { // 머스테치 파일의 {{review.project}}

            private String title;

        }



        @Builder
        public ReviewDetailDTO(Review review) {

            this.content = review.getContent();
            this.createdAt = review.getCreatedAt();
            this.id = review.getId();

            this.member = MemberDTO
                    .builder()
                    .name(review.getMember().getName())
                    .build();

            this.expertProfile = ExpertDTO
                    .builder()
                    .id(review.getExpertProfile().getId())
                    .name(review.getExpertProfile().getMember().getName())
                    .career(review.getExpertProfile().getCareer())
                    .build();

            this.project = ProjectDTO
                    .builder()
                    .title(review.getProject().getTitle())
                    .build();
        }
    }


}
