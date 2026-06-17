package com.example.SevMerge.partnership;


import com.example.SevMerge.portfolio.utile.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PartnerShipService {

    private final PartnerShipRepository partnerShipRepository;

    // 제휴문의 가 발생하면 일단 DB에 보관 과 동시에 메일 발송
    // 제휴문의는 비로그인 이라도 처리가능
    @Transactional
    public void save(PartnerShipRequest request){
        try {
            String fileUrl = FileUtil.partnerFileSave(request.getPartnerFile());

           PartnerShip savePartnerShip = PartnerShip
                    .builder()
                    .email(request.getEmail())
                    .managerName(request.getManagerName())
                    .companyName(request.getCompanyName())
                    .partnerFileUrl(fileUrl) // 파일 유틸에서 URL 만들어 꺼내기
                    .content(request.getContent())
                    .build();
            partnerShipRepository.save(savePartnerShip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





}
