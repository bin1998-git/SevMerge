package com.example.SevMerge.expertprofile;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.CalculateException;
import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "expert_profile_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpertProfile {

    private static final Double MIN_RELIABLE_REVIEWS = 5.0;

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

    @Enumerated(EnumType.STRING) // Enum을 DB에 문자열로 저장
    @Column(nullable = false)
    private Grade expertGrade; // 필드 추가

    @Column(nullable = false)
    private boolean isCertified;

    // 전문가 등급 측정을 위한 편의 메서드
    public Grade checkGrade(Double avgRating, Integer reviewCount ,Integer doneCount, Double globalAvg) {

        if(reviewCount <= 0) {
            return Grade.NORMAL;
        }

        if(globalAvg <3.5) {
            globalAvg = 3.5;
        }

        Double bayesianScore = (reviewCount.doubleValue()/(reviewCount.doubleValue()+MIN_RELIABLE_REVIEWS)) * avgRating
                + (MIN_RELIABLE_REVIEWS/(reviewCount.doubleValue()+MIN_RELIABLE_REVIEWS)) * globalAvg;
        Double workScore = 5.0 / (1+Math.exp(-0.05 * (doneCount.doubleValue()-50)));

        Double score = (bayesianScore*0.7) + (workScore * 0.3);

        if(score < 0 || score > 5.0) {
            throw new CalculateException("점수 산출에 실패했습니다. 관리자에게 문의하세요.");
        } else if (score >= 0 && score < 2.5) {
            return Grade.NORMAL;
        } else if(score >= 2.5 && score < 3.0) {
            return Grade.SKILLED;
        } else if (score >= 3.0) {
            return Grade.MASTER;
        } else {
            return Grade.NORMAL;
        }

    }
}
