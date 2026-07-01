package com.example.SevMerge.partnership;

import com.example.SevMerge.core.exception.BadRequestException;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class PartnerShipRequest {

    private String companyName;
    private String managerName;
    private String email;
    private MultipartFile partnerFile;
    private String content;
    private PartnerShipType partnerShipType;

}
