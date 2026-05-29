package com.example.SevMerge.board;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 게시글 조회
    public List<Board> findAllByBoardType(BoardType BoardType) {
        return  boardRepository.findAllByBoardType(BoardType);
    }

    // 게시글 저장
    @Transactional
    public void saveBoard(Member member,
                          BoardRequest.SaveBoardDTO saveBoardDTO) {

        // 1. 로그인 여부 확인 - 로그인 인터셉트

        // todo - findById()통해서 멤버 찾기
        Member memberEntity = memberRepository.findById(member.getId()).orElseThrow(
                () -> new BadRequestException("사용자를 찾을 수 없습니다.")
        );

        // 2. 유효성 검사
        saveBoardDTO.validate();

        Board newBoard = Board.builder()
                .title(saveBoardDTO.getTitle())
                .content(saveBoardDTO.getContent())
                .boardType(saveBoardDTO.getBoardType())
                .member(memberEntity)
                .build();

        // 3. 게시글 작성(insert)
        log.info("게시글 저장 요청 - title: {}, boardType: {}, content: {}",
                saveBoardDTO.getTitle(),saveBoardDTO.getBoardType(),saveBoardDTO.getContent());
        boardRepository.save(newBoard);
    }

    public void updateBoard(Integer boardId, BoardRequest.updateBoardDTO updateBoardDTO) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new NotFoundException("게시글을 찾을 수 없습니다.")
        );

        board.setTitle(updateBoardDTO.getTitle());
        board.setTitle(updateBoardDTO.getContent());
        log.info("게시글 수정 요청 - title: {}, content: {}",
                board.getTitle(),board.getContent());

        boardRepository.save(board);
    }
}
