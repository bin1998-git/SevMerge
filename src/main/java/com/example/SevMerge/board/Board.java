package com.example.SevMerge.board;

import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name="board_tb")
@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private String content;

    @ColumnDefault("0")
    private Integer viewCount = 0;

    @CreationTimestamp
    private Timestamp createdAt;

    @ColumnDefault("true")
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private BoardInquiryScope inquiryScope;

    // 첨부파일 경로 (단일 파일)
    @Column(length = 500)
    private String attachmentUrl;

    // 첨부파일 원본명
    @Column(length = 200)
    private String attachmentName;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'WAITING'")
    private BoardAnswerStatus answerStatus = BoardAnswerStatus.WAITING;

    @Builder
    public Board(BoardInquiryScope inquiryScope, BoardType boardType, String title, String content,
                 Integer viewCount, Timestamp createdAt, Member member, Boolean isActive,
                 String attachmentUrl, String attachmentName) {
        this.boardType = boardType;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.member = member;
        this.isActive = isActive;
        this.inquiryScope = inquiryScope;
        this.attachmentUrl = attachmentUrl;
        this.attachmentName = attachmentName;
        this.answerStatus = BoardAnswerStatus.WAITING;
    }

    public void markAsAnswered() {
        this.answerStatus = BoardAnswerStatus.ANSWERED;
    }

    public void markAsWaiting() {
        this.answerStatus = BoardAnswerStatus.WAITING;
    }

    public void update(BoardRequest.UpdateBoardDTO dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.inquiryScope = dto.getInquiryScope();
        if (dto.getAttachmentUrl() != null) {
            this.attachmentUrl = dto.getAttachmentUrl();
            this.attachmentName = dto.getAttachmentName();
        }
    }

    public void softDelete() {
        this.isActive = false;
    }
}
