package com.example.SevMerge.member;

import lombok.*;

public class MemberRequest {

    @Data
    public static class Join {
        private String email;
        private String password;
        private String name;
        private String phone;
        private Role role;
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
    }

    @Data
    public static class ChangePassword {
        private String currentPassword;
        private String newPassword;
    }
}