package com.example.SevMerge.review;

import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import com.sun.jdi.event.StepEvent;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


public class ReviewResponse {

    @Data
    @Builder
    // 리뷰화면
    public static class ReviewSaveDTO {

        private SaveExpertDTO expertProfile;

        // expert
        @Data
        @Builder
        public static class SaveExpertDTO {

            private Long id;
            private SaveMemberDTO member;
            private String career;
            // member
            @Data
            @Builder
            public static class SaveMemberDTO {
                private String name;
            }

        }
        public ReviewSaveDTO (ExpertProfile expertProfile  ){

            this.expertProfile = SaveExpertDTO
                    .builder()
                    .career(expertProfile.getCareer())
                    .id(expertProfile.getId())
                    .build();

            this.expertProfile.member = SaveExpertDTO
                    .SaveMemberDTO
                    .builder()
                    .name(expertProfile.getMember().getName())
                    .build();
        }
    }

    // 리뷰 상세화면
    @Data
    public static class ReviewDetailDTO {

        private String content;
        private Timestamp createdAt;
        private Long id;

        private ExpertDTO expertProfile;
        private DetailMemberDTO member;

        @Data
        @Builder
        public static class ExpertDTO {   // 머스테치 파일의 {{review.expertProfile}}
            private Long id;
            private String name;
            private String career;
        }

        @Data
        @Builder
        public static class DetailMemberDTO {  // 머스테치 파일의 {{review.member}}

            private String name;

        }

        @Builder
        public ReviewDetailDTO(Review review) {

            this.content = review.getContent();
            this.createdAt = review.getCreatedAt();
            this.id = review.getId();

            this.member = DetailMemberDTO
                    .builder()
                    .name(review.getMember().getName())
                    .build();

            this.expertProfile = ExpertDTO
                    .builder()
                    .id(review.getExpertProfile().getId())
                    .name(review.getExpertProfile().getMember().getName())
                    .career(review.getExpertProfile().getCareer())
                    .build();


        }
    }


    // 리뷰 목록 화면

    @Data
    public static class ReviewListDTO {

        private Timestamp createdAt;
        private String content;
        private Long id; // reviewId
        private ReviewListMemberDTO member;


        @Data
        @Builder
        public static class ReviewListMemberDTO {

            private String name; // 리뷰의 일반회원 이름
        }

        public ReviewListDTO (Review review){

            this.createdAt = review.getCreatedAt();
            this.content = review.getContent();
            this.id = review.getId();

            this.member = ReviewListMemberDTO
                    .builder()
                    .name(review.getMember().getName())// 일반회원 이름
                    .build();

        }
    }

    // 리뷰 리스트에 뿌려줄 전문가 데이터
    @Data
    public static class ExpertListDTO {

        private ExpertListMemberDTO member;
        private BigDecimal avgRating;
        private String career;
//        private BigDecimal experienceYears; // TODO 전문가 경력 추후 전문가에추가
//        private Long reviewCount; //TODO 전문가가 가진 리뷰갯수 추후 전문가에 추가
        @Data
        @Builder
        public static class ExpertListMemberDTO {

            private String name;
            private boolean isCertified;


        }

        public ExpertListDTO (ExpertProfile expertProfile) {
            this.avgRating = expertProfile.getAvgRating();
            this.career = expertProfile.getCareer();

            this.member = ExpertListMemberDTO
                    .builder()
                    .name(expertProfile.getMember().getName())
                    .isCertified(expertProfile.isCertified())
                    .build();

        }

    }

    @Data
    public static class ReviewListPageDTO {

        private List<ReviewListDTO> reviews;
        private ExpertListDTO expertProfile;

        public ReviewListPageDTO(List<ReviewListDTO> reviews , ExpertListDTO expertProfile){
            this.reviews = reviews;
            this.expertProfile = expertProfile;

        }

    }


}
