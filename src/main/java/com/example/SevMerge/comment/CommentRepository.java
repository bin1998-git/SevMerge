package com.example.SevMerge.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시글 ID로 댓글 목록 조회 (한번에 댓글 작성자 정보 포함 - JOIN FETCH 사용)
    @Query("""
                SELECT c FROM Comment c JOIN FETCH c.member
                WHERE c.board.id = :boardId AND c.isDeleted = false
            """)
    List<Comment> findByBoardIdWithMember(@Param("boardId") Long boardId);

    // 댓글 삭제
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Comment c SET c.isDeleted = true WHERE c.id = :commentId")
    void deleteByCommentId(@Param("commentId") Long commentId);

    // 관리자용 전체 댓글 목록 조회
    @Query("""
        SELECT c FROM Comment c 
        JOIN FETCH c.member 
        JOIN FETCH c.board 
        WHERE c.isDeleted = false
        ORDER BY c.id DESC
    """)
    List<Comment> findAllWithMemberAndBoard();

    // 키워드가 포함된 댓글만 필터링하는 검색전용기능
    @Query("""
        SELECT c FROM Comment c 
        JOIN FETCH c.member 
        JOIN FETCH c.board 
        WHERE c.isDeleted = false 
          AND (c.content LIKE %:keyword% OR c.member.name LIKE %:keyword%)
        ORDER BY c.id DESC
    """)
    List<Comment> findByContentContainingForAdmin(@Param("keyword") String keyword);
}
