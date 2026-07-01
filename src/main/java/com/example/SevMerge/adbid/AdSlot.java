package com.example.SevMerge.adbid;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "ad_slot_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String slotName;

    @Column(nullable = false)
    private Integer minBidPrice;

    @Column(name = "bid_start_at", nullable = false)
    private Timestamp bidStartAt;

    @Column(name = "bid_end_at", nullable = false)
    private Timestamp bidEndAt;

    @Column(name = "display_start_at")
    private Timestamp displayStartAt;

    @Column(name = "display_end_at")
    private Timestamp displayEndAt;

    // 낙찰자 ID
    @Column(name = "winner_expert_id")
    private Long winnerExpertId;

    // 낙찰 금액
    @Column(name = "winning_price")
    private Integer winningPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AdSlotStatus status = AdSlotStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "slot_type", nullable = false, length = 30)
    @Builder.Default
    private String slotType = "MAIN_BANNER";

    public void close(Long winnerExpertId, Integer winningPrice,
                      Timestamp displayStartAt, Timestamp displayEndAt) {
        this.status = AdSlotStatus.CLOSED;
        this.winnerExpertId = winnerExpertId;
        this.winningPrice = winningPrice;
        this.displayStartAt = displayStartAt;
        this.displayEndAt = displayEndAt;
    }

    public void award() {
        this.status = AdSlotStatus.AWARDED;
    }

    public void expire() {
        this.status = AdSlotStatus.EXPIRED;
    }

    public boolean isBidding() {
        long now = System.currentTimeMillis();
        return status == AdSlotStatus.OPEN
                && bidStartAt.getTime() <= now
                && bidEndAt.getTime() >= now;
    }

    public boolean isDisplaying() {
        long now = System.currentTimeMillis();
        return status == AdSlotStatus.AWARDED
                && displayStartAt != null
                && displayStartAt.getTime() <= now
                && displayEndAt != null
                && displayEndAt.getTime() >= now;
    }

    public void reopen(Timestamp bidStartAt, Timestamp bidEndAt) {
        this.status = AdSlotStatus.OPEN;
        this.bidStartAt = bidStartAt;
        this.bidEndAt = bidEndAt;
        this.displayStartAt = null;
        this.displayEndAt = null;
        this.winnerExpertId = null;
        this.winningPrice = null;
    }

    public void updateSettings(Integer minBidPrice, Integer durationMinutes) {
        if (minBidPrice != null) this.minBidPrice = minBidPrice;
        if (durationMinutes != null) {
            this.bidEndAt = new Timestamp(this.bidStartAt.getTime() + (long) durationMinutes * 60 * 1000);
        }
    }
}