package com.example.SevMerge;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfileResponse;
import com.example.SevMerge.expertprofile.ExpertProfileService;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.Role;
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

    private final ExpertProfileService expertProfileService;

    @GetMapping("/")
    public String introPage(HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember != null) {
            return "redirect:/exmain";
        }
        return "intro";
    }

    @GetMapping("/exmain")
    public String exmainPage(HttpSession session,Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);

        if (loginMember != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("sessionUser", loginMember);
            model.addAttribute("isExpert", loginMember.isExpert());
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        List<ExpertProfileResponse> all = expertProfileService.getAll();

        // 섹션 1 — 오분대기조: avgRating 높은 순 상위 6명
        List<ExpertProfileResponse> fastExperts = all.stream()
                .sorted(Comparator.comparing(ExpertProfileResponse::getAvgRating,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(6)
                .toList();

        // 섹션 2 — 바가지 수사대: totalReviews(완료 건수) 많은 순 상위 6명
        List<ExpertProfileResponse> valueExperts = all.stream()
                .sorted(Comparator.comparingInt(ExpertProfileResponse::getTotalReviews).reversed())
                .limit(6)
                .toList();

        // 섹션 3 — 만족 취조실: isCertified 우선 정렬, 그 다음 avgRating 순
        List<ExpertProfileResponse> asExperts = all.stream()
                .sorted(Comparator.comparing(ExpertProfileResponse::isCertified).reversed()
                        .thenComparing(Comparator.comparing(ExpertProfileResponse::getAvgRating,
                                Comparator.nullsLast(Comparator.naturalOrder())).reversed()))
                .limit(6)
                .toList();

        model.addAttribute("fastExperts", fastExperts);
        model.addAttribute("valueExperts", valueExperts);
        model.addAttribute("asExperts", asExperts);
        return "exmain";
    }
}
