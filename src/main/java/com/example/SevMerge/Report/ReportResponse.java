package com.example.SevMerge.Report;

import lombok.Data;

import java.text.SimpleDateFormat;

public class ReportResponse {
    @Data
    public static class ListDTO {
        private Long id; // 신고 PK번호
        private Long commentId; // 원본 댓글 PK
        private Long boardId; // 이동할 게시글 PK
        private String commentContent; // 신고당한 원본 댓글내용
        private String reporterName; // 신고한 사람이름
        private String commentWriterName; // 실제 댓글작성한 사람이름
        private String reason; // 신고 분류 (스팸, 욕설)
        private String contentDetail; // 사용자가 적은 상세내용
        private String createdAt; // 신고일자
        private boolean isProcessed;

        public ListDTO(Report report) {
            this.id = report.getId();
            this.isProcessed = report.isProcessed();

            if (report.getComment() != null) {
                this.commentId = report.getComment().getId();
                this.commentContent = report.getComment().getContent();

                if (report.getComment().getMember() != null) {
                    this.commentWriterName = report.getComment().getMember().getName();
                } else {
                    this.commentWriterName = "알 수 없는 사용자입니다.";
                }

                if (report.getComment().getBoard() != null) {
                    this.boardId = report.getComment().getBoard().getId();
                }
            } else { // 원본 댓글이 완전히 존재하지 않을경우 방어코드
                this.commentContent = "삭제되거나 존재하지 않는 댓글입니다.";
                this.commentWriterName = "-";
            }

            if (report.getReporter() != null) {
                this.reporterName = report.getReporter().getName();
            }

            this.reason = report.getReason();
            this.contentDetail = report.getContentDetail();

            if (report.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                this.createdAt = sdf.format(report.getCreatedAt());
            }
        }

        public boolean getIsProcessed() {
            return this.isProcessed;
        }
    }

    @Data
    public static class CommentReportSummaryDTO {
        private Long commentId;
        private Long boardId;
        private String commentContent;
        private String commentWriterName;
        private int reportCount;
        private boolean reReported;
        private boolean commentDeleted;
        private String reasons;
        private String latestReportedAt;

        public CommentReportSummaryDTO(java.util.List<Report> reports) {
            this.reportCount = reports.size();
            this.reReported = reports.size() >= 2;

            com.example.SevMerge.comment.Comment c = reports.get(0).getComment();
            if (c != null) {
                this.commentId = c.getId();
                this.commentContent = c.getContent();
                this.commentDeleted = c.isDeleted();
                this.commentWriterName = (c.getMember() != null)
                        ? c.getMember().getName() : "알 수 없는 사용자";
                if (c.getBoard() != null) this.boardId = c.getBoard().getId();
            } else {
                this.commentContent = "삭제되거나 존재하지 않는 댓글입니다.";
                this.commentWriterName = "-";
                this.commentDeleted = true;
            }

            this.reasons = reports.stream()
                    .map(Report::getReason)
                    .filter(r -> r != null && !r.isEmpty())
                    .distinct()
                    .collect(java.util.stream.Collectors.joining(", "));

            java.sql.Timestamp latest = null;
            for (Report r : reports) {
                if (r.getCreatedAt() != null && (latest == null || r.getCreatedAt().after(latest))) {
                    latest = r.getCreatedAt();
                }
            }
            if (latest != null) {
                this.latestReportedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(latest);
            }
        }

        public boolean getReReported() { return this.reReported; }
        public boolean getCommentDeleted() { return this.commentDeleted; }
    }
}
