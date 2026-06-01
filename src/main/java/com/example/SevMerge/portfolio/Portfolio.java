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
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id")
    private ExpertProfile expertProfile;
    @Column(nullable = false)
    private String title;
    private String description;
    private String imageUrl;
    private String projectUrl;
    @CreationTimestamp
    private Timestamp createdAt;
    @ColumnDefault("true")
    private boolean isActive;
}
