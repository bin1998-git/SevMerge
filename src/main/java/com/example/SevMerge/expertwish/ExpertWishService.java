package com.example.SevMerge.expertwish;

import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
@Service
@RequiredArgsConstructor
public class ExpertWishService {

    private final ExpertWishRepository expertWishRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void toggleWish(Long memberId, Long expertId) {
        if (expertWishRepository.existsByMemberIdAndExpertId(memberId, expertId)){
            // 이미 찜을 했다면 삭제
            expertWishRepository.deleteByMemberIdAndExpertId(memberId,expertId);
        } else {
            // 저장
            Member member = memberRepository.findById(memberId).orElseThrow(
                    () -> new NotFoundException("사용자를 찾을수 없습니다"));
            Member expert = memberRepository.findById(expertId).orElseThrow(
                    () -> new NotFoundException("전문가를 찾을 수 없습니다"));


            ExpertWish wish = ExpertWish.builder()
                    .member(member)
                    .expert(expert)
                    .build();

            expertWishRepository.save(wish);
        }
    }
    @Transactional(readOnly = true)
    public Page<ExpertWish> findMyWishesPaged(Long memberId, Pageable pageable) {
        return expertWishRepository.findAllByMemberIdOrderByIdDesc(memberId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ExpertWish> filterWishesPaged(Long memberId, String keyword, Pageable pageable) {
        return expertWishRepository.findByMemberIdAndExpertNameContaining(keyword, memberId, pageable);
    }
}
