package com.example.SevMerge.refund;

import com.example.SevMerge.core.util.MyDateUtil;
import lombok.Getter;

@Getter
public class RefundApplicationResponse {

    private final String reasonCategory;
    private Long id;
    private Long paymentId;
    private Long clientId;
    private String clientName;
    private Long expertId;
    private String expertName;
    private String reason;
    private String adminComment;
    private String status;
    private String createdAt;

    public RefundApplicationResponse(RefundApplication r, String clientName, String expertName) {
        this.id = r.getId();
        this.paymentId = r.getPaymentId();
        this.clientId = r.getClientId();
        this.clientName = clientName;
        this.expertId = r.getExpertId();
        this.expertName = expertName;
        this.reason = r.getReason();
        this.adminComment = r.getAdminComment();
        this.status = r.getStatus().name();
        this.createdAt = MyDateUtil.timestampFormat(r.getCreatedAt());
        this.reasonCategory = r.getReasonCategory();
    }

    public boolean isPending()  { return "PENDING".equals(status); }
    public boolean isApproved() { return "APPROVED".equals(status); }
    public boolean isRejected() { return "REJECTED".equals(status); }

    public String getReasonCategoryLabel() {
        if (reasonCategory == null) return "";
        return switch (reasonCategory) {
            case "WORK_DELAYED" -> "작업 지연";
            case "QUALITY_ISSUE" -> "품질 문제";
            case "COMMUNICATION_ISSUE" -> "소통 문제";
            case "REQUIREMENT_CHANGE" -> "고객 사정";
            case "DUPLICATE_PAYMENT" -> "결제 오류";
            default -> "기타";
        };
    }
}