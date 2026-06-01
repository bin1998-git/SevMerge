package com.example.SevMerge.board;

import org.springframework.data.jpa.repository.JpaRepository;
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

    // 1:1 문의 게시글 조회
    @Query("""
            SELECT b FROM Board b JOIN FETCH b.member WHERE b.boardType = :boardType AND b.member.id = :memberId AND b.isActive = true
            """)
    List<Board> findInquiryByBoardTypeWithMemberIdAndIsActive(@Param("boardType") BoardType boardType,Long memberId);

    // 사용자가 작성한 게시글 검색 용
    @Query("""
                SELECT b FROM Board b JOIN FETCH b.member WHERE b.id = :boardId
            """)
    Optional<Board> findByIdWithMember(@Param("boardId") Long boardId);
}
