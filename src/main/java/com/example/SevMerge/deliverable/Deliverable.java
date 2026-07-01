package com.example.SevMerge.deliverable;

import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "deliverable_tb")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deliverable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private Member expert;

    @Column(nullable = false)
    private int round;

    @Column(length = 255)
    private String fileUrl;

    @Column(length = 255)
    private String fileName;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false)
    private boolean isFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliverableStatus status;

    @Column(length = 1000)
    private String feedback;

    @CreationTimestamp
    private Timestamp createdAt;

    public void requestRevision(String reason) {
        this.status = DeliverableStatus.REVISION_REQUESTED;
        this.feedback = reason;
    }

    public void approve() {
        this.status = DeliverableStatus.APPROVED;
    }

    public boolean getIsFinal() {
        return this.isFinal;
    }
}
