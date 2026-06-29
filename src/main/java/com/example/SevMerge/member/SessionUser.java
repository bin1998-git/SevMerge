package com.example.SevMerge.member;

import lombok.Getter;
import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private Long id;
    private String email;
    private String name;
    private String phone;
    private Role role;
    private Status status;
    private Integer balance;
    private String profileImage;

    public SessionUser(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.role = member.getRole();
        this.status = member.getStatus();
        this.balance = member.getBalance();
        this.profileImage = member.getProfileImage();
    }

    public boolean isAdmin()  { return this.role == Role.ADMIN; }
    public boolean isExpert() { return this.role == Role.EXPERT; }
    public boolean isClient() { return this.role == Role.CLIENT; }
    public boolean isPending() { return this.status == Status.PENDING; }
    public boolean isActiveExpert() { return this.role == Role.EXPERT && this.status == Status.ACTIVE; }

    public String getProfileImageUrl() {
        if (profileImage == null || profileImage.isBlank()) return "/images/default.png";
        if (profileImage.startsWith("http")) return profileImage;
        return "/images/" + profileImage;
    }

    public String getDisplayBalance() {
        return String.format("%,d", this.balance);
    }
}