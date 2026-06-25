package com.example.SevMerge.member;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private Role role;
    private Status status;
    private Timestamp createdAt;
    private String profileImage;
    private Integer balance;
    private String provider;

    // 관리자 회원관리 - 순서대로 보여줄 가상번호를 넣어줘야함
    @Setter
    private int virtualNo;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .role(member.getRole())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .profileImage(member.getProfileImage())
                .balance(member.getBalance())
                .provider(member.getProvider())
                .virtualNo(0)
                .build();
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isExpert() {
        return this.role == Role.EXPERT;
    }

    public boolean isClient() {
        return this.role == Role.CLIENT;
    }

    public boolean isSocial() {
        return provider != null && !provider.isBlank();
    }
    /**
     * 액세스 토큰 응답 정보
     */
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OAuthToken {
        private String accessToken;
        private String tokenType;
        private String refreshToken;
        private Integer expiresIn;
        private String scope;
        private Integer refreshTokenExpiresIn;
    }

    /**
     * 카카오 사용자 정보 응답
     */
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KakaoProfile {
        private Long id;
        private KakaoAccount kakaoAccount;


        @Data
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class KakaoAccount {
            private Profile profile;

            @Data
            @NoArgsConstructor
            @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
            public static class Profile {
                private String nickname;
                private String thumbnailImageUrl;
                private String profileImageUrl;
                private Boolean isDefaultImage;
                private Boolean isDefaultNickname;
            }
        }
    }

    /**
     * 구글 액세스 토큰 응답
     */
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class GoogleToken {
        private String accessToken;
        private String tokenType;
        private Integer expiresIn;
        private String scope;
    }

    /**
     * 구글 사용자 정보 응답 (userinfo v3)
     */
    @Data
    @NoArgsConstructor
    public static class GoogleProfile {
        private String sub;    // 구글 고유 ID
        private String name;
        private String email;
        private String picture;
    }

    // 이미지 최종 경로 편의성
    public String getProfileImageUrl() {
        if (profileImage == null || profileImage.isBlank()) {
            return "/images/default.png";
        }
        if (profileImage.startsWith("http")) {
            return profileImage;
        }
        return "/images/" + profileImage;
    }

    // 포맷팅용
    public String getDisplayBalance() {
        return balance == null ? "0" : String.format("%,d", balance);
    }

    public String getRoleName() {
        return this.role != null ? this.role.name() : "일반회원";
    }

    public String getCreatedAtFormatted() {
        if (this.createdAt == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(this.createdAt);
    }
}