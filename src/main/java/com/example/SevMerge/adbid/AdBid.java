package com.example.SevMerge.adbid;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "ad_bid_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_id", nullable = false)
    private Long slotId;

    @Column(name = "expert_id", nullable = false)
    private Long expertId;

    @Column(name = "bid_price", nullable = false)
    private Integer bidPrice;

    // 배너 이미지 (입찰 시 제출)
    @Column(name = "banner_image", length = 500)
    private String bannerImage;

    // 광고 문구 (입찰 시 제출)
    @Column(name = "ad_message", length = 200)
    private String adMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AdBidStatus status = AdBidStatus.PENDING;

    // 낙찰 후 관리자 검토 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false)
    @Builder.Default
    private AdBidReviewStatus reviewStatus = AdBidReviewStatus.NONE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    public void win() {
        this.status = AdBidStatus.WINNER;
        this.reviewStatus = AdBidReviewStatus.NONE; // 낙찰시 배너이미지제출
    }

    public void lose() {
        this.status = AdBidStatus.LOST;
    }

    public void refund() {
        this.status = AdBidStatus.REFUNDED;
    }

    public void approveReview() {
        this.reviewStatus = AdBidReviewStatus.APPROVED;
    }

    public void rejectReview() {
        this.reviewStatus = AdBidReviewStatus.REJECTED;
    }

    // 메인화면 노출 가능 여부
    public boolean isDisplayable() {
        return this.status == AdBidStatus.WINNER
                && this.reviewStatus == AdBidReviewStatus.APPROVED;
    }

    public void submitBanner(String bannerImage, String adMessage) {
        this.bannerImage = bannerImage;
        this.adMessage = adMessage;
        this.reviewStatus = AdBidReviewStatus.PENDING;
    }

    public boolean isApprovedReview() {
        return this.reviewStatus == AdBidReviewStatus.APPROVED;
    }

    public boolean isRejectedReview() {
        return this.reviewStatus == AdBidReviewStatus.REJECTED;
    }
}