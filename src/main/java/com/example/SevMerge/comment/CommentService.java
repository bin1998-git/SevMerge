package com.example.SevMerge.comment;

import com.example.SevMerge.board.Board;
import com.example.SevMerge.board.BoardRepository;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 댓글 목록 조회
    public List<CommentResponse.ListDTO> findComments(Long boardId) {

        List<Comment> commentList = commentRepository.findByBoardIdWithMember(boardId);

        return commentList.stream()
                .map(CommentResponse.ListDTO::new)
                .toList();
    }

    // 댓글 작성
    public Comment createComment(CommentRequest.SaveDTO saveDTO, long id) {
        // 게시글 조회
        Board boardEntity = boardRepository.findById(saveDTO.getBoardId()).orElseThrow(
                () -> new NotFoundException("해당 게시글을 찾을 수 없습니다."));

        // id로 사용자 조회
        Member memberEntity = memberRepository.findById(id).orElseThrow(
                () -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Comment comment = saveDTO.toEntity(memberEntity, boardEntity);
        commentRepository.save(comment);
        return comment;
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Integer commentId, long sessionMemberId) {
        Comment commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("해당 게시글을 찾을 수 없습니다."));

        // 인가처리
        if (commentEntity.getMember().getId() != sessionMemberId) {
            throw new ForbiddenException("댓글 삭제 권한이 없습니다.");
        }

        // 댓글 삭제
        commentRepository.delete(commentEntity);
    }
}
