package com.example.SevMerge.review;


import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileService;
import com.example.SevMerge.member.Member;

import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.member.Role;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberService memberService;

    // 전문가 리뷰 확인
    @GetMapping("/reviews/my")
    public String myReviews(HttpSession session,
                            Model model) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);

        if (sessionUser == null) {
            return "login-form";
        }

        List<ReviewResponse.ReviewListDTO> reviewList = reviewService.findMyReviews(sessionUser.getId());
        model.addAttribute("reviews", reviewList);
        double avg = reviewService.avgRating(sessionUser.getId());
        model.addAttribute("avgStar", String.format("%.1f", avg));

        return "review/my-reviews";
    }

    // 리뷰작성 화면
    @GetMapping("/reviews/save")
    public String saveReviewForm(Model model, HttpSession session,
                                 @RequestParam(required = false) Long targetId
    ) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "login-form";
        }

        Member targeter = memberService.findMemberById(targetId);

        System.out.println(targeter.getName());


        model.addAttribute("targeter", targeter);
        model.addAttribute("reviewer", sessionUser);

        return "review/review-save";
    }


    // 리뷰 작성 후 저장
    @PostMapping("/reviews/save")
    public String saveReview(ReviewRequest.SaveReviewDTO reviewDTO, HttpSession session, Model model,
                             @RequestParam(required = false) Long targetId) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER); // 누가 쓸건지 특정

        if (sessionUser == null) {
            return "login-form";
        }
        Member targeter = memberService.findMemberById(targetId);

        model.addAttribute("targeter", targeter);
        model.addAttribute("reviewer", sessionUser);

        reviewService.save(reviewDTO, sessionUser);

        return "redirect:/my-pages?tab=reviews";
    }

    // 리뷰상세 화면
    @GetMapping("/reviews/{reviewId}")
    public String reviewDetail(@PathVariable(name = "reviewId") Long reviewId, Model model, HttpSession session) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "login-form";
        }
        ReviewResponse.ReviewDetailDTO review = reviewService.detail(reviewId);

        model.addAttribute("review", review);
        model.addAttribute("isOwner",review.getReviewerId().equals(sessionUser.getId()) );
        return "review/review-detail";
    }

    // 리뷰 수정 화면
    @GetMapping("/reviews/{reviewId}/edit")
    public String editReviewForm(@PathVariable(name = "reviewId") Long reviewId, Model model, HttpSession session) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "login-form";
        }

        model.addAttribute("review",reviewService.detail(reviewId));
        return "review/review-update";
    }


    @PutMapping("/reviews/{reviewId}")
    @ResponseBody
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @RequestBody ReviewRequest.UpdateRequestDTO updateRequestDTO,
                                          HttpSession session) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return ResponseEntity.status(401).build();
        }

        reviewService.updateReview(updateRequestDTO, reviewId, sessionUser.getId());

        return ResponseEntity.ok().build();
    }

    // 리뷰 삭제 기능
    @PostMapping("/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable(name = "reviewId") Long reviewId, HttpSession session) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "login-form";
        }
        reviewService.deleteReview(reviewId,sessionUser.getId());
        // 삭제후 해당 전문가 리뷰 목록으로 돌아간다
        return "redirect:/my-pages?tab=reviews";

    }


}
