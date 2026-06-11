package com.example.SevMerge.comment;

import com.example.SevMerge.board.Board;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.member.Member;
import lombok.Data;

public class CommentRequest {
    @Data
    public static class SaveDTO {
        private Long boardId; // 게시글 PK
        private String content; // 댓글 내용

        // 유효성 검사
        public void validate() {
            if (content == null || content.isBlank()) {
                throw new BadRequestException("댓글 내용을 입력해 주세요.");
            }

            if (content.length() > 500) {
                throw new BadRequestException("댓글을 500자 이하여야 합니다.");
            }

            if (boardId == null) {
                throw new BadRequestException("잘못된 요청입니다.");
            }
        }

        // 댓글 수정 전용 DTO
        @Data
        public static class UpdateDTO {
            private String comment;
            private Long boardId;
        }

        // DTO를 엔티티로 변환
        public Comment toEntity(Member member, Board board) {
            return Comment.builder()
                    .content(this.content)
                    .member(member)
                    .board(board)
                    .build();
        }
    }
}
