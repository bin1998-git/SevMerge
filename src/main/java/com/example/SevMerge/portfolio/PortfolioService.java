package com.example.SevMerge.portfolio;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.portfolio.utile.FileUtil;
import com.example.SevMerge.portfolio.utile.PortfolioAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtemplate.v4.ST;

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
    private final PortfolioAiService portfolioAiService;
    // 포트폴리오 리스트 페이징
    public Page<PortfolioResponse.ListDTO> findByMemberId(Long expertId, int page) {

        Member expertEntity = memberRepository.findById(expertId).orElseThrow(
                () -> new NotFoundException("전문가를 찾을 수 없습니다.")
        );
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());
        // 전문가 아이디로 찾아 해당 전문가 포트 폴리오
        Page<Portfolio> portfolioPage = portfolioRepository.findByExpertIdIsActive(expertEntity.getId(), pageable);

        return portfolioPage.map(PortfolioResponse.ListDTO::new);

    }

    // 포트폴리오 리스트
    public List<Portfolio> findPortfolioList(Long memberId) {
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
    public void save(PortfolioRequest.SaveDTO saveDTO) {

        saveDTO.validate();

        // ai 판별 기능 주석처리
//        if (!portfolioAiService.aiSaveValid(saveDTO)){
//            throw new BadRequestException("유효한 프로젝트를 등록해 주세요");
//        }

        try {
            Portfolio newPortfolio = null;
            String savedFileName = FileUtil.saveFile(saveDTO.getImageFile());
            newPortfolio = Portfolio
                    .builder()
                    .expertProfile(expertProfileRepository
                            .findByMemberId(saveDTO.getExpertId()).orElseThrow(() -> new BadRequestException("전문가를 찾지못했습니다.")))
                    .title(saveDTO.getTitle())
                    .description(saveDTO.getDescription())
                    .imageUrl(savedFileName)
                    .projectUrl(saveDTO.getProjectUrl())
                    .isActive(true)
                    .build();
            portfolioRepository.save(newPortfolio);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Transactional
    public void update(Long portfolioId, PortfolioRequest.UpdateDTO updateDTO, Long sessionUserId) {
        String newImageFile = null; // 파일경로
        updateDTO.validate();
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() ->
                new BadRequestException("포트폴리오를 찾을수 없습니다.")
        );

        if (!portfolio.getExpertProfile().getMember().getId().equals(sessionUserId)) {
            throw new ForbiddenException("수정 권한이 없습니다.");
        }

        // 리퀘스트로 파일이 있을때
        if (updateDTO.getImageFile() != null && !updateDTO.getImageFile().isEmpty()) {
            try {
                if (!FileUtil.isImageFile(updateDTO.getImageFile())) {
                    throw new BadRequestException("이미지 파일만 업로드 가능 합니다.");
                }
                // 새이미지 로컬 폴더에 저장 (중복되지 않을 이미지 파일 이름을 리턴)
                // 이미지 파일 경로 반환
                newImageFile = FileUtil.saveFile(updateDTO.getImageFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (newImageFile != null) {
            portfolio.setImageUrl(newImageFile);
        }

//        if (!portfolioAiService.aiUpdateValid(updateDTO)){
//            throw new BadRequestException("유효한 프로젝트를 등록해 주세요");
//        }

        portfolio.setDescription(updateDTO.getDescription());
        portfolio.setTitle(updateDTO.getTitle());
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
