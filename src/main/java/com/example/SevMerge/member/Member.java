package com.example.SevMerge.member;

import com.example.SevMerge.bid.Bid;
import com.example.SevMerge.chatRoom.ChatRoom;
import com.example.SevMerge.project.Project;
import com.example.SevMerge.review.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member_tb")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)  // 구글 로그인 시 비밀번호 없음 → nullable 허용
    private String password;

    // 소셜 로그인 구분 ("LOCAL" | "GOOGLE")
    @Column(length = 20)
    private String provider;

    // 구글 sub 값 (소셜 로그인 고유 ID)
    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    // 소프트 삭제 추가
    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

    // 지갑 잔액 (충전 후 차감 방식)
    @Builder.Default
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer balance = 0;

    // 이미지
    @Column(length = 500)
    private String profileImage;

    //연관관계

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "expert", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Bid> bids = new ArrayList<>();

    @OneToMany(mappedBy = "reviewer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatRoom> clientChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "expert", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ChatRoom> expertChatRooms = new ArrayList<>();

    // 기존 생성자 (유지)
    public Member(Long id, String email, String password,
                  String name, String phone,
                  Role role, Status status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = (role != null) ? role : Role.CLIENT;
        this.status = (status != null) ? status : Status.ACTIVE;
        this.isDeleted = false; // 소프트삭제
        this.balance = 0;
    }

    // 기존 편의 메서드 (유지)
    public void update(String password, String name, String phone) {
        if (password != null) this.password = password;
        if (name != null) this.name = name;
        if (phone != null) this.phone = phone;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isExpert() {
        return this.role == Role.EXPERT;
    }

    public boolean isActiveExpert() {
        return this.role == Role.EXPERT && this.status == Status.ACTIVE;
    }

    public boolean isPending() {
        return this.status == Status.PENDING;
    }

    public String getRoleDisplay() {
        return this.role.name();
    }

    //추가 비즈니스 메서드
    public void approve() {
        this.status = Status.ACTIVE;
    }

    public void reject() {
        this.status = Status.REJECTED;
    }

    public void suspend() {
        this.status = Status.SUSPENDED;
    }

    public void changePassword(String encoded) {
        this.password = encoded;
    }

    public void updateInfo(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public boolean isClient() {
        return this.role == Role.CLIENT;
    }

    //소프트삭제
    public void withdraw() {
        this.isDeleted = true;
    }

    // ── 잔액 관련 ──
    public void addBalance(int amount) {
        this.balance += amount;
    }

    public void deductBalance(int amount) {
        if (this.balance < amount) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
        this.balance -= amount;
    }

    // 이미지 편의 메서드
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}