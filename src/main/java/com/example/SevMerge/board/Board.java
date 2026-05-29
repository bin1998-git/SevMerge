package com.example.SevMerge.board;


import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name="user_tb")
@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private BoardType boardType;
    private String title;
    private String content;
    private Integer viewCount;
    @CreationTimestamp
    private Timestamp createdAt;

    // todo - 추후 Member Class추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name="comment_id")
    // todo - 추구 Comment Class 추가
    //private Comment comment;

    @Builder
    public Board(BoardType boardType, String title, String content, Integer viewCount, Timestamp createdAt, Member member) {
        this.boardType = boardType;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.member = member;
        //this.comment = comment;
    }
}
