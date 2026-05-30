package com.example.SevMerge.review;


import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.project.Project;
import com.example.SevMerge.project.ProjectRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewService {


    private final ReviewRepository reviewRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;


    // 리뷰작성
    @Transactional
    public void save(ReviewRequest.SaveReviewDTO reviewDTO, Member member) {

        // 로그인 인터셉터 처리

        //  유효성
        reviewDTO.validate(reviewDTO);

        ExpertProfile expertProfile = expertProfileRepository.findById(reviewDTO.getExpertId()).orElseThrow(() ->
                new BadRequestException("전문가를 찾을수 없습니다.")
        );
        Project project = projectRepository.findByProjectId(reviewDTO.getProjectId()).orElseThrow(() ->
                new BadRequestException("해당 프로젝트는 존재하지 않습니다.")
        );

        Review newReview = Review.builder()
                .project(project)
                .member(member)
                .expertProfile(expertProfile)
                .content(reviewDTO.getContent())
                .countStar(reviewDTO.getRating())
                .build();

        reviewRepository.save(newReview);

    }


    // 작성 화면
    public ReviewResponse.ReviewSaveDTO savePage(Member member, Long projectId, Long expertProfileId) {

        ExpertProfile expertProfile = expertProfileRepository.findById(expertProfileId).orElseThrow(() ->
                new BadRequestException("전문가를 찾을수 없습니다.")
        );
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new BadRequestException("프로젝트를 찾을수 없습니다.")
        );
        ReviewResponse.ReviewSaveDTO saveDTO = new ReviewResponse.ReviewSaveDTO(expertProfile, project);

        return saveDTO;

    }


    // 리뷰조회
    public ReviewResponse.ReviewDetailDTO detail(Long id) {

        Review review = reviewRepository.findById(id).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );

        return new ReviewResponse.ReviewDetailDTO(review);
    }

}
