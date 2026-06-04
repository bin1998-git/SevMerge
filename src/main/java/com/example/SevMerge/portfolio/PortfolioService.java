package com.example.SevMerge.portfolio;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;
    private final ExpertProfileRepository expertProfileRepository;

    public List<PortfolioResponse.ListDTO> findByMemberId(Long expertId) {

        Member expertEntity = memberRepository.findById(expertId).orElseThrow(
                () -> new NotFoundException("전문가를 찾을 수 없습니다.")
        );
        // 전문가 아이디로 찾아 해당 전문가 포트 폴리오
        return portfolioRepository.findByExpertIdIsActive(expertEntity.getId());
    }




    public PortfolioResponse.DetailDTO findPortfolio(Long portfolioId) {

        Portfolio portfolioEntity = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new NotFoundException("게시글을 찾을 수 없습니다.")
        );
        //
        return new PortfolioResponse.DetailDTO(portfolioEntity);
    }





    public void save(PortfolioRequest.SaveDTO saveDTO) {

        Portfolio newPortfolio = Portfolio
                .builder()
                .expertProfile(expertProfileRepository
                        .findById(saveDTO.getExpertId()).orElseThrow(() -> new BadRequestException("전문가를 찾지못했습니다.")))
                .title(saveDTO.getTitle())
                .description(saveDTO.getDescription())
                .imageUrl(saveDTO.getImageUrl())
                .projectUrl(saveDTO.getProjectUrl())
                .build();

        portfolioRepository.save(newPortfolio);

    }


    public PortfolioResponse.UpdateDTO updatePage(Long portfolioId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() ->
                new BadRequestException("포트폴리오를 찾을수 없습니다.")
        );
        return new PortfolioResponse.UpdateDTO(portfolio);
    }

    @Transactional
    public void update(Long portfolioId, PortfolioRequest.UpdateDTO updateDTO) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() ->
                new BadRequestException("포트폴리오를 찾을수 없습니다.")
        );

        portfolio.setDescription(updateDTO.getDescription());
        portfolio.setTitle(updateDTO.getTitle());
        portfolio.setImageUrl(updateDTO.getImageUrl());
        portfolio.setProjectUrl(updateDTO.getProjectUrl());

    }


    public void delete(Long portfolioId) {

       Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() ->
                new BadRequestException("포트폴리오를 찾을수 없습니다.")
        );

        portfolioRepository.deleteById(portfolio.getId());

    }
}
