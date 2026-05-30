package com.example.SevMerge.board;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.core.exception.UnauthorizedException;
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
    public List<BoardResponse.ListDTO> findAllByBoardType(BoardType BoardType) {
        return  boardRepository.findAllByBoardTypeWithMemberIsActive(BoardType)
                .stream()
                .map(BoardResponse.ListDTO::new)
                .toList();
    }

    public BoardResponse.DetailDTO detailBoard(Long boardId) {
        Board boardEntity = boardRepository.findByIdWithMember(boardId).orElseThrow(
                () -> new NotFoundException("게시글을 찾을 수 없습니다.")
        );

        return new BoardResponse.DetailDTO(boardEntity);
    }

    // 게시글 저장
    @Transactional
    public void saveBoard(Member member,
                          BoardRequest.SaveBoardDTO saveBoardDTO) {

        // 1. 로그인 여부 확인 - 로그인 인터셉트

        Member memberEntity = memberRepository.findById(member.getId()).orElseThrow(
                () -> new BadRequestException("사용자를 찾을 수 없습니다.")
        );

        // 2. 유효성 검사
        saveBoardDTO.validate();

        boardRepository.save(saveBoardDTO.toEntity(memberEntity));
    }

    @Transactional
    public void updateBoard(Long boardId, BoardRequest.UpdateBoardDTO updateBoardDTO, Long memberId) {
        Board boardEntity = boardRepository.findById(boardId).orElseThrow(
                () -> new NotFoundException("게시글을 찾을 수 없습니다.")
        );

        if(!boardEntity.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException("수정 권한이 없습니다.");
        }

        updateBoardDTO.validate();

        boardEntity.update(updateBoardDTO);

        boardRepository.save(boardEntity);
    }



    @Transactional
    public void deleteBoard(Long boardId, Long memberId) {
        Board boardEntity = boardRepository.findByIdWithMember(boardId).orElseThrow(
                () -> new NotFoundException("게시글을 찾을 수 없습니다.")
        );

        if(!boardEntity.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException("삭제 권한이 없습니다.");
        }

        boardEntity.setIsActive(false);

    }
}
