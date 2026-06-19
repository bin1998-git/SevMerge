package com.example.SevMerge.ai;

import com.example.SevMerge.expertwish.ExpertWishRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// ai에게 인기많은 전문가 목록이랑 상세페이지 링크
public class ExpertToolService {

    private final ExpertWishRepository expertWishRepository;
    private final MemberRepository memberRepository;

    @Tool(description = "인기 전문가 목록과 상세 페이지 링크를 조회합니다.")
    public String getPopularExperts() {
       List<Long> topExpertIds = expertWishRepository.findTopExpertIds();
       if (topExpertIds.isEmpty()) {
           return "현재 찜을 받은 전문가가 없습니다.";
       }

       return topExpertIds.stream()
               .limit(5)// 상위 5명제한
               .map(id -> {
                   Member expert = memberRepository.findById(id).orElseThrow();
                   long count = expertWishRepository.countByExpertId(id);
                   String displayCount = (count > 999) ? "999+" : String.valueOf(count);

                   return expert.getName() + "(찜 : " + displayCount + ") - 상세정보: /experts/" + id;
               })
               .collect(Collectors.joining("\n"));
    }
}
