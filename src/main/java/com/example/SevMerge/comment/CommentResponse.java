package com.example.SevMerge.comment;

import com.example.SevMerge.core.util.MyDateUtil;
import lombok.Data;

public class CommentResponse {
    /**
     * 댓글 목록 응답 DTO
     */
    @Data
    public static class ListDTO {
        private Long id; // 댓글 pk
        private String comment; // 댓글 내용
        private Long memberId; // 댓글 작성자 PK
        private String memberName; // 댓글 작성자 이름
        private Boolean isOwner; // 댓글 소유자 여부 (로그인유저랑 비교)
        private String createdAt; // 댓글 작성 시간
        private Long boardId;
        private Boolean isCommentAdmin;

        public ListDTO(Comment comment, Long sessionUserId, String sessionUserRole) {
            this.id = comment.getId();
            this.comment = comment.getContent();

            // JOIN FETCH로 한방에 들고올 예정
            if (comment.getMember() != null) {
                this.memberId = comment.getMember().getId();
                this.memberName = comment.getMember().getName();
                this.isCommentAdmin = comment.getMember().getRole() != null && "ADMIN".equalsIgnoreCase(comment.getMember().getRole().toString());
            }

            // 게시글 ID추출
            if (comment.getBoard() != null) {
                this.boardId = comment.getBoard().getId();
            }

            if (comment.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.timestampFormat(comment.getCreatedAt());
            }

            this.isOwner = sessionUserId != null && this.memberId != null && this.memberId.equals(sessionUserId);

        }
    }
}
