package com.example.SevMerge.portfolio;

import com.example.SevMerge.expertprofile.ExpertProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name="portfolio_tb")
@Builder
@AllArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id")
    private ExpertProfile expertProfile; // 전문가
    @Column(nullable = false)
    private String title; // 제목
    private String description; // 설명
    private String imageUrl; // 이미지 링크
    private String projectUrl; // 프로젝트 링크
    @CreationTimestamp
    private Timestamp createdAt; // 작성일
    @ColumnDefault("true")
    private boolean isActive; // 소프트 삭제



}
