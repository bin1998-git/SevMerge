package com.example.SevMerge.expertprofile;

import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "expert_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpertProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 500)
    private String profileImage;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String intro;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String career;

    @Column(nullable = true)
    private String speciality;

    @Column(nullable = false, precision = 3, scale = 2, columnDefinition = "DECIMAL(3,2) DEFAULT 0.00")
    private BigDecimal avgRating;

    @Column(nullable = false)
    private int totalReviews;

    @Column(nullable = false)
    private boolean isCertified;
}
