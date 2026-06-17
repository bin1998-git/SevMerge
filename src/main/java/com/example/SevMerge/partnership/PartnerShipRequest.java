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



    public void validate(){
        if (companyName == null || companyName.trim().isEmpty()){

            throw new BadRequestException("회사명을 입력해 주세요");
        }
        if (managerName == null || managerName.trim().isEmpty()){

            throw new BadRequestException("담당자 성함을 입력해 주세요");
        }
        if (email == null || email.trim().isEmpty()){

            throw new BadRequestException("이메일을 입력해 주세요");
        }
        if (partnerFile == null || partnerFile.isEmpty()){
            throw new BadRequestException("파일을 넣어 주세요");
        }
        if (content == null || content.trim().isEmpty()){
            throw new BadRequestException("제휴 내용을 입력해 주세요");
        }
    }
}
