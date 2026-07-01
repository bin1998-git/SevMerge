    package com.example.SevMerge.bid;

    import com.example.SevMerge.member.Member;
    import com.example.SevMerge.project.Project;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import org.hibernate.annotations.CreationTimestamp;

    import java.sql.Timestamp;

    @Entity
    @Table(name = "bid_tb")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Bid {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "project_id", nullable = false)
        private Project project;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "expert_id", nullable = false)
        private Member expert; // 입찰한 전문가

        @Column(nullable = false, columnDefinition = "TEXT")
        private String coverLetter; // 자기소개


        @Column(columnDefinition = "TEXT")
        private String approach; // 작업 접근 방식

        @Column(nullable = false)
        private Long estimatedDays; // 예상 작업 기간

        @Column(nullable = false)
        private Long proposedPrice; // 희망금액

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private BidStatus status;

        @Enumerated(EnumType.STRING)
        @Column(name = "work_status")
        @Builder.Default
        private WorkStatus workStatus = WorkStatus.IN_PROGRESS;

        // 작업물 제출 정보
        @Column(name = "submitted_file_url", length = 500)
        private String submittedFileUrl;

        @Column(name = "submitted_file_name", length = 255)
        private String submittedFileName;

        @Column(name = "submitted_note", columnDefinition = "TEXT")
        private String submittedNote;

        @Column(name = "submitted_at")
        private Timestamp submittedAt;

        @Column(nullable = false)
        @Builder.Default
        private boolean isDeleted = false;

        @CreationTimestamp
        @Column(nullable = false)
        private Timestamp createdAt;

        // DB에 저장되기 직전에 자동으로 실행되는 메서드
        @PrePersist
        public void prePersist() {
            if (this.status == null) this.status = BidStatus.PENDING;
        }

        public void update(BidRequestDTO.UpdateDTO req) {
            if (req.getCoverLetter() != null) this.coverLetter = req.getCoverLetter();
            if (req.getApproach() != null) this.approach = req.getApproach();
            if (req.getEstimatedDays() != null) this.estimatedDays = req.getEstimatedDays();
            if (req.getProposedPrice() != null) this.proposedPrice = req.getProposedPrice();
        }

        public void select() {
            this.status = BidStatus.SELECTED;
            this.workStatus = WorkStatus.IN_PROGRESS;
        }

        public void changeWorkStatus(WorkStatus ws) {
            this.workStatus = ws;
        }

        public void submitWork(String fileUrl, String fileName, String note) {
            this.submittedFileUrl = fileUrl;
            this.submittedFileName = fileName;
            this.submittedNote = note;
            this.submittedAt = new Timestamp(System.currentTimeMillis());
            this.workStatus = WorkStatus.WORK_DONE;
        }

        public void fail() {
            this.status = BidStatus.REJECTED;
        }
        // 소프트 딜리트 삭제 메서드

        public void delete() {
            this.isDeleted = true;
        }

        // 제안서 거절
        public void reject() {
            this.status = BidStatus.REJECTED;
        }

        // 제안서의 상태를 보류하는 메서드
        public void hold() {
            this.status = BidStatus.HOLD;
        }

        //  보류상태 분기처리 메서드
        //     return 현재 제안서가 보류(HOLD) 상태라면 true
        public boolean isHold() {
            return this.status == BidStatus.HOLD;
        }
    }
