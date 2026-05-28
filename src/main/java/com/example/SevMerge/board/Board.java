package com.example.SevMerge.board;


import com.example.SevMerge.comment.Comment;
import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


import java.sql.Timestamp;
import java.util.List;

@Table(name = "board_tb")
@Entity
@Data
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 50,nullable = false)
    private String title;

    @Column(length = 1000,nullable = false)
    private String content;

    @CreationTimestamp
    private Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Integer viewCount;

    // TODO Comment 테이블

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id",nullable = false)
    private List<Comment> comment;

    @Builder
    public Board(Member member, String title, String content, Timestamp createdAt, Integer viewCount,Type type) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.viewCount = viewCount;
        this.type = type;
    }





}
