package com.example.SevMerge;

import com.example.SevMerge.adbid.AdBid;
import com.example.SevMerge.adbid.AdBidService;
import com.example.SevMerge.adbid.AdSlot;
import com.example.SevMerge.advertisement.AdvertisementPlacement;
import com.example.SevMerge.advertisement.AdvertisementResponse;
import com.example.SevMerge.advertisement.AdvertisementService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfileResponse;
import com.example.SevMerge.expertprofile.ExpertProfileService;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.SessionUser;
import com.example.SevMerge.project.ProjectResponseDTO;
import com.example.SevMerge.project.ProjectService;
import com.example.SevMerge.review.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class main {

    private final ProjectService projectService;
    private final AdvertisementService advertisementService;
    private final ExpertProfileService expertProfileService;
    private final ReviewService reviewService;
    private final AdBidService adBidService;
    private final MemberRepository memberRepository;

    @GetMapping("/")
    public String introPage(HttpSession session) {
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (loginMember != null) return "redirect:/main";
        return "intro";
    }

    @GetMapping("/main")
    public String exmainPage(HttpSession session, Model model) {
        SessionUser loginMember = (SessionUser) session.getAttribute(Define.SESSION_USER);

        model.addAttribute("isMainPage", true);

        if (loginMember != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("sessionUser", loginMember);
            model.addAttribute("isExpert", loginMember.isExpert());
            model.addAttribute("isClient", loginMember.isClient());
        } else {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("isExpert", false);
        }

        advertisementService.expireOutdatedAds();

        // 1. 메인 배너 광고 — 랜덤 로테이션 최대 3개
        List<AdvertisementResponse> mainAds =
                advertisementService.getActiveAds(AdvertisementPlacement.MAIN_BANNER);
        Collections.shuffle(mainAds);
        List<AdvertisementResponse> topAds = mainAds.stream()
                .limit(3)
                .map(ad -> {
                    Double avgObj = reviewService.avgRating(ad.getExpertId());
                    double avg = avgObj != null ? avgObj : 0.0;
                    ad.setAvgRating(avg > 0 ? String.format("%.1f", avg) : "-");
                    return ad;
                })
                .collect(Collectors.toList());
        model.addAttribute("adExperts", topAds);
        model.addAttribute("hasAdExperts", !topAds.isEmpty());

        // 2. 인피드 캐러셀 광고 — 랜덤 로테이션
        List<AdvertisementResponse> carouselAds =
                advertisementService.getActiveAds(AdvertisementPlacement.EXPERT_CAROUSEL);
        Collections.shuffle(carouselAds);

        // 3. 실시간 프로젝트 + 인피드 광고 합치기
        List<ProjectResponseDTO.ListDTO> projects =
                projectService.findAllProjectsList().stream()
                        .filter(p -> "OPEN".equals(p.getProjectStatus()))
                        .sorted(Comparator.comparing(
                                ProjectResponseDTO.ListDTO::getCreatedAt).reversed())
                        .limit(9)
                        .collect(Collectors.toList());

        // 3번째 슬롯마다 광고 1개 삽입
        List<Map<String, Object>> feedItems = new ArrayList<>();
        int adIdx = 0;
        for (int i = 0; i < projects.size(); i++) {
            if (i > 0 && i % 3 == 0 && adIdx < carouselAds.size()) {
                AdvertisementResponse ad = carouselAds.get(adIdx++);
                Map<String, Object> adItem = new HashMap<>();
                adItem.put("isAd", true);
                adItem.put("expertId", ad.getExpertId());
                adItem.put("expertName", ad.getExpertName());
                adItem.put("speciality", ad.getSpeciality());
                adItem.put("displayImageUrl", ad.getDisplayImageUrl());
                adItem.put("customMessage", ad.getCustomMessage());
                feedItems.add(adItem);
            }
            ProjectResponseDTO.ListDTO p = projects.get(i);
            Map<String, Object> projItem = new HashMap<>();
            projItem.put("isAd", false);
            projItem.put("id", p.getId());
            projItem.put("title", p.getTitle());
            projItem.put("categoryName", p.getCategoryName());
            projItem.put("category", p.getCategoryName());
            projItem.put("budgetMin", p.getBudgetMin());
            projItem.put("budgetMax", p.getBudgetMax());
            projItem.put("budget", p.getBudgetMin());
            projItem.put("dday", p.getDDay());
            projItem.put("endDate", p.getDeadline() != null
                    ? p.getDeadline().toString().substring(0, 10) : "");
            feedItems.add(projItem);
        }
        model.addAttribute("recentProjects", feedItems);

        // 4. 마스터 전문가
        List<ExpertProfileResponse> masterExperts =
                expertProfileService.getAll().stream()
                        .filter(e -> "MASTER".equals(e.getGrade()))
                        .limit(6)
                        .collect(Collectors.toList());
        model.addAttribute("masterExperts", masterExperts);

        // 5. 최신 리뷰
        try {
            model.addAttribute("recentReviews",
                    reviewService.getRecentReviews(4));
        } catch (Exception e) {
            model.addAttribute("recentReviews", List.of());
        }
        // 6. 경매 낙찰 광고
        try {
            List<AdBid> approvedBids = adBidService.getApprovedWinnerBids();
            List<Map<String, Object>> auctionAds = approvedBids.stream().map(b -> {
                Member winner = memberRepository.findById(b.getExpertId()).orElse(null);
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("expertId", b.getExpertId());
                map.put("expertName", winner != null ? winner.getName() : "");
                map.put("adMessage", b.getAdMessage() != null ? b.getAdMessage() : "");
                map.put("bannerImage", b.getBannerImage() != null ?
                        "/images/" + b.getBannerImage() : "/images/default-banner.png");
                return map;
            }).toList();
            model.addAttribute("auctionAds", auctionAds);
            model.addAttribute("hasAuctionAds", !auctionAds.isEmpty());
        } catch (Exception e) {
            model.addAttribute("auctionAds", List.of());
            model.addAttribute("hasAuctionAds", false);
        }

        return "main";
    }
}