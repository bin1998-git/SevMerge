package com.example.SevMerge.review;

import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

// 의뢰인도 전무가를 평가 할수있고 전문가도 의뢰인 평가

@Table(name = "review_tb")
@Entity
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.REMOVE)
    @JoinColumn(name = "expert_profile_id" )
    private ExpertProfile expertProfile; // 전문가

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id" )
    private Member member; // 의뢰인

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;


    @CreationTimestamp
    private Timestamp createdAt; // 작성일

    private Integer countStar; // 별점

    private String content; // 리뷰 내용


}
