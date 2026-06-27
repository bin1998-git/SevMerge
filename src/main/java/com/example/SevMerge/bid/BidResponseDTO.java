package com.example.SevMerge.bid;

import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.payment.Payment;
import com.example.SevMerge.project.ProjectStatus;
import lombok.*;

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
        private String avgRating;
        private boolean isPending;
        private boolean isSelected;
        private boolean isRejected;
        private boolean isHold;
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
            this.isHold = bid.getStatus() == BidStatus.HOLD;
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

    // ── 주문 관리 (전문가 시점, SELECTED 낙찰 건) ──────────────────────────────
    @Data
    @NoArgsConstructor
    public static class OrderDTO {
        private Long id;
        private Long projectId;
        private String projectTitle;
        private Long clientId;
        private String clientName;
        private Long proposedPrice;
        private Long estimatedDays;
        private String projectStatus;
        private boolean isInProgress;
        private boolean isCompleted;  // 검토 대기 중 (전문가 완료신고 후)
        private boolean isDone;
        private String workStatus;
        private boolean workIsInProgress;
        private boolean workIsUnderReview;
        private boolean workIsDone;
        private String submittedFileUrl;
        private String submittedFileName;
        private String submittedNote;
        private Timestamp createdAt;
        private PaymentInfo payment;
        private java.util.List<com.example.SevMerge.deliverable.DeliverableResponse.ListDTO> deliverables;

        public OrderDTO(Bid bid, Payment paymentEntity) {
            this.id = bid.getId();
            this.projectId = bid.getProject().getId();
            this.projectTitle = bid.getProject().getTitle();
            this.clientId = bid.getProject().getMember().getId();
            this.clientName = bid.getProject().getMember().getName();
            this.proposedPrice = bid.getProposedPrice();
            this.estimatedDays = bid.getEstimatedDays();
            ProjectStatus ps = bid.getProject().getProjectStatus();
            this.projectStatus = ps.name();
            this.isInProgress = ps == ProjectStatus.IN_PROGRESS;
            this.isCompleted = ps == ProjectStatus.COMPLETED;
            this.isDone = ps == ProjectStatus.DONE;
            WorkStatus ws = bid.getWorkStatus() != null ? bid.getWorkStatus() : WorkStatus.IN_PROGRESS;
            this.workStatus = ws.name();
            this.workIsInProgress = ws == WorkStatus.IN_PROGRESS;
            this.workIsUnderReview = ws == WorkStatus.UNDER_REVIEW;
            this.workIsDone = ws == WorkStatus.WORK_DONE;
            this.submittedFileUrl = bid.getSubmittedFileUrl();
            this.submittedFileName = bid.getSubmittedFileName();
            this.submittedNote = bid.getSubmittedNote();
            this.createdAt = bid.getCreatedAt();
            this.payment = paymentEntity != null ? new PaymentInfo(paymentEntity) : new PaymentInfo();
        }
    }

    // 결제 정보 (OrderDTO 내부용)
    @Data
    @NoArgsConstructor
    public static class PaymentInfo {
        private Integer amount;
        private Integer platformFee;
        private Integer netAmount;
        private boolean isPaid;
        private boolean isSettled;

        public PaymentInfo(Payment p) {
            this.amount = p.getAmount();
            this.platformFee = p.getPlatformFee();
            this.netAmount = p.getNetAmount();
            this.isPaid = p.getStatus().name().equals("PAID");
            this.isSettled = p.getStatus().name().equals("SETTLED");
        }
    }

}
