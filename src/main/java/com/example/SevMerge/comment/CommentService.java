package com.example.SevMerge.comment;

import com.example.SevMerge.board.Board;
import com.example.SevMerge.board.BoardRepository;
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
    public List<CommentResponse.ListDTO> findComments(Long boardId, Long sessionUserId, String sessionUserRole) {

        List<Comment> commentList = commentRepository.findByBoardIdWithMember(boardId);

        return commentList.stream()
                .map(comment -> new CommentResponse.ListDTO(comment, sessionUserId,  sessionUserRole))
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

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, String newContent, Long sessionUserId) {
        Comment commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("해당 댓글을 찾을 수 없습니다."));

        if (!commentEntity.getMember().getId().equals(sessionUserId)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        commentEntity.updateContent(newContent);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long sessionUserId) { // Integer -> Long 변환
        // 1. 댓글 조회 (Reply -> Comment)
        Comment commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다"));

        // 2. 인가 처리 (본인 확인)
        // 객체 비교(!=) 대신 안전한 Long 타입 값 비교(!equals)를 사용했습니다.
        if (!commentEntity.getMember().getId().equals(sessionUserId)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다");
        }

        // 3. 댓글 삭제
        commentEntity.softDelete();
    }

    // 관리자 전용 전체 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponse.ListDTO> findAllCommentsForAdmin(String keyword) {
        List<Comment> commentList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            commentList = commentRepository.findByContentContainingForAdmin(keyword.trim());
        } else {
            commentList = commentRepository.findAllWithMemberAndBoard();
        }
        return commentList.stream()
                .map(comment -> new CommentResponse.ListDTO(comment, null, null))
                .toList();
    }

    // 관리자 전용 삭제
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        Comment commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        commentEntity.softDelete();
    }

}
