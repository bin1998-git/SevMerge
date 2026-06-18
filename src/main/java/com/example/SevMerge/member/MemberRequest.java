package com.example.SevMerge.member;

import com.example.SevMerge.core.exception.BadRequestException;
import lombok.*;

public class MemberRequest {

    @Data
    public static class Join {
        private String email;
        private String password;
        private String name;
        private String phone;
        private Role role;

        private String intro;
        private String career;
        private String githubUrl;
        private String speciality;
    }

    @Data
    public static class ExpertJoin {
        private String name;
        private String email;
        private String intro;
        private String career;
        private String githubUrl;
        private String speciality;
    }

    @Data
    public static class Login {
        private String email;
        private String password;
    }

    @Data
    public static class Update {
        private String name;
        private String phone;
        private String currentPassword;
        private String newPassword;
    }

    @Data
    public static class ChangePassword {
        private String currentPassword;
        private String newPassword;
    }

    @Data
    public static class EmailCheckDTO {
        private String email;
        private String code;

        public void validate() {
            if (email == null || email.trim().isEmpty()) {
                throw new BadRequestException("이메일을 입력해주세요");
            }
            if (!email.contains("@")) {
                throw new BadRequestException("올바른 이메일 형식이 아닙니다");
            }
        }
    }
}