package com.example.SevMerge.review;


import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.core.exception.UnauthorizedException;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.payment.PaymentRepository;
import com.example.SevMerge.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewService {


    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final ExpertProfileRepository expertProfileRepository;

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

        // 완료된 거래가 있는지 확인 (의뢰인→전문가 또는 전문가→의뢰인 양방향 체크)
        boolean hasSettledTransaction =
                paymentRepository.existsByClientIdAndExpertIdAndStatus(reviewer.getId(), targetEntity.getId(), PaymentStatus.SETTLED)
                || paymentRepository.existsByClientIdAndExpertIdAndStatus(targetEntity.getId(), reviewer.getId(), PaymentStatus.SETTLED);

        if (!hasSettledTransaction) {
            throw new BadRequestException("완료된 거래가 있어야 리뷰를 작성할 수 있습니다.");
        }

        // 동일 대상 중복 리뷰 방지
        if (reviewRepository.existsByReviewerAndTargeter(reviewer.getId(), targetEntity.getId())) {
            throw new BadRequestException("이미 해당 사용자에게 리뷰를 작성했습니다.");
        }

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
    public void updateReview(ReviewRequest.UpdateRequestDTO updateDTO, Long reviewId, Long userId) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );

        if(!review.getReviewer().getId().equals(userId)) {
            throw new UnauthorizedException("리뷰 수정 권한이 없습니다.");
        }

        updateDTO.validate();

        review.update(updateDTO);

    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new BadRequestException("해당 리뷰는 존재하지 않습니다.")
        );

        if(!review.getReviewer().getId().equals(userId)) {
            throw new UnauthorizedException("리뷰 삭제 권한이 없습니다");
        }

        review.softDelete();

    }

    // 전문가 리뷰 조회
    public List<ReviewResponse.ReviewListDTO> findMyReviews(Long targetId) {
        return reviewRepository.findMyReviews(targetId)
                .stream()
                .map(ReviewResponse.ReviewListDTO::new)
                .toList();
    }

    // 내가 작성한 리뷰 조회
    public List<ReviewResponse.ReviewListDTO> findMySaveReviews(Long reviewerId) {
        return reviewRepository.findMySaveReviews(reviewerId)
                .stream()
                .map(ReviewResponse.ReviewListDTO::new)
                .toList();
    }

    // 평균 별점 조회
    public Double avgRating(Long targetId) {
        return reviewRepository.avgRating(targetId);
    }
}
