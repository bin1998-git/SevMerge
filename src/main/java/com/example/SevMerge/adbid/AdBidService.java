package com.example.SevMerge.adbid;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.notification.NotificationService;
import com.example.SevMerge.notification.NotificationType;
import com.example.SevMerge.revenue.PlatformRevenueService;
import com.example.SevMerge.revenue.PlatformRevenueType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdBidService {

    private final AdSlotRepository adSlotRepository;
    private final AdBidRepository adBidRepository;
    private final MemberRepository memberRepository;
    private final PlatformRevenueService platformRevenueService;
    private final NotificationService notificationService;

    @Value("${ad.bid.duration.minutes:2880}")
    private int bidDurationMinutes;

    @Value("${ad.display.duration.minutes:1440}")
    private int displayDurationMinutes;

    //  전문가: 입찰
    @Transactional
    public AdBid placeBid(Long slotId, Long expertId, Integer bidPrice,
                          String adMessage, String bannerImage) {
        AdSlot slot = adSlotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("슬롯을 찾을 수 없습니다."));

        if (!slot.isBidding()) {
            throw new BadRequestException("현재 입찰 가능한 슬롯이 아닙니다.");
        }
        if (bidPrice < slot.getMinBidPrice()) {
            throw new BadRequestException("최소 입찰가(" + slot.getMinBidPrice() + "P) 이상 입찰해야 합니다.");
        }

        Member expert = memberRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("전문가를 찾을 수 없습니다."));

        // 기존 입찰 있으면 환불 후 재입찰
        adBidRepository.findBySlotIdAndExpertId(slotId, expertId).ifPresent(existing -> {
            if (existing.getStatus() == AdBidStatus.PENDING) {
                expert.addBalance(existing.getBidPrice());
                existing.refund();
                log.info("[AdBid] 기존 입찰 환불 - expertId={}, amount={}", expertId, existing.getBidPrice());
            }
        });

        if (expert.getBalance() < bidPrice) {
            throw new BadRequestException("포인트가 부족합니다. (필요: " + bidPrice + "P)");
        }
        expert.deductBalance(bidPrice);

        AdBid bid = AdBid.builder()
                .slotId(slotId)
                .expertId(expertId)
                .bidPrice(bidPrice)
                .adMessage(adMessage)
                .bannerImage(bannerImage)
                .status(AdBidStatus.PENDING)
                .reviewStatus(AdBidReviewStatus.NONE)
                .build();
        adBidRepository.save(bid);
        log.info("[AdBid] 입찰 완료 - slotId={}, expertId={}, price={}", slotId, expertId, bidPrice);
        return bid;
    }

    //  스케줄러: 마감된 슬롯 자동 낙찰 (1분마다)
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void processExpiredSlots() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<AdSlot> expiredSlots = adSlotRepository.findExpiredOpenSlots(now);

        for (AdSlot slot : expiredSlots) {
            List<AdBid> bids = adBidRepository.findBySlotIdOrderByBidPriceDesc(slot.getId());

            if (bids.isEmpty()) {
                // 입찰자 없으면 즉시 다음 경매 시작
                slot.reopen(
                        new Timestamp(now.getTime()),
                        new Timestamp(now.getTime() + (long) bidDurationMinutes * 60 * 1000)
                );
                log.info("[AdBid] 입찰자 없음 - 다음 경매 즉시 시작 - slotId={}", slot.getId());
                continue;
            }

            // 최고가 낙찰
            AdBid winner = bids.get(0);
            winner.win();

            Timestamp displayStart = now;
            Timestamp displayEnd = new Timestamp(now.getTime() + (long) displayDurationMinutes * 60 * 1000);
            slot.close(winner.getExpertId(), winner.getBidPrice(), displayStart, displayEnd);

            // 수익 적재
            Member winnerMember = memberRepository.findById(winner.getExpertId()).orElse(null);
            String winnerName = winnerMember != null ? winnerMember.getName() : "알 수 없음";
            platformRevenueService.record(
                    PlatformRevenueType.AD,
                    winner.getBidPrice(),
                    slot.getId(),
                    winner.getExpertId(),
                    "경매 광고 수익 - " + winnerName
            );

            // 낙찰 알림
            if (winnerMember != null) {
                notificationService.notify(
                        winnerMember,
                        NotificationType.AD_REJECTED,
                        "🎉 [" + slot.getSlotName() + "] 경매에 낙찰되었습니다! " + winner.getBidPrice() + "P",
                        "/ad-auction/my-bids"
                );
            }

            // 나머지 환불
            for (int i = 1; i < bids.size(); i++) {
                AdBid loser = bids.get(i);
                loser.lose();
                Member loserMember = memberRepository.findById(loser.getExpertId()).orElse(null);
                if (loserMember != null) {
                    loserMember.addBalance(loser.getBidPrice());
                    log.info("[AdBid] 탈락 환불 - expertId={}, amount={}", loser.getExpertId(), loser.getBidPrice());
                }
            }
            log.info("[AdBid] 낙찰 완료 - slotId={}, winner={}, price={}",
                    slot.getId(), winner.getExpertId(), winner.getBidPrice());
        }
    }

    //  스케줄러: 노출 종료 -> 다음 경매 자동 시작 (1분마다)
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void processExpiredDisplays() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        List<AdSlot> expired = adSlotRepository.findExpiredDisplaying(now);

        for (AdSlot slot : expired) {
            slot.reopen(
                    new Timestamp(now.getTime()),
                    new Timestamp(now.getTime() + (long) bidDurationMinutes * 60 * 1000)
            );
            log.info("[AdBid] 노출 종료 - 다음 경매 자동 시작 - slotId={}", slot.getId());
        }
    }

    // 관리자: 슬롯 설정 수정
    @Transactional
    public void updateSlotSettings(Long slotId, Integer minBidPrice, Integer durationMinutes) {
        AdSlot slot = adSlotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("슬롯을 찾을 수 없습니다."));
        if (slot.getStatus() != AdSlotStatus.OPEN) {
            throw new BadRequestException("입찰 진행중인 슬롯만 설정 변경이 가능합니다.");
        }
        slot.updateSettings(minBidPrice, durationMinutes);
    }

    // 관리자: 낙찰 배너 승인
    @Transactional
    public void approveBidReview(Long bidId) {
        AdBid bid = adBidRepository.findById(bidId)
                .orElseThrow(() -> new NotFoundException("입찰을 찾을 수 없습니다."));
        if (bid.getStatus() != AdBidStatus.WINNER) {
            throw new BadRequestException("낙찰된 입찰만 승인할 수 있습니다.");
        }
        bid.approveReview();

        // 낙찰자에게 알림
        memberRepository.findById(bid.getExpertId()).ifPresent(member -> {
            notificationService.notify(member, NotificationType.AD_REJECTED,
                    " 경매 낙찰 광고가 승인되었습니다! 메인화면에 노출됩니다.",
                    "/ad-auction/my-bids");
        });
        log.info("[AdBid] 배너 승인 - bidId={}", bidId);
    }

    // 관리자: 낙찰 배너 거절
    @Transactional
    public void rejectBidReview(Long bidId, String reason) {
        AdBid bid = adBidRepository.findById(bidId)
                .orElseThrow(() -> new NotFoundException("입찰을 찾을 수 없습니다."));
        if (bid.getStatus() != AdBidStatus.WINNER) {
            throw new BadRequestException("낙찰된 입찰만 거절할 수 있습니다.");
        }
        bid.rejectReview();

        // 포인트 환불
        memberRepository.findById(bid.getExpertId()).ifPresent(member -> {
            member.addBalance(bid.getBidPrice());
            notificationService.notify(member, NotificationType.AD_REJECTED,
                    " 경매 낙찰 광고가 거절되었습니다. 사유: " + reason + " 포인트가 환불되었습니다.",
                    "/ad-auction/my-bids");
            log.info("[AdBid] 배너 거절 환불 - expertId={}, amount={}", member.getId(), bid.getBidPrice());
        });
        log.info("[AdBid] 배너 거절 - bidId={}", bidId);
    }

    @Transactional
    public void submitBanner(Long bidId, Long expertId, String bannerImage, String adMessage) {
        AdBid bid = adBidRepository.findById(bidId)
                .orElseThrow(() -> new NotFoundException("입찰을 찾을 수 없습니다."));
        if (!bid.getExpertId().equals(expertId)) {
            throw new BadRequestException("본인 입찰만 수정할 수 있습니다.");
        }
        if (bid.getStatus() != AdBidStatus.WINNER) {
            throw new BadRequestException("낙찰된 입찰만 배너를 제출할 수 있습니다.");
        }
        bid.submitBanner(bannerImage, adMessage);
        log.info("[AdBid] 배너 제출 - bidId={}, expertId={}", bidId, expertId);
    }

    // 승인된 낙찰 광고 조회 (메인화면용)
    public List<AdBid> getApprovedWinnerBids() {
        return adBidRepository.findApprovedWinners();
    }

    //  조회
    public List<AdSlot> getAllSlots() {
        return adSlotRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AdBid> getSlotBids(Long slotId) {
        return adBidRepository.findBySlotIdOrderByBidPriceDesc(slotId);
    }

    public List<AdBid> getMyBids(Long expertId) {
        return adBidRepository.findByExpertIdOrderByCreatedAtDesc(expertId);
    }

    public List<AdSlot> getCurrentlyDisplaying() {
        return adSlotRepository.findCurrentlyDisplaying(new Timestamp(System.currentTimeMillis()));
    }

    public AdSlot getSlot(Long slotId) {
        return adSlotRepository.findById(slotId)
                .orElseThrow(() -> new NotFoundException("슬롯을 찾을 수 없습니다."));
    }

    // 배너 검토 대기 목록
    public List<AdBid> getPendingReviewBids() {
        return adBidRepository.findByReviewStatus(AdBidReviewStatus.PENDING);
    }

    public List<AdBid> getProcessedReviewBids() {
        return adBidRepository.findByReviewStatusIn(
                List.of(AdBidReviewStatus.APPROVED, AdBidReviewStatus.REJECTED)
        );
    }
}