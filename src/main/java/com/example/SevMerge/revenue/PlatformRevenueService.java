package com.example.SevMerge.revenue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlatformRevenueService {

    private final PlatformRevenueRepository platformRevenueRepository;

    // 수익 적재
    @Transactional
    public PlatformRevenue record(PlatformRevenueType type, Integer amount,
                                  Long referenceId, Long targetId, String description) {
        PlatformRevenue revenue = PlatformRevenue.builder()
                .type(type)
                .amount(amount)
                .referenceId(referenceId)
                .targetId(targetId)
                .description(description)
                .status(PlatformRevenueStatus.CONFIRMED)
                .build();
        platformRevenueRepository.save(revenue);
        log.info("[Revenue] 수익 적재 - type={}, amount={}, referenceId={}", type, amount, referenceId);
        return revenue;
    }

    // 수익 취소 (광고 거절 등)
    @Transactional
    public void cancel(Long referenceId, PlatformRevenueType type) {
        platformRevenueRepository.findByReferenceIdAndType(referenceId, type)
                .ifPresent(r -> {
                    r.cancel();
                    log.info("[Revenue] 수익 취소 - referenceId={}, type={}", referenceId, type);
                });
    }

    // 전체 확정 수익
    public Long getTotalRevenue() {
        return platformRevenueRepository.findTotalConfirmedRevenue();
    }

    // 타입별 수익
    public Long getRevenueByType(PlatformRevenueType type) {
        return platformRevenueRepository.findTotalByType(type);
    }

    // 이번달 수익
    public Long getThisMonthRevenue() {
        return platformRevenueRepository.findThisMonthRevenue();
    }

    // 전체 수익 목록
    public List<PlatformRevenue> getAllRevenues() {
        return platformRevenueRepository.findAllByOrderByCreatedAtDesc();
    }

    // 기간별 수익
    public List<PlatformRevenue> getRevenuesByPeriod(Timestamp start, Timestamp end) {
        return platformRevenueRepository.findByPeriod(start, end);
    }
}