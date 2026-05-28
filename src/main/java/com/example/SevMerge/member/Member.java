package com.example.SevMerge.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;


@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String email;

        private String password;

        private String name;

        private String phone;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Status status;

        @CreationTimestamp
        private Timestamp createdAt;

        @Builder
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
        }

        // 회원 정보 수정 편의 메서드
        public void update(String password, String name, String phone) {
            if (password != null) this.password = password;
            if (name != null) this.name = name;
            if (phone != null) this.phone = phone;
        }

        // 관리자 여부 확인 (AdminInterceptor 용)
        public boolean isAdmin() {
            return this.role == Role.ADMIN;
        }

        // 전문가 여부 확인
        public boolean isExpert() {
            return this.role == Role.EXPERT;
        }

        // 승인된 전문가 여부 확인
        public boolean isActiveExpert() {
            return this.role == Role.EXPERT && this.status == Status.ACTIVE;
        }

        // 전문가 승인 대기 여부
        public boolean isPending() {
            return this.status == Status.PENDING;
        }

        // 머스태치 화면 편의 매서드
        public String getRoleDisplay() {
            return this.role.name();
        }
    }


