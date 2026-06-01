package com.example.SevMerge.review;


import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.project.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewService {


    private final ReviewRepository reviewRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final MemberRepository memberRepository;


    // 리뷰작성
    @Transactional
    public void save(ReviewRequest.SaveReviewDTO reviewDTO, Member member) {

        // 로그인 인터셉터 처리

        if (member == null) {
            throw new BadRequestException("로그인 먼저 해주세요");
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

        // ExpertProfile 갱신
        BigDecimal avgRating = reviewRepository.avgRating(expertProfile.getId()).orElse(BigDecimal.ZERO);

        Integer reviewCount = reviewRepository.countReview(expertProfile.getId()).orElse(0);

        expertProfile.setTotalReviews(reviewCount);
        expertProfile.setAvgRating(avgRating);


    }


    // 리뷰작성 화면
    public ReviewResponse.ReviewSaveDTO savePage(Long expertProfileId) {


        ExpertProfile expertProfile = expertProfileRepository.findById(expertProfileId).orElseThrow(() ->
                new BadRequestException("전문가를 찾을수 없습니다.")
        );

        ReviewResponse.ReviewSaveDTO saveDTO = new ReviewResponse.ReviewSaveDTO(expertProfile);

        return saveDTO;

    }

    // 전문가가 의뢰인에게 리뷰 작성화면
    public ReviewResponse.ExpertReviewToClient saveExpertToClientPage(Long expertProfileId, Long memberId) {

       Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new BadRequestException("해당 유저를 찾을수 없습니다.")
                );

       return new ReviewResponse.ExpertReviewToClient(expertProfileId,member);
    }


    // 리뷰조회
    public ReviewResponse.ReviewDetailDTO detail(Long id) {

        Review review = reviewRepository.findById(id).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );

        return new ReviewResponse.ReviewDetailDTO(review);
    }


    // 리뷰 목록
    public ReviewResponse.ReviewListPageDTO reviewsListPage(Long expertProfileId, int page, Member sessionMember) {

        // 1 페이지씩 5개
        Pageable pageable = PageRequest.of(page - 1, 5);

        // 0 부터 4 까지 리뷰목록
        Page<Review> reviews = reviewRepository.findByExpertProfileReviewPage(expertProfileId, pageable);

        ExpertProfile expertProfileEntity = expertProfileRepository.findById(expertProfileId).orElseThrow(() ->
                new BadRequestException("전문가를 찾을수 없습니다.")
        );

        // 실제 리뷰 목록
        List<Review> reviewList = reviews.getContent();


        List<ReviewResponse.ReviewListDTO> reviewListDTOS = reviewList.stream()
                .map(review -> new ReviewResponse.ReviewListDTO(review, sessionMember))
                .collect(Collectors.toList());


        // 페이징 정보 만들기
        ReviewResponse.PagingDTO pagingDTO = new ReviewResponse.PagingDTO();
        pagingDTO.setHasNext(reviews.hasNext());
        pagingDTO.setHasPrev(reviews.hasPrevious());
        pagingDTO.setNextPage(page + 1);
        pagingDTO.setPrevPage(page - 1);

        // 페이지 번호 리스트 만들기
        List<ReviewResponse.PagingDTO.PageDTO> pageList = new ArrayList<>();
        for (int i = 1; i <= reviews.getTotalPages(); i++) {

            ReviewResponse.PagingDTO.PageDTO pageDTO = new ReviewResponse.PagingDTO.PageDTO();
            pageDTO.setNum(i);
            pageDTO.setCurrent(i == page);
            pageList.add(pageDTO);

        }
        pagingDTO.setPageList(pageList);

        // 특정 전문가
        ReviewResponse.ExpertListDTO expertListDTO = new ReviewResponse.ExpertListDTO(expertProfileEntity);

        return new ReviewResponse.ReviewListPageDTO(reviewListDTOS, expertListDTO, pagingDTO);

    }


    // 리뷰 수정 화면
    public ReviewResponse.UpdateDTO updatePage(Long reviewId) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );

        ReviewResponse.UpdateDTO reviewEntity = new ReviewResponse.UpdateDTO(review);

        return reviewEntity;

    }


    // 리뷰수정
    @Transactional
    public void updateReview(ReviewRequest.UpdateRequestDTO dto, Long reviewId) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );
        review.setCountStar(dto.getRating());
        review.setContent(dto.getContent());

        // 리뷰로 전문가 찾기
        ExpertProfile expertProfile = review.getExpertProfile();

        // 전문가 별점 갱신 리뷰수도 갱신
        BigDecimal avgRating = reviewRepository.avgRating(expertProfile.getId()).orElse(BigDecimal.ZERO);
        Integer reviewCount = reviewRepository.countReview(expertProfile.getId()).orElse(0);

        expertProfile.setTotalReviews(reviewCount);
        expertProfile.setAvgRating(avgRating);



    }


    // 리뷰 삭제
    @Transactional
    public Long deleteReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );
        Long expertId = review.getExpertProfile().getId();

        reviewRepository.deleteById(reviewId);

        Integer reviewCount = reviewRepository.countReview(expertId).orElse(0);
        BigDecimal avgRating = reviewRepository.avgRating(expertId).orElse(BigDecimal.ZERO);

        ExpertProfile expertProfile = expertProfileRepository.findById(expertId).orElseThrow(() ->
                    new BadRequestException("전문가를 찾을수 없습니다.")
                );

        expertProfile.setTotalReviews(reviewCount);
        expertProfile.setAvgRating(avgRating);


        return expertId;

    }


}
