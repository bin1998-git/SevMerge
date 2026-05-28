package com.example.SevMerge.board;

import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaAttributeConverter<Board, Integer> {

    @Query("""
    SELECT b FROM Board b WHERE b.type = :type
    """)
    public List<Board> 게시판전체조회(@Param("type") Type type);





}
