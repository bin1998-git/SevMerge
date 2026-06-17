package com.example.SevMerge.expertprofile;

import com.example.SevMerge.member.Member;
import com.example.SevMerge.review.Review;
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

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = true)
    private String profileImage;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String intro;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String career;

    private String speciality;

    @Column(nullable = true)
    private String githubUrl;

    @Column(nullable = true)
    private String contactEmail;

    @Column(nullable = false)
    private boolean isCertified;
}
