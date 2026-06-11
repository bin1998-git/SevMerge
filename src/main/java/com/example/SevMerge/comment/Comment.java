package com.example.SevMerge.comment;

import com.example.SevMerge.board.Board;
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
@Table(name = "comment_tb")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 500, nullable = false)
    private  String content;

    @CreationTimestamp
    private Timestamp createdAt;

    private boolean isDeleted = false;

    @Builder
    public Comment(Board board, Member member, String content) {
        this.board = board;
        this.member = member;
        this.content = content;
        this.isDeleted = false;
    }

    // 편의 메소드 추가
    public void softDelete() {
        this.isDeleted = true;
    }

    /**
     * 댓글 소유자 확인 로직 (세션 정보, DB에 작성된 user_id확인용)
     */
    public boolean isOwner(Long memberId) {
        if (this.member == null || memberId == null) {
            return false;
        }
        return true;
    }
}
