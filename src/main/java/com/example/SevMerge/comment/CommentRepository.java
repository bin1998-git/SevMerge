package com.example.SevMerge.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // 게시글 ID로 댓글 목록 조회 (한번에 댓글 작성자 정보 포함 - JOIN FETCH 사용)
    @Query("""
                SELECT c FROM Comment c JOIN FETCH c.member
                                WHERE c.board.id = :boardId
            """)
    List<Comment> findByBoardIdWithMember(@Param("boardId") Long boardId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.board.id = :boardId")
    void deleteByBoardId(@Param("boardId") Long boardId);
}
