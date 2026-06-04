package com.example.SevMerge.member;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
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

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .role(member.getRole())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .build();
    }
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isExpert() {
        return this.role == Role.EXPERT;
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
}