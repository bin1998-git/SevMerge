package com.example.SevMerge.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board,Integer> {

    @Query("""
    SELECT b FROM Board b JOIN FETCH b.member WHERE b.boardType = :boardType
    """)
    List<Board> findAllByBoardTypeWithMember(@Param("boardType") BoardType type);


}
