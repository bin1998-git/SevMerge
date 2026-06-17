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

    /**
     * 지갑 잔액 (충전 후 차감 방식)
     * 충전된 돈을 플랫폼 안에서 가상 화폐처럼 쌓아두고(balance),
     * 그 잔액으로 프로젝트 결제·정산·환불을 처리하는 구조
     */
    @Builder.Default
    @Column(nullable = false, columnDefinition = "int default 0")
    private Integer balance = 0;

    // 이미지
    @Column(length = 500)
    private String profileImage;

    // 신고 카운트
    @Builder.Default
    @Column(nullable = false)
    private int reportCount = 0; // 신고 누적 횟수

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

    // 생성자
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
        this.reportCount = 0;
    }

    public void reapply() {
        this.status = Status.PENDING;
    }

    //  편의 메서드
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

    // 이미지 소셜 http URL은 그대로, 업로드 파일은 /images/ 처리
    public String getProfileImageUrl() {
        if (profileImage == null || profileImage.isBlank()) {
            return "/images/default.png";
        }
        if (profileImage.startsWith("http")) {
            return profileImage;
        }
        return "/images/" + profileImage;
    }

    // 포맷팅
    public String getDisplayBalance() {
        return String.format("%,d", this.balance);
    }

    public void addReport() {
        this.reportCount++;
    }

    // 상태 바꿔주는 메소드 차단해제시켜준다던지 머 그런거
    public void changeStatusByBlacklist(Status nextStatus) {
        this.status = nextStatus;
    }

    // 정지인지 영구삭제인지
    public boolean isBlacklisted() {
        return this.status == Status.BLACKLISTED || this.status == Status.SUSPENDED;
    }

    // 누적 신고 횟수를 다시 0으로 리셋시키기
    public void resetReportCount() {
        this.reportCount = 0;
    }
}