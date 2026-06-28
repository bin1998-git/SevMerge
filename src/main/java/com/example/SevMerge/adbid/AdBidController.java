package com.example.SevMerge.adbid;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.core.util.FileUtil;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdBidController {

    private final AdBidService adBidService;
    private final MemberRepository memberRepository;


    //  전문가: 경매 목록 페이지
    @GetMapping("/ad-auction")
    public String auctionPage(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return "redirect:/login";
        if (!loginMember.isExpert()) return "redirect:/";

        Member freshMember = memberRepository.findById(loginMember.getId())
                .orElse(loginMember);

        List<AdSlot> slots = adBidService.getAllSlots().stream()
                .filter(s -> s.getStatus() == AdSlotStatus.OPEN)
                .toList();

        model.addAttribute("slots", slots.stream().map(s -> {
            List<AdBid> bids = adBidService.getSlotBids(s.getId());
            int topPrice = bids.isEmpty() ? s.getMinBidPrice() : bids.get(0).getBidPrice();
            boolean myBid = bids.stream().anyMatch(b -> b.getExpertId().equals(loginMember.getId())
                    && b.getStatus() == AdBidStatus.PENDING);

            int myBidPrice = bids.stream()
                    .filter(b -> b.getExpertId().equals(loginMember.getId())
                            && b.getStatus() == AdBidStatus.PENDING)
                    .mapToInt(AdBid::getBidPrice)
                    .findFirst()
                    .orElse(0);

            int myRank = 0;
            for (int i = 0; i < bids.size(); i++) {
                if (bids.get(i).getExpertId().equals(loginMember.getId())
                        && bids.get(i).getStatus() == AdBidStatus.PENDING) {
                    myRank = i + 1;
                    break;
                }
            }

            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", s.getId());
            map.put("slotName", s.getSlotName());
            map.put("minBidPrice", s.getMinBidPrice());
            map.put("topPrice", topPrice);
            map.put("bidCount", bids.size());
            map.put("bidEndAt", s.getBidEndAt().toString().substring(0, 19));
            map.put("isBidding", s.isBidding());
            map.put("myBid", myBid);
            map.put("myBidPrice", myBidPrice);
            map.put("myRank", myRank);
            map.put("isFirst", myRank == 1);
            return map;
        }).toList());

        model.addAttribute("balance", freshMember.getBalance());
        return "adbid/ad-auction";
    }

    //  전문가: 내 입찰 내역
    @GetMapping("/ad-auction/my-bids")
    public String myBids(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return "redirect:/login";
        if (!loginMember.isExpert()) return "redirect:/";

        List<AdBid> bids = adBidService.getMyBids(loginMember.getId());
        model.addAttribute("bids", bids.stream().map(b -> {
            AdSlot slot = adBidService.getSlot(b.getSlotId());
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("bidId", b.getId());
            map.put("slotName", slot.getSlotName());
            map.put("bidPrice", b.getBidPrice());
            map.put("isPending", b.getStatus() == AdBidStatus.PENDING);
            map.put("isWinner", b.getStatus() == AdBidStatus.WINNER);
            map.put("isLost", b.getStatus() == AdBidStatus.LOST);
            map.put("isRefunded", b.getStatus() == AdBidStatus.REFUNDED);
            map.put("createdAt", b.getCreatedAt().toString().substring(0, 16));
            // 배너 관련
            map.put("needsBanner", b.getStatus() == AdBidStatus.WINNER
                    && b.getReviewStatus() == AdBidReviewStatus.NONE);
            map.put("isApproved", b.getReviewStatus() == AdBidReviewStatus.APPROVED);
            map.put("isPendingReview", b.getReviewStatus() == AdBidReviewStatus.PENDING);
            map.put("isRejectedReview", b.getReviewStatus() == AdBidReviewStatus.REJECTED);
            return map;
        }).toList());
        return "adbid/my-bids";
    }

    //  전문가: 입찰 API
    @ResponseBody
    @PostMapping("/api/ad-auction/{slotId}/bid")
    public ResponseEntity<?> placeBid(@PathVariable Long slotId,
                                      @RequestParam("bidPrice") Integer bidPrice,
                                      HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");
        if (!loginMember.isExpert()) return ResponseEntity.status(403).body("전문가만 입찰할 수 있습니다.");

        try {
            adBidService.placeBid(slotId, loginMember.getId(), bidPrice, null, null);
            return ResponseEntity.ok("입찰 완료!");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  전문가: 낙찰 후 배너 이미지 제출
    @ResponseBody
    @PostMapping("/api/ad-bids/{bidId}/banner")
    public ResponseEntity<?> submitBanner(@PathVariable Long bidId,
                                          @RequestParam("bannerImageFile") MultipartFile bannerImageFile,
                                          @RequestParam(value = "adMessage", required = false) String adMessage,
                                          HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return ResponseEntity.status(401).body("로그인이 필요합니다.");

        try {
            if (!FileUtil.isImageFile(bannerImageFile)) {
                return ResponseEntity.badRequest().body("이미지 파일만 업로드할 수 있습니다.");
            }
            String savedImage = FileUtil.saveFile(bannerImageFile, FileUtil.IMAGES_DIR);
            adBidService.submitBanner(bidId, loginMember.getId(), savedImage, adMessage);
            return ResponseEntity.ok("배너가 제출되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  관리자: 슬롯 목록 페이지
    @GetMapping("/admin/ad-slots")
    public String adminSlots(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null || !loginMember.isAdmin()) return "redirect:/login";

        model.addAttribute("slots", adBidService.getAllSlots().stream().map(s -> {
            List<AdBid> bids = adBidService.getSlotBids(s.getId());
            int topPrice = bids.isEmpty() ? 0 : bids.get(0).getBidPrice();

            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", s.getId());
            map.put("slotName", s.getSlotName());
            map.put("minBidPrice", s.getMinBidPrice());
            map.put("topPrice", topPrice);
            map.put("bidCount", bids.size());
            map.put("bidEndAt", s.getBidEndAt().toString().substring(0, 19));
            map.put("isOpen", s.getStatus() == AdSlotStatus.OPEN);
            map.put("isClosed", s.getStatus() == AdSlotStatus.CLOSED);
            map.put("isAwarded", s.getStatus() == AdSlotStatus.AWARDED);
            map.put("isExpired", s.getStatus() == AdSlotStatus.EXPIRED);
            map.put("winnerExpertId", s.getWinnerExpertId() != null ? s.getWinnerExpertId() : "");
            map.put("winningPrice", s.getWinningPrice() != null ? s.getWinningPrice() : 0);
            return map;
        }).toList());
        return "admin/admin-ad-slots";
    }

    //  관리자: 슬롯 설정 수정 API
    @ResponseBody
    @PatchMapping("/api/admin/ad-slots/{slotId}/settings")
    public ResponseEntity<?> updateSlotSettings(@PathVariable Long slotId,
                                                @RequestBody Map<String, Object> body,
                                                HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null || !loginMember.isAdmin()) {
            return ResponseEntity.status(403).body("관리자만 접근 가능합니다.");
        }
        try {
            Integer minBidPrice = (Integer) body.get("minBidPrice");
            Integer durationMinutes = (Integer) body.get("durationMinutes");
            adBidService.updateSlotSettings(slotId, minBidPrice, durationMinutes);
            return ResponseEntity.ok(Map.of("message", "설정이 저장되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  관리자: 슬롯 입찰자 목록 API
    @ResponseBody
    @GetMapping("/api/admin/ad-slots/{slotId}/bids")
    public ResponseEntity<?> getSlotBids(@PathVariable Long slotId, HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null || !loginMember.isAdmin()) {
            return ResponseEntity.status(403).body("관리자만 접근 가능합니다.");
        }
        List<AdBid> bids = adBidService.getSlotBids(slotId);
        List<Map<String, Object>> result = bids.stream().map(b -> {
            Member m = memberRepository.findById(b.getExpertId()).orElse(null);
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", b.getId());
            map.put("expertId", b.getExpertId());
            map.put("expertName", m != null ? m.getName() : "알 수 없음");
            map.put("bidPrice", b.getBidPrice());
            map.put("status", b.getStatus().name());
            map.put("createdAt", b.getCreatedAt().toString().substring(0, 16));
            map.put("reviewStatus", b.getReviewStatus().name());
            map.put("bannerImage", b.getBannerImage() != null ? b.getBannerImage() : "");
            map.put("adMessage", b.getAdMessage() != null ? b.getAdMessage() : "");
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }


    //  관리자: 낙찰 배너 승인
    @ResponseBody
    @PatchMapping("/api/admin/ad-bids/{bidId}/approve")
    public ResponseEntity<?> approveBidReview(@PathVariable Long bidId,
                                              HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null || !loginMember.isAdmin()) {
            return ResponseEntity.status(403).body("관리자만 접근 가능합니다.");
        }
        try {
            adBidService.approveBidReview(bidId);
            return ResponseEntity.ok(Map.of("message", "승인 완료!"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // 관리자: 낙찰 배너 거절
    @ResponseBody
    @PatchMapping("/api/admin/ad-bids/{bidId}/reject")
    public ResponseEntity<?> rejectBidReview(@PathVariable Long bidId,
                                             @RequestBody Map<String, String> body,
                                             HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null || !loginMember.isAdmin()) {
            return ResponseEntity.status(403).body("관리자만 접근 가능합니다.");
        }
        try {
            String reason = body.getOrDefault("reason", "");
            adBidService.rejectBidReview(bidId, reason);
            return ResponseEntity.ok(Map.of("message", "거절 완료!"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}