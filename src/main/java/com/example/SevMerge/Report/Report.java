package com.example.SevMerge.Report;

import com.example.SevMerge.comment.Comment;
import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment_report_tb")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    @Column(length = 100, nullable = false)
    private String reason;

    @Column(length = 1000, nullable = false)
    private String contentDetail;

    @CreationTimestamp
    private Timestamp createdAt;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isProcessed = false;

    @Builder
    public Report(Comment comment, Member reporter, String reason, String contentDetail) {
        this.comment = comment;
        this.reporter = reporter;
        this.reason = reason;
        this.contentDetail = contentDetail;
        this.isProcessed = false;
    }

    public void completeProcessing() {
        this.isProcessed = true;
    }


}
