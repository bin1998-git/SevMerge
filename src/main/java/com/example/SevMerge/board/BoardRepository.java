package com.example.SevMerge.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("""
            SELECT b FROM Board b JOIN FETCH b.member WHERE b.boardType = :boardType AND b.isActive = true
            """)
    List<Board> findAllByBoardTypeWithMemberIsActive(@Param("boardType") BoardType boardType);


    @Query("""
                SELECT b FROM Board b JOIN FETCH b.member WHERE b.id = :boardId
            """)
    Optional<Board> findByIdWithMember(@Param("boardId") Long boardId);
}
