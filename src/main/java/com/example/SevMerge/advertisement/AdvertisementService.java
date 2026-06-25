package com.example.SevMerge.advertisement;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.notification.NotificationService;
import com.example.SevMerge.revenue.PlatformRevenueService;
import com.example.SevMerge.revenue.PlatformRevenueType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 전문가 광고 슬롯 구매/노출
 *  전문가가 포인트로 광고 슬롯 구매 (정액제, 기간제)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final MemberRepository memberRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final PlatformRevenueService platformRevenueService;
    private final NotificationService notificationService;

    // 구매
    @Transactional
    public AdvertisementResponse purchase(Long expertId, AdvertisementPlacement placement,
                                          String customMessage, String bannerImage) {
        Member expert = memberRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("전문가 정보를 찾을 수 없습니다."));

        if (!expert.isExpert()) {
            throw new ForbiddenException("전문가만 광고를 구매할 수 있습니다.");
        }

        // 중복 신청 방지
        boolean hasActiveOrPending = advertisementRepository
                .existsByExpertIdAndPlacementAndStatusIn(
                        expertId, placement,
                        List.of(AdvertisementStatus.ACTIVE, AdvertisementStatus.PENDING)
                );
        if (hasActiveOrPending) {
            throw new BadRequestException("이미 진행 중이거나 승인 대기 중인 광고가 있습니다. 종료 후 다시 신청해 주세요.");
        }

        int price = placement.getPrice();
        try {
            expert.deductBalance(price);
        } catch (IllegalStateException e) {
            throw new BadRequestException("잔액이 부족합니다. (필요 금액: " + price + "P)");
        }

        long now = System.currentTimeMillis();
        Timestamp startDate = new Timestamp(now);
        Timestamp endDate = new Timestamp(now + placement.getDurationDays() * 24L * 60 * 60 * 1000);

        Advertisement ad = Advertisement.builder()
                .expertId(expertId)
                .placement(placement)
                .price(price)
                .startDate(startDate)
                .endDate(endDate)
                .status(AdvertisementStatus.PENDING)
                .customMessage(customMessage)
                .bannerImage(bannerImage)
                .build();

        advertisementRepository.save(ad);
        log.info("[Advertisement] 신청 완료(승인 대기) - expertId={}, placement={}, price={}", expertId, placement, price);
        return toResponse(ad);
    }

    // 노출용 조회
    public List<AdvertisementResponse> getActiveAds(AdvertisementPlacement placement) {
        List<Advertisement> ads = advertisementRepository
                .findActiveByPlacement(placement, new Timestamp(System.currentTimeMillis()));

        List<AdvertisementResponse> responses = ads.stream().map(this::toResponse).toList();

        List<AdvertisementResponse> shuffled = new java.util.ArrayList<>(responses);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    //  전문가 본인 광고 내역
    public List<AdvertisementResponse> getMyAds(Long expertId) {
        return advertisementRepository.findByExpertIdOrderByCreatedAtDesc(expertId)
                .stream().map(this::toResponse).toList();
    }

    //  만료 처리 (스케줄러 또는 조회 시점에 호출)
    @Transactional
    public void expireOutdatedAds() {
        List<Advertisement> expired = advertisementRepository
                .findExpiredButStillActive(new Timestamp(System.currentTimeMillis()));
        expired.forEach(Advertisement::expire);
        if (!expired.isEmpty()) {
            log.info("[Advertisement] 만료 처리 - {}건", expired.size());
        }
    }
    // 승인 대기 광고 목록 조회 (관리자용)
    public List<AdvertisementResponse> getPendingAds() {
        return advertisementRepository.findPendingAds()
                .stream().map(this::toResponse).toList();
    }

    // 광고 승인 시점부터 7일
    @Transactional
    public void approveAd(Long adId) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("광고를 찾을 수 없습니다."));
        if (ad.getStatus() != AdvertisementStatus.PENDING) {
            throw new BadRequestException("대기 중인 광고만 승인할 수 있습니다.");
        }
        ad.approve();

        // 전문가 이름 조회
        Member expert = memberRepository.findById(ad.getExpertId())
                .orElseThrow(() -> new NotFoundException("전문가 정보를 찾을 수 없습니다."));

        // 수익 적재
        platformRevenueService.record(
                PlatformRevenueType.AD,
                ad.getPrice(),
                ad.getId(),
                ad.getExpertId(),
                ad.getPlacement().getLabel() + " 광고 수익 - expertId=" + expert.getName()
        );
        log.info("[Advertisement] 승인 완료 - adId={}", adId);
    }

    // 광고 거절
    @Transactional
    public void rejectAd(Long adId, String reason) {
        Advertisement ad = advertisementRepository.findById(adId)
                .orElseThrow(() -> new NotFoundException("광고를 찾을 수 없습니다."));
        if (ad.getStatus() != AdvertisementStatus.PENDING) {
            throw new BadRequestException("대기 중인 광고만 거절할 수 있습니다.");
        }

        // 포인트 환불
        Member expert = memberRepository.findById(ad.getExpertId())
                .orElseThrow(() -> new NotFoundException("전문가 정보를 찾을 수 없습니다."));
        expert.addBalance(ad.getPrice());
        log.info("[Advertisement] 포인트 환불 - expertId={}, amount={}", ad.getExpertId(), ad.getPrice());

        // 수익 취소 (승인 후 거절 케이스 대비)
        platformRevenueService.cancel(adId, PlatformRevenueType.AD);

        ad.reject(reason);
        log.info("[Advertisement] 거절 완료 - adId={}", adId);

        // 알림 발송
        notificationService.notifyAdRejected(expert, ad.getPlacement().getLabel(), reason);
    }

    public List<AdvertisementResponse> getProcessedAds() {
        return advertisementRepository.findProcessedAds()
                .stream().map(this::toResponse).toList();
    }

    public Long getTotalRevenue() {
        return advertisementRepository.findTotalRevenue();
    }

    public Long getThisMonthRevenue() {
        return advertisementRepository.findThisMonthRevenue();
    }

    public List<Integer> getRevenueTrendByPeriod(LocalDate startDate, LocalDate endDate) {
        Timestamp start = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp end = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        List<Object[]> rawData = advertisementRepository.findRevenueByPeriod(start, end);
        Map<String, Integer> dateMap = new HashMap<>();
        for (Object[] row : rawData) {
            dateMap.put((String) row[0], ((Number) row[1]).intValue());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        List<Integer> result = new ArrayList<>();
        for (long i = 0; i <= ChronoUnit.DAYS.between(startDate, endDate); i++) {
            result.add(dateMap.getOrDefault(startDate.plusDays(i).format(formatter), 0));
        }
        return result;
    }
    // private
    private AdvertisementResponse toResponse(Advertisement ad) {
        Member expert = memberRepository.findById(ad.getExpertId()).orElse(null);
        String expertName = expert != null ? expert.getName() : "알 수 없음";
        String profileImage = expert != null ? expert.getProfileImage() : null;

        String speciality = expertProfileRepository.findByMemberId(ad.getExpertId())
                .map(ExpertProfile::getSpeciality)
                .orElse("");

        return new AdvertisementResponse(ad, expertName, speciality, profileImage);
    }
}