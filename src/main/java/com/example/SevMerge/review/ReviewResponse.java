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


            @Data
            @Builder
            public static class SaveMemberDTO {
                private String name;
            }

        }

        public ReviewSaveDTO(ExpertProfile expertProfile) {

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

    // 전문가가 의뢰인에게 작성하는 화면
    @Data
public static class SaveExpertToClient {

        private MemberDTO member;
        private ExpertProfileDTO expertProfile;

        @Data
        @Builder
        public static class ExpertProfileDTO {

            private Long id;

        }

        @Data
        @Builder
        public static class MemberDTO {
            private Long id; // member id
            private String name;
            private String email;

        }

        public SaveExpertToClient(Member member, ExpertProfile expertProfile) {

            this.member = MemberDTO
                    .builder()
                    .id(member.getId())
                    .name(member.getName())
                    .email(member.getEmail())
                    .build();

            this.expertProfile = ExpertProfileDTO
                    .builder()
                    .id(expertProfile.getId())
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
            private DetailExpertMemberDTO member;

            @Data
            @Builder
            public static class DetailExpertMemberDTO {

                private String name;
            }

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
                    .member(ExpertDTO.DetailExpertMemberDTO
                            .builder()
                            .name(review.getExpertProfile().getMember().getName())
                            .build())
                    .career(review.getExpertProfile().getCareer())
                    .build();


        }
    }


    // 리뷰 목록 화면

    @Data
    public static class ReviewListDTO {

        private Long id; // reviewId
        private String content;
        private Integer countStart;
        private Timestamp createdAt;
        private Member member;

        public ReviewListDTO(Review review) {
            this.id = review.getId();
            this.content = review.getContent();
            this.countStart = review.getCountStar();
            this.createdAt = review.getCreatedAt();
            this.member = review.getMember();
        }
    }

    // 리뷰 리스트에 뿌려줄 전문가 데이터
    @Data
    public static class ExpertListDTO {

        private ExpertListMemberDTO member;
        private BigDecimal avgRating;
        private String career;
        private Integer reviewCount;
        private Long id; // 전문가 아이디

//                private Timestamp experienceYears; // TODO 전문가 경력 추후 전문가에추가

        @Data
        @Builder
        public static class ExpertListMemberDTO {

            private String name;
            private boolean isCertified;


        }

        public ExpertListDTO(ExpertProfile expertProfile) {
            this.avgRating = expertProfile.getAvgRating();
            this.career = expertProfile.getCareer();
            this.reviewCount = expertProfile.getTotalReviews();
            this.id = expertProfile.getId();

            this.member = ExpertListMemberDTO
                    .builder()
                    .name(expertProfile.getMember().getName())
                    .isCertified(expertProfile.isCertified())
                    .build();

        }

    }

    @Data
    public static class ReviewListPageDTO {
        private PagingDTO paging;
        private List<ReviewListDTO> reviews;
        private ExpertListDTO expertProfile;

        public ReviewListPageDTO(List<ReviewListDTO> reviews, ExpertListDTO expertProfile, PagingDTO pagingDTO) {
            this.reviews = reviews;
            this.expertProfile = expertProfile;
            this.paging = pagingDTO;

        }

    }


    @Data
    public static class PagingDTO {

        private boolean hasPrev;
        private boolean hasNext;
        private int prevPage;
        private int nextPage;
        private List<PageDTO> pageList;

        @Data
        public static class PageDTO {
            private int num;
            private boolean isCurrent;
        }

    }


    // 리뷰 수정화면


    @Data
    public static class UpdateDTO {

        private Long id; // review id
        private Integer rating;
        private String content;
        private boolean isRating1;
        private boolean isRating2;
        private boolean isRating3;
        private boolean isRating4;
        private boolean isRating5;

        private UpdateExpertProfileDTO expertProfile;

        @Data
        @Builder
        public static class UpdateExpertProfileDTO {

            private UpdateMemberDTO member;
            private String career;

            @Data
            @Builder
            public static class UpdateMemberDTO {

                private String name; // 전문가 이름

            }

        }

        public UpdateDTO(Review review) {


            this.isRating1 = review.getCountStar() >= 1;
            this.isRating2 = review.getCountStar() >= 2;
            this.isRating3 = review.getCountStar() >= 3;
            this.isRating4 = review.getCountStar() >= 4;
            this.isRating5 = review.getCountStar() >= 5;
            this.id = review.getId();
            this.rating = review.getCountStar();
            this.content = review.getContent();
            this.expertProfile = UpdateExpertProfileDTO
                    .builder()
                    .member(UpdateExpertProfileDTO.UpdateMemberDTO
                            .builder()
                            .name(review.getExpertProfile().getMember().getName())
                            .build())
                    .career(review.getExpertProfile().getCareer())
                    .build();

        }
    }

}
