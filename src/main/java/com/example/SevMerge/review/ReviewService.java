package com.example.SevMerge.review;


import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {


    private final ReviewRepository reviewRepository;
    private final ExpertProfileRepository expertProfileRepository;


    // 리뷰작성
    @Transactional
    public void save(ReviewRequest.SaveReviewDTO reviewDTO, Member member) {

        // 로그인 인터셉터 처리

        if(member == null){
            throw  new BadRequestException("로그인 먼저 해주세요");
        }

        //  유효성
        reviewDTO.validate(reviewDTO);

        ExpertProfile expertProfile = expertProfileRepository.findById(reviewDTO.getExpertId()).orElseThrow(() ->
                new BadRequestException("전문가를 찾을수 없습니다.")
        );


        Review newReview = Review.builder()
                .member(member)
                .expertProfile(expertProfile)
                .content(reviewDTO.getContent())
                .countStar(reviewDTO.getRating())
                .build();

        reviewRepository.save(newReview);

    }


    // 리뷰작성 화면
    public ReviewResponse.ReviewSaveDTO savePage( Long expertProfileId) {


        ExpertProfile expertProfile = expertProfileRepository.findById(expertProfileId).orElseThrow(() ->
                new BadRequestException("전문가를 찾을수 없습니다.")
        );

        ReviewResponse.ReviewSaveDTO saveDTO = new ReviewResponse.ReviewSaveDTO(expertProfile);

        return saveDTO;

    }


    // 리뷰조회
    public ReviewResponse.ReviewDetailDTO detail(Long id) {

        Review review = reviewRepository.findById(id).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );

        return new ReviewResponse.ReviewDetailDTO(review);
    }

    // 리뷰 목록
    public ReviewResponse.ReviewListPageDTO reviewsListPage (Long expertProfileId){


        List<Review> reviews = reviewRepository.findByExpertProfile(expertProfileId);

        ExpertProfile expertProfileEntity = expertProfileRepository.findById(expertProfileId).orElseThrow(() ->
                new BadRequestException("전문가를 찾을수 없습니다.")
                );


        // 특정 전문가에게 달릴 리뷰들

        List<ReviewResponse.ReviewListDTO> reviewListDTOS = new ArrayList<>();

        for (int i = 0; i < reviews.size(); i++){

            ReviewResponse.ReviewListDTO reviewListDTO = new ReviewResponse.ReviewListDTO(reviews.get(i));

            reviewListDTOS.add(reviewListDTO);

        }
        // 특정 전문가
        ReviewResponse.ExpertListDTO expertListDTO =  new ReviewResponse.ExpertListDTO(expertProfileEntity);

        return new ReviewResponse.ReviewListPageDTO(reviewListDTOS,expertListDTO);

    }



}
