package com.example.SevMerge.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stringtemplate.v4.ST;

import java.util.List;
import java.util.Optional;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {

    // 나의 북마크 리스트
    @Query("""
        SELECT b FROM BookMark b
        JOIN FETCH b.expertProfile ep
        JOIN FETCH ep.member
        WHERE b.member.id = :memberId
    """)
    List<BookMark> findAllByMemberId (@Param("memberId") Long memberId);



    // 해당 유저가 전문가를 북마크 했는지 확인하는 메서드
    // 내 아이디로 전문가 마킹 마킹 완료한 것만 BookMark 객체 반환 즉 DB에 존재한다
    @Query("""
                SELECT b FROM BookMark b
                         JOIN FETCH b.expertProfile e
                         WHERE b.member.id = :memberId AND e.id = :expertId
            """)
    Optional<BookMark> findByMemberIdAndExpertProfileId(@Param("memberId") Long memberId,@Param("expertId") Long expertId);


    // 필터 검색 기능
    // 전문가 이름으로 검색
    @Query("""
           SELECT b FROM BookMark b
           JOIN FETCH b.expertProfile ep
           JOIN FETCH ep.member
           WHERE ep.member.name LIKE %:keyword% AND b.member.id = :memberId
        """)
    List<BookMark> findFilterByName(@Param("keyword")String keyword, @Param("memberId") Long memberId);


}
