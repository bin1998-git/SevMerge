package com.example.SevMerge.charge;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ChargeResponse {

    private Long id;
    private Integer amount;
    private String orderId;
    private String status;
    private Timestamp createdAt;

    // Mustache boolean
    private boolean isDone;
    private boolean isFailed;

    public static ChargeResponse from(Charge charge) {
        String s = charge.getStatus().name();
        return ChargeResponse.builder()
                .id(charge.getId())
                .amount(charge.getAmount())
                .orderId(charge.getOrderId())
                .status(s)
                .createdAt(charge.getCreatedAt())
                .isDone("DONE".equals(s))
                .isFailed("FAILED".equals(s))
                .build();
    }

    public String getDisplayAmount() {
        return amount == null ? "0" : String.format("%,d", amount);
    }
}
