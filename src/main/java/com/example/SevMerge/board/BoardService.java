package com.example.SevMerge.board;

import com.example.SevMerge.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    public void saveBoard(Member member,
                          BoardRequest.SaveBoardDTO saveBoardDTO) {

        // 1. 로그인 여부 확인 - 로그인 인터셉트

        // todo - findById()통해서 멤버 찾기

        // 2. 유효성 검사
        saveBoardDTO.validate();

        Board newBoard = Board.builder()
                .title(saveBoardDTO.getTitle())
                .content(saveBoardDTO.getContent())
                .boardType(saveBoardDTO.getBoardType())
                .member(member)
                .build();

        // 3. 게시글 작성(insert)
        boardRepository.save(newBoard);

    }
}
