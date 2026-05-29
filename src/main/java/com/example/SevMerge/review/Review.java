package com.example.SevMerge.review;

import com.example.SevMerge.expert.Expert;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

// 의뢰인도 전무가를 평가 할수있고 전문가도 의뢰인 평가

@Table(name = "review_tb")
@Entity
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id")
    private Expert expert; // 전문가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 의뢰인


    @CreationTimestamp
    private Timestamp createdAt;

    private Integer countStar; // 별점

    private Double totalStar; // 평균별점

    private String content; // 리뷰 내용

}
