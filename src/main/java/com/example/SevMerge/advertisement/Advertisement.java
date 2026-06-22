package com.example.SevMerge.advertisement;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

/**
 * Advertisement Entity
 *
 * [충돌 방지 전략]
 * - Member, ExpertProfile 등 타 도메인 Entity를 직접 참조하지 않고
 *   FK를 Long 타입으로만 보관합니다. (@ManyToOne 미사용)
 */
@Entity
@Table(name = "advertisement_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expert_id", nullable = false)
    private Long expertId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdvertisementPlacement placement;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "start_date", nullable = false)
    private Timestamp startDate;

    @Column(name = "end_date", nullable = false)
    private Timestamp endDate;

    @Column(name = "custom_message", length = 100)
    private String customMessage;   // 홍보 문구용

    @Column(name = "banner_image", length = 500)
    private String bannerImage;     // 전용 배너 이미지용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AdvertisementStatus status = AdvertisementStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    public boolean isCurrentlyActive() {
        long now = System.currentTimeMillis();
        return status == AdvertisementStatus.ACTIVE
                && startDate.getTime() <= now
                && endDate.getTime() >= now;
    }

    public void expire() {
        this.status = AdvertisementStatus.EXPIRED;
    }
}