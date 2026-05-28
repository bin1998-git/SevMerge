package com.example.SevMerge.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    @Query("""
    SELECT b FROM Board b WHERE b.type = :type
    """)
    public List<Board> 게시판전체조회(@Param("type") Type type);





}
