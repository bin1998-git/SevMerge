package com.example.SevMerge.deliverable;

import lombok.Data;

import java.text.SimpleDateFormat;

public class DeliverableResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private int round;
        private String fileUrl;
        private String fileName;
        private String note;
        private boolean isFinal;
        private String status;
        private boolean isSubmitted;
        private boolean isRevisionRequested;
        private boolean isApproved;
        private String feedback;
        private String createdAt;

        public ListDTO(Deliverable d) {
            this.id = d.getId();
            this.round = d.getRound();
            this.fileUrl = d.getFileUrl();
            this.fileName = d.getFileName();
            this.note = d.getNote();
            this.isFinal = d.getIsFinal();
            this.status = d.getStatus().name();
            this.isSubmitted = d.getStatus() == DeliverableStatus.SUBMITTED;
            this.isRevisionRequested = d.getStatus() == DeliverableStatus.REVISION_REQUESTED;
            this.isApproved = d.getStatus() == DeliverableStatus.APPROVED;
            this.feedback = d.getFeedback();
            if (d.getCreatedAt() != null) {
                this.createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(d.getCreatedAt());
            }
        }

        public boolean getIsFinal() { return this.isFinal; }
        public boolean getIsSubmitted() { return this.isSubmitted; }
        public boolean getIsRevisionRequested() { return this.isRevisionRequested; }
        public boolean getIsApproved() { return this.isApproved; }
    }
}
