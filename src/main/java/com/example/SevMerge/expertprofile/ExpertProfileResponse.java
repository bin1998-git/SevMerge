package com.example.SevMerge.expertprofile;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExpertProfileResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private String profileImage;
    private String intro;
    private String career;
    private String speciality;
    private BigDecimal avgRating;
    private int totalReviews;
    private boolean isCertified;

    public static ExpertProfileResponse from(ExpertProfile profile) {
        return ExpertProfileResponse.builder()
                .id(profile.getId())
                .memberId(profile.getMember().getId())
                .memberName(profile.getMember().getName())
                .profileImage(profile.getProfileImage())
                .intro(profile.getIntro())
                .career(profile.getCareer())
                .speciality(profile.getSpeciality())
                .avgRating(profile.getAvgRating())
                .totalReviews(profile.getTotalReviews())
                .isCertified(profile.isCertified())
                .build();
    }
}