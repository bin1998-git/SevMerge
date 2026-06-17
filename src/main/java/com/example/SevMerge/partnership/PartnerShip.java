package com.example.SevMerge.partnership;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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



}
