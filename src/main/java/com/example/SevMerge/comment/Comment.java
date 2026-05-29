package com.example.SevMerge.comment;

import com.example.SevMerge.board.Board;
import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comment_tb")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

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

    @Builder
    public Comment(Board board, Member member, String content) {
        this.board = board;
        this.member = member;
        this.content = content;
    }

    /**
     * 댓글 소유자 확인 로직 (세션 정보, DB에 작성된 user_id확인용)
     */
    public boolean isOwner(Integer memberId) {
        if (this.member == null || memberId == null) {
            return false;
        }
        return true;
    }
}
