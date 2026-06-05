package com.example.SevMerge.review;

import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
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
    @JoinColumn(name = "reviewer_id" )
    private Member reviewer; // 리뷰 작성자

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "targeter_id" )
    private Member targeter; // 리뷰 수신자

    @CreationTimestamp
    private Timestamp createdAt; // 작성일

    private Integer countStar; // 별점

    private String content; // 리뷰 내용

    @ColumnDefault("false")
    private boolean isDelete = false;


    public void update(ReviewRequest.UpdateRequestDTO updateDTO) {
        this.content = updateDTO.getContent();
        this.countStar = updateDTO.getRating();
    }

    public void softDelete() {
        this.isDelete = true;
    }
}