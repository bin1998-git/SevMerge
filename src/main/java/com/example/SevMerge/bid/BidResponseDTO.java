package com.example.SevMerge.bid;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

public class BidResponseDTO {

    // 제안서 조회
    @Data
    @NoArgsConstructor
    public static class ListDTO {
        private Long id;
        private Long projectId;
        private String projectTitle;
        private Long expertId;
        private Long memberId;
        private String expertName;
        private String coverLetter;
        private String approach;
        private Long estimatedDays;
        private Long proposedPrice;
        private String status;
        private boolean isPending;
        private boolean isSelected;
        private boolean isRejected;
        private Timestamp createdAt;

        public ListDTO(Bid bid) {
            this.id = bid.getId();
            this.projectId = bid.getProject().getId();
            this.projectTitle = bid.getProject().getTitle();
            this.expertId = bid.getExpert().getId();
            this.memberId = bid.getProject().getMember().getId();
            this.expertName = bid.getExpert().getName();
            this.coverLetter = bid.getCoverLetter();
            this.approach = bid.getApproach();
            this.estimatedDays = bid.getEstimatedDays();
            this.proposedPrice = bid.getProposedPrice();
            this.status = bid.getStatus().name();
            // 엔티티의 status를 기반으로 true / false 지정
            this.isPending = bid.getStatus() == BidStatus.PENDING;
            this.isSelected = bid.getStatus() == BidStatus.SELECTED;
            this.isRejected = bid.getStatus() == BidStatus.REJECTED;
            this.createdAt = bid.getCreatedAt();
        }
    }

    @Data
    @NoArgsConstructor
    public static class DetailDTO {
        private Long id;
        private Long projectId;
        private String projectTitle;
        private Long expertId;
        private String expertName;
        private String coverLetter;
        private String approach;
        private Long estimatedDays;
        private Long proposedPrice;
        private String status;
        private boolean isSelected;
        private Timestamp createdAt;

        public DetailDTO(Bid bid) {
            this.id = bid.getId();
            this.projectId = bid.getProject().getId();
            this.projectTitle = bid.getProject().getTitle();
            this.expertId = bid.getExpert().getId();
            this.expertName = bid.getExpert().getName();
            this.coverLetter = bid.getCoverLetter();
            this.approach = bid.getApproach();
            this.estimatedDays = bid.getEstimatedDays();
            this.proposedPrice = bid.getProposedPrice();
            this.status = bid.getStatus().name();
            this.isSelected = bid.getStatus() == BidStatus.SELECTED;
            this.createdAt = bid.getCreatedAt();

        }
    }

}
