package com.example.SevMerge;

import com.example.SevMerge.advertisement.AdvertisementPlacement;
import com.example.SevMerge.advertisement.AdvertisementResponse;
import com.example.SevMerge.advertisement.AdvertisementService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.ProjectResponseDTO;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class main {

    private final ProjectService projectService;
    private final AdvertisementService advertisementService;

    @GetMapping("/")
    public String introPage(HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember != null) {
            return "redirect:/exmain";
        }
        return "intro";
    }

    @GetMapping("/exmain")
    public String exmainPage(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);

        if (loginMember != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("sessionUser", loginMember);
            model.addAttribute("isExpert", loginMember.isExpert());
            model.addAttribute("isClient", loginMember.isClient());
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        advertisementService.expireOutdatedAds();

        // 광고 배너 조회 (추가)
        List<AdvertisementResponse> mainAds = advertisementService.getActiveAds(AdvertisementPlacement.MAIN_BANNER);
        model.addAttribute("mainAds", mainAds);

        List<ProjectResponseDTO.ListDTO> all = projectService.findAllProjects()
                .stream()
                .filter(p -> "OPEN".equals(p.getProjectStatus()))
                .toList();

        // 섹션 1 — 마감 임박 의뢰: dDay 오름차순 상위 6개
        List<ProjectResponseDTO.ListDTO> urgentProjects = all.stream()
                .filter(p -> p.getDDay() >= 0)
                .sorted(Comparator.comparingInt(ProjectResponseDTO.ListDTO::getDDay))
                .limit(6)
                .toList();

        // 섹션 2 — 합리적인 예산 의뢰: budgetMax 오름차순 상위 6개
        List<ProjectResponseDTO.ListDTO> budgetProjects = all.stream()
                .sorted(Comparator.comparingInt(ProjectResponseDTO.ListDTO::getBudgetMax))
                .limit(6)
                .toList();

        // 섹션 3 — 최신 의뢰: createdAt 최신순 상위 6개
        List<ProjectResponseDTO.ListDTO> latestProjects = all.stream()
                .sorted(Comparator.comparing(ProjectResponseDTO.ListDTO::getCreatedAt).reversed())
                .limit(6)
                .toList();

        model.addAttribute("urgentProjects", urgentProjects);
        model.addAttribute("budgetProjects", budgetProjects);
        model.addAttribute("latestProjects", latestProjects);
        return "exmain";
    }
}