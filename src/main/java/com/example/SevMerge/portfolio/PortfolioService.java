package com.example.SevMerge.portfolio;

import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;

    public List<PortfolioResponse.ListDTO> findByMemberId(Long expertId) {

        Member expertEntity = memberRepository.findById(expertId).orElseThrow(
            () -> new NotFoundException("전문가를 찾을 수 없습니다.")
        );

        return portfolioRepository.findByExpertIdIsActive(expertEntity.getId());
    }

    public PortfolioResponse.DetailDTO findPortfolio(Long portfolioId) {

        Portfolio portfolioEntity = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new NotFoundException("게시글을 찾을 수 없습니다.")
        );

        return new PortfolioResponse.DetailDTO(portfolioEntity);
    }
}
