package com.example.SevMerge.member;

import lombok.*;

public class MemberRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Join {
        private String email;
        private String password;
        private String name;
        private String phone;
        private Role role;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        private String email;
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private String name;
        private String phone;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePassword {
        private String currentPassword;
        private String newPassword;
    }
}