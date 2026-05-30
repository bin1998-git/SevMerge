package com.example.SevMerge.comment;

import com.example.SevMerge.board.Board;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.member.Member;
import lombok.Data;

public class    CommentRequest {
    @Data
    public static class SaveDTO {
        private Long boardId; // 게시글 PK
        private String comment; // 댓글 내용

        // 유효성 검사
        public void validate() {
            if (comment == null || comment.isBlank()) {
                throw new BadRequestException("댓글 내용을 입력해 주세요.");
            }

            if (comment.length() > 500) {
                throw new BadRequestException("댓글을 500자 이하여야 합니다.");
            }

            if (boardId == null) {
                throw new BadRequestException("잘못된 요청입니다.");
            }
        }

        // DTO를 엔티티로 변환
        public Comment toEntity(Member member, Board board) {
            return Comment.builder()
                    .content(this.comment)
                    .member(member)
                    .board(board)
                    .build();
        }
    }
}
