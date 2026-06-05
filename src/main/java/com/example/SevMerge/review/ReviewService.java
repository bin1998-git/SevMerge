package com.example.SevMerge.review;


import com.example.SevMerge.bid.Bid;
import com.example.SevMerge.bid.BidRepository;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.core.exception.UnauthorizedException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.MemberResponse;
import com.example.SevMerge.member.Role;
import com.example.SevMerge.project.Project;
import com.example.SevMerge.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.AbstractProtocol;
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
    private final ProjectRepository projectRepository;
    private final BidRepository bidRepository;

    // 리뷰작성
    @Transactional
    public void save(ReviewRequest.SaveReviewDTO reviewDTO, Member reviewer) {

        if (reviewer == null) {
            throw new BadRequestException("로그인 먼저 해주세요");
        }

        reviewDTO.validate(reviewDTO);

        Member targetEntity = memberRepository.findById(reviewDTO.getTargetId()).orElseThrow(
                () -> new NotFoundException("대상자를 찾을 수 없습니다.")
        );

        reviewRepository.save(reviewDTO.toEntity(reviewer, targetEntity));

    }

    // 리뷰조회
    public ReviewResponse.ReviewDetailDTO detail(Long id) {

        Review review = reviewRepository.findById(id).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );

        return new ReviewResponse.ReviewDetailDTO(review);
    }


    // 리뷰수정
    @Transactional
    public void updateReview(ReviewRequest.UpdateRequestDTO updateDTO, Long reviewId) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );

        if(!review.getReviewer().getId().equals(reviewId)) {
            throw new UnauthorizedException("리뷰 수정 권한이 없습니다.");
        }

        updateDTO.validate();

        review.update(updateDTO);

    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );

        if(!review.getReviewer().getId().equals(reviewId)) {
            throw new UnauthorizedException("리뷰 삭제 권한이 없습니다");
        }

        review.softDelete();

    }

    public List<ReviewResponse.ReviewListDTO> findMyReviews(Long targetId) {
        return reviewRepository.findMyReviews(targetId)
                .stream()
                .map(ReviewResponse.ReviewListDTO::new)
                .toList();
    }

    public Double avgRating(Long targetId) {
        return reviewRepository.avgRating(targetId);
    }
}
