package com.example.SevMerge.expertprofile;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    private List<String> skillList;  // speciality 콤마 분리 리스트 (mustache {{#skillList}} 용)
    private String githubUrl;
    private String contactEmail;
    private BigDecimal avgRating;
    private int totalReviews;
    private boolean isCertified;

    // Mustache용 포맷 필드 — {{ratingDisplay}} 로 접근
    public String getRatingDisplay() {
        if (avgRating == null) return "0.0";
        return String.format("%.1f", avgRating);
    }

    public static ExpertProfileResponse from(ExpertProfile profile) {
        return ExpertProfileResponse.builder()
                .id(profile.getId())
                .memberId(profile.getMember().getId())
                .memberName(profile.getMember().getName())
                .profileImage(profile.getProfileImage())
                .intro(profile.getIntro())
                .career(profile.getCareer())
                .speciality(profile.getSpeciality())
                .skillList(parseSkills(profile.getSpeciality()))
                .githubUrl(profile.getGithubUrl())
                .contactEmail(profile.getContactEmail())
                .isCertified(profile.isCertified())
                .build();
    }

    // speciality 문자열을 콤마로 분리해 List 반환
    private static List<String> parseSkills(String speciality) {
        List<String> result = new ArrayList<>();
        if (speciality == null || speciality.isBlank()) return result;
        for (String s : speciality.split(",")) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return result;
    }


}