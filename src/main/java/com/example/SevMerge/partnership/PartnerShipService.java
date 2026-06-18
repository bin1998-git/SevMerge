package com.example.SevMerge.partnership;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.portfolio.utile.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerShipService {

    private final PartnerShipRepository partnerShipRepository;
    private final PartnerShipMailService partnerShipMailService;

    public void save(PartnerShipRequest request) {
        // 스크립트에서 validate 처리
        try {
            partnerShipMailService.sendPartnerShipMail(request);

            PartnerShip partnerShip = PartnerShip
                    .builder()
                    .companyName(request.getCompanyName())
                    .managerName(request.getManagerName())
                    .email(request.getEmail())
                    .partnerFileUrl(FileUtil.partnerFileSave(request.getPartnerFile()))
                    .content(request.getContent())
                    .status(PartnerShipStatus.PENDING)
                    .build();
            partnerShipRepository.save(partnerShip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 제휴목록
    public List<PartnerShipResponse> list() {
        List<PartnerShip> partnerShipList = partnerShipRepository.findAll();
        return partnerShipList.stream().map(PartnerShipResponse::new).toList();
    }

    // 승인
    @Transactional
    public void findByIdAndApprove(Long id) {
        PartnerShip partnerShipEntity = partnerShipRepository.findById(id).orElseThrow(() ->
                new BadRequestException("해당하는 제휴가 없습니다.")
        );
        partnerShipMailService.sendPartnerShipMailApprove(partnerShipEntity.getEmail());

        partnerShipEntity.setStatus(PartnerShipStatus.APPROVED);
    }

    @Transactional
    public void findByIdAndReject(Long id) {
        PartnerShip partnerShipEntity = partnerShipRepository.findById(id).orElseThrow(() ->
                new BadRequestException("해당하는 제휴가 없습니다.")
        );
        partnerShipMailService.sendPartnerShipMailReject(partnerShipEntity.getEmail());
        partnerShipEntity.setStatus(PartnerShipStatus.REJECTED);
        partnerShipEntity.deleteAt();
        // 스케줄러 표시된 메서드는 알아서 스프링부트가 설정된 시간마다 메서드를 불러오기에 따로 호출 안해도 된다.
//        deleteRejected();
    }

//    @Scheduled(fixedDelay = 1000)
//    @Transactional
//    public void deleteRejected() {
//        Timestamp now = new Timestamp(System.currentTimeMillis());
//        partnerShipRepository.deletedAtByTime(now);
//    }

}
