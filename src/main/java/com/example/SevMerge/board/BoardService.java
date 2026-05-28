package com.example.SevMerge.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;




    public List<Board> 게시판목록(Type type) {

       return  boardRepository.게시판전체조회(type);
    }



    }




