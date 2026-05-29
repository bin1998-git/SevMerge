package com.example.SevMerge.review;


import com.example.SevMerge.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {


    private final ReviewRepository reviewRepository;

    // 리뷰작성
    @Transactional
    public Review save(ReviewRequest.SaveReviewDTO reviewDTO) {

        // 로그인 인터셉터 처리

        //  DTO 빌더 리뷰는 일반 전문가 둘다 작성 가능하니 Member 하나만 씀
        reviewDTO.validate(reviewDTO);

        // 전문가는 Member 를 가지고 있다
        Review newReview = Review.builder()
                .member(reviewDTO.getMember())
                .content(reviewDTO.getContent())
                .countStar(reviewDTO.getCountStar())
                .build();


        return reviewRepository.save(newReview);

    }

    // 리뷰조회


}
