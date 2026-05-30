package com.example.SevMerge.review;


import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {


    private final ReviewRepository reviewRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final ProjectRepository projectRepository;


    // 리뷰작성
    @Transactional
    public Review save(ReviewRequest.SaveReviewDTO reviewDTO) {

        // 로그인 인터셉터 처리

        //  DTO 빌더 리뷰는 일반 전문가 둘다 작성 가능하니 Member 하나만 씀
        reviewDTO.validate(reviewDTO);

        Review newReview = Review.builder()

                .content(reviewDTO.getContent())
                .countStar(reviewDTO.getRating())
                .build();


        return reviewRepository.save(newReview);

    }

    public void findById(Long id) {



        reviewRepository.findById(id);


    }

    // 리뷰조회


}
