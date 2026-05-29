package com.example.SevMerge.comment;

import com.example.SevMerge.core.util.MyDateUtil;
import lombok.Data;

public class CommentResponse {
    /**
     * 댓글 목록 응답 DTO
     */
    @Data
    public static class ListDTO {
        private Integer id; // 댓글 pk
        private String comment; // 댓글 내용
        private long userId; // 댓글 작성자 PK
        private String username; // 댓글 작성자 이름
        private boolean isOwner; // 댓글 소유자 여부 (로그인유저랑 비교)
        private String createdAt; // 댓글 작성 시간

        public ListDTO(Comment comment, Integer sessionUserId) {
            this.id = comment.getId();
            this.comment = comment.getContent();

            // JOIN FETCH로 한방에 들고올 예정
            if (comment.getMember() != null) {
                this.userId = comment.getMember().getId();
                this.username = comment.getMember().getName();
            }

            if (comment.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.timestampFormat(comment.getCreatedAt());
            }
            this.isOwner = comment.isOwner(sessionUserId);
        }
    }
}
