package com.example.SevMerge.revenue;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "platform_revenue_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlatformRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlatformRevenueType type;

    @Column(nullable = false)
    private Integer amount;

    // 참조 ID (광고 ID, 제휴 ID 등)
    @Column(name = "reference_id")
    private Long referenceId;

    // 수익 발생 대상 (전문가 ID 등)
    @Column(name = "target_id")
    private Long targetId;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlatformRevenueStatus status = PlatformRevenueStatus.CONFIRMED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    public void cancel() {
        this.status = PlatformRevenueStatus.CANCELLED;
    }

    public boolean isAd()          { return this.type == PlatformRevenueType.AD; }
    public boolean isPartnership()  { return this.type == PlatformRevenueType.PARTNERSHIP; }
    public boolean isCommission()   { return this.type == PlatformRevenueType.COMMISSION; }
    public boolean isConfirmed()    { return this.status == PlatformRevenueStatus.CONFIRMED; }
    public boolean isCancelled()    { return this.status == PlatformRevenueStatus.CANCELLED; }

    public String getCreatedAtFormatted() {
        if (createdAt == null) return "";
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(createdAt);
    }
}