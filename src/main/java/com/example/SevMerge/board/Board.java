package com.example.SevMerge.board;


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
    private Integer id;

    @ManyToOne(fetch =  FetchType.LAZY)
    private Member member;

    private String title;

    private String content;

    @CreationTimestamp
    private Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Integer viewCount;

    // TODO Comment 테이블

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
