package com.example.SevMerge.expertwish;

import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "expert_wish_tb", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "expert_id"})
}) // 중복 찜 방지
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpertWish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private Member expert;

    @CreationTimestamp
    private Timestamp createdAt;
}
