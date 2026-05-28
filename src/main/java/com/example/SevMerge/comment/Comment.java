package com.example.SevMerge.comment;


import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name = "comment_tb")
@Entity
@Data
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 250,nullable = false)
    private String content;

}
