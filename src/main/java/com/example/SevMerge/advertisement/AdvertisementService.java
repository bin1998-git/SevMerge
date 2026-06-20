package com.example.SevMerge.advertisement;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * AdvertisementService — 전문가 광고 슬롯 구매/노출
 *
 * [역할]
 * - 전문가가 포인트로 광고 슬롯 구매 (정액제, 기간제)
 * - exmain 등 화면에 노출할 활성 광고 조회 (랜덤 셔플)
 * - 기간 만료 처리
 *
 * [충돌 방지 전략]
 * - Member.deductBalance()는 기존 메서드 재사용 (잔액 차감 로직 중복 없음)
 * - Member, ExpertProfile은 ID로만 참조, 표시용 정보만 조회해서 DTO에 채움
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final MemberRepository memberRepository;
    private final ExpertProfileRepository expertProfileRepository;

    // ── 구매 ──

    @Transactional
    public AdvertisementResponse purchase(Long expertId, AdvertisementPlacement placement) {
        Member expert = memberRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("전문가 정보를 찾을 수 없습니다."));

        if (!expert.isExpert()) {
            throw new ForbiddenException("전문가만 광고를 구매할 수 있습니다.");
        }

        int price = placement.getPrice();

        try {
            expert.deductBalance(price);   // 기존 Member.deductBalance() 재사용
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
                .status(AdvertisementStatus.ACTIVE)
                .build();

        advertisementRepository.save(ad);
        log.info("[Advertisement] 구매 완료 - expertId={}, placement={}, price={}", expertId, placement, price);

        return toResponse(ad);
    }

    // ── 노출용 조회 ──

    public List<AdvertisementResponse> getActiveAds(AdvertisementPlacement placement) {
        List<Advertisement> ads = advertisementRepository
                .findActiveByPlacement(placement, new Timestamp(System.currentTimeMillis()));

        List<AdvertisementResponse> responses = ads.stream().map(this::toResponse).toList();

        // 같은 슬롯을 산 광고들끼리 랜덤 순서로 섞어서 노출 (크몽 방식)
        List<AdvertisementResponse> shuffled = new java.util.ArrayList<>(responses);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    // ── 전문가 본인 광고 내역 ──

    public List<AdvertisementResponse> getMyAds(Long expertId) {
        return advertisementRepository.findByExpertIdOrderByCreatedAtDesc(expertId)
                .stream().map(this::toResponse).toList();
    }

    // ── 만료 처리 (스케줄러 또는 조회 시점에 호출) ──

    @Transactional
    public void expireOutdatedAds() {
        List<Advertisement> expired = advertisementRepository
                .findExpiredButStillActive(new Timestamp(System.currentTimeMillis()));
        expired.forEach(Advertisement::expire);
        if (!expired.isEmpty()) {
            log.info("[Advertisement] 만료 처리 - {}건", expired.size());
        }
    }

    // ── private ──

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