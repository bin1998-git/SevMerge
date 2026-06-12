package com.example.SevMerge.portfolio;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;
    private final ExpertProfileRepository expertProfileRepository;


    // 포트폴리오 리스트 페이징
    public List<PortfolioResponse.ListDTO> findByMemberId(Long expertId, int page) {

        Member expertEntity = memberRepository.findById(expertId).orElseThrow(
                () -> new NotFoundException("전문가를 찾을 수 없습니다.")
        );
        Pageable pageable = PageRequest.of(page-1,10, Sort.by("createdAt").descending());
        // 전문가 아이디로 찾아 해당 전문가 포트 폴리오
        Page<Portfolio> portfolioPage = portfolioRepository.findByExpertIdIsActive(expertEntity.getId(),pageable);
        Long count = portfolioRepository.countPortfolioByMemberId(expertEntity.getId());
        return portfolioPage.stream().map(portfolio -> new PortfolioResponse.ListDTO(portfolio,count)).toList();
    }


    // 포트폴리오 리스트
    public List<Portfolio> findPortfolioList(Long memberId){

       return portfolioRepository.findByMemberId(memberId);

    }


    // 포트폴리오 아이디로 포트플리오 찾고 포트폴리오 DetailDTO 반환
    @Transactional
    public PortfolioResponse.DetailDTO findPortfolio(Long portfolioId) {

        Portfolio portfolioEntity = portfolioRepository.findById(portfolioId).orElseThrow(
                () -> new NotFoundException("게시글을 찾을 수 없습니다.")
        );
        //
        return new PortfolioResponse.DetailDTO(portfolioEntity);
    }




    @Transactional
    public void save(PortfolioRequest.SaveDTO saveDTO)  {



        saveDTO.validate();

        System.out.println("saveDTO: "+saveDTO);
        Portfolio newPortfolio = Portfolio
                .builder()
                .expertProfile(expertProfileRepository
                        .findByMemberId(saveDTO.getExpertId()).orElseThrow(() -> new BadRequestException("전문가를 찾지못했습니다.")))
                .title(saveDTO.getTitle())
                .description(saveDTO.getDescription())
                .imageUrl(saveDTO.getImageUrl())
                .projectUrl(saveDTO.getProjectUrl())
                .isActive(true)
                .build();

        portfolioRepository.save(newPortfolio);

    }



    @Transactional
    public void update(Long portfolioId, PortfolioRequest.UpdateDTO updateDTO,Long sessionUserId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() ->
                new BadRequestException("포트폴리오를 찾을수 없습니다.")
        );

        if(!portfolio.getExpertProfile().getMember().getId().equals(sessionUserId)){
            throw new ForbiddenException("수정 권한이 없습니다.");
        }


        updateDTO.validate();

        portfolio.setDescription(updateDTO.getDescription());
        portfolio.setTitle(updateDTO.getTitle());
        portfolio.setImageUrl(updateDTO.getImageUrl());
        portfolio.setProjectUrl(updateDTO.getProjectUrl());

    }

    @Transactional
    public void delete(Long portfolioId) {
       Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() ->
                new BadRequestException("포트폴리오를 찾을수 없습니다.")
        );
       portfolio.delete();
    }
}
