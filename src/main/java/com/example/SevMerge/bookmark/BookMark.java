package com.example.SevMerge.bookmark;


import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "book_mark_tb")
@Entity
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class BookMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 북마크 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id")
    private ExpertProfile expertProfile; // 내가 마킹한 전문가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 나

}
