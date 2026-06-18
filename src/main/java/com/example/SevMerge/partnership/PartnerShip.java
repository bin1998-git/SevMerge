package com.example.SevMerge.partnership;


import com.example.SevMerge.member.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;

@Table(name = "partner_ship_tb")
@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class PartnerShip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String companyName;
    @Column(nullable = false, length = 100)
    private String managerName;
    @Column(nullable = false, length = 100)
    private String email;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String partnerFileUrl; // 제휴문의 파일 경로
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerShipStatus status;
    @CreationTimestamp
    private Timestamp createdAt;

    private Timestamp deletedAt;


    // 승인
    public void isApproved(){
        status = PartnerShipStatus.APPROVED;
    }

    // 거절
    public void isRejected(){
        status = PartnerShipStatus.REJECTED;
    }

    // 대기 (혹시몰라서 만듬)
    public void isPending(){
        status = PartnerShipStatus.PENDING;
    }



    public void deleteAt() {

        final Long DELETE_TIME = 10000L;
        deletedAt = new Timestamp(System.currentTimeMillis() + DELETE_TIME); // 거절후 삭제되기까지 시간
    }
}
