package com.example.SevMerge.board;

import com.example.SevMerge.core.util.MyDateUtil;
import lombok.Data;

public class BoardResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private String title;
        private String boardType;
        private String memberName;
        private String createdAt;
        private Integer viewCount;
        private String content;
        private BoardInquiryScope inquiryScope;
        private String attachmentUrl;
        private String attachmentName;
        private boolean answered;

        public ListDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.boardType = board.getBoardType().name();
            this.memberName = board.getMember().getName();
            this.viewCount = board.getViewCount();
            this.content = board.getContent();
            this.inquiryScope = board.getInquiryScope();
            this.attachmentUrl = board.getAttachmentUrl();
            this.attachmentName = board.getAttachmentName();
            this.answered = board.getAnswerStatus() == BoardAnswerStatus.ANSWERED;
            if (board.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.timestampFormat(board.getCreatedAt());
            }
        }

        public String getInquiryScopeLabel() {
            if (inquiryScope == null) return "";
            return switch (inquiryScope) {
                case NORMAL -> "일반";
                case PAYMENT -> "결제";
                case SECURITY -> "계정";
            };
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
        private String createdAt;
        private Integer viewCount;
        private String attachmentUrl;
        private String attachmentName;
        private boolean hasAttachment;
        private BoardInquiryScope inquiryScope;

        public DetailDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.boardType = board.getBoardType().name();
            this.memberName = board.getMember().getName();
            this.memberId = board.getMember().getId();
            this.createdAt = board.getCreatedAt() != null ? MyDateUtil.timestampFormat(board.getCreatedAt()) : null;
            this.viewCount = board.getViewCount();
            this.attachmentUrl = board.getAttachmentUrl();
            this.attachmentName = board.getAttachmentName();
            this.hasAttachment = board.getAttachmentUrl() != null && !board.getAttachmentUrl().isEmpty();
            this.inquiryScope = board.getInquiryScope();
        }

        public String getInquiryScopeLabel() {
            if (inquiryScope == null) return "";
            return switch (inquiryScope) {
                case NORMAL -> "일반";
                case PAYMENT -> "결제";
                case SECURITY -> "계정";
            };
        }
    }
}
