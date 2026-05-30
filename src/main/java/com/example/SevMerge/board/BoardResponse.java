package com.example.SevMerge.board;

import lombok.Data;

import java.sql.Timestamp;

public class BoardResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private String title;
        private String boardType;
        private String memberName;
        private Timestamp createdAt;
        private Integer viewCount;

        public ListDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.boardType = board.getBoardType().name();
            this.memberName = board.getMember().getName();
            this.createdAt = board.getCreatedAt();
            this.viewCount = board.getViewCount();
        }
    }

    @Data
    public static class DetailDTO {
        private Long id;
        private String title;
        private String content;
        private String boardType;
        private String memberName;
        private Long memberId;
        private Timestamp createdAt;
        private Integer viewCount;

        public DetailDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.boardType = board.getBoardType().name();
            this.memberName = board.getMember().getName();
            this.memberId = board.getMember().getId();
            this.createdAt = board.getCreatedAt();
            this.viewCount = board.getViewCount();
        }
    }
}
