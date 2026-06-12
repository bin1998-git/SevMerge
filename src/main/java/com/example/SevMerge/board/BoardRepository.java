package com.example.SevMerge.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 게시글 조회
    @Query("""
            SELECT b FROM Board b JOIN FETCH b.member WHERE b.boardType = :boardType AND b.isActive = true
            """)
    List<Board> findAllByBoardTypeIsActive(@Param("boardType") BoardType boardType);

    @Query(""" 
            SELECT b FROM Board b JOIN FETCH b.member
            WHERE b.boardType = :boardType
            AND b.isActive = true
            AND (:keyword = '' OR b.title LIKE %:keyword%)
            ORDER BY b.createdAt DESC""")
    Page<Board> findAllByBoardTypeAndKeyword(
            @Param("boardType") BoardType boardType,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("""
                SELECT b FROM Board b JOIN FETCH b.member
                WHERE b.member.id = :memberId
            """)
    List<Board> findByMyBoard(@Param("memberId") Long memberId);

    // 관리자 게시판 키워드 검색 조회
    @Query("""
                    select b from Board b join fetch b.member where b.boardType = :boardType and (b.title LIKE %:keyword% OR b.content LIKE %:keyword%)
            """)
    List<Board> findAllByBoardTypeAndKeyword(
            @Param("boardType") BoardType boardType,
            @Param("keyword") String keyword
    );

    // 1:1 문의 게시글 조회
    @Query("""
            SELECT b FROM Board b JOIN FETCH b.member WHERE b.boardType = :boardType AND b.member.id = :memberId AND b.isActive = true
            """)
    List<Board> findInquiryByBoardTypeWithMemberIdAndIsActive(@Param("boardType") BoardType boardType, Long memberId);

    // 사용자가 작성한 게시글 검색 용
    @Query("""
                SELECT b FROM Board b JOIN FETCH b.member WHERE b.id = :boardId
            """)
    Optional<Board> findByIdWithMember(@Param("boardId") Long boardId);

    @Modifying
    @Query("""
                UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :boardId
            """)
    void increaseViewCount(@Param("boardId") Long boardId);
}
