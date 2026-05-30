package com.example.SevMerge.review;


import com.example.SevMerge.member.Member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰목록 화면
    @GetMapping("/reviews")
    public String reviewList(Model model, @RequestParam("expertId") Long expertId) {


        ReviewResponse.ReviewListPageDTO reviewListPageDTO = reviewService.reviewsListPage(expertId);

        model.addAttribute("expertProfile",reviewListPageDTO.getExpertProfile());
        model.addAttribute("reviews",reviewListPageDTO.getReviews());

         return "review/review-list";
    }

    // 리뷰작성 화면
    @GetMapping("/reviews/save")
    public String saveReviewForm(Model model , HttpSession session,@RequestParam Long expertId) {

        Member sessionMember = (Member) session.getAttribute("sessionUser");
        if(sessionMember == null ){

            return "redirect:/login";

        }

        ReviewResponse.ReviewSaveDTO reviewSaveDTO = reviewService.savePage(expertId);

        model.addAttribute("expertProfile",reviewSaveDTO.getExpertProfile());

        return  "review/review-save";
    }


    // 리뷰 작성 후 저장
    @PostMapping("/reviews/save")
    public String saveReview(ReviewRequest.SaveReviewDTO reviewDTO, HttpSession session) {

        Member sessionMember = (Member) session.getAttribute("sessionUser"); // 누가 쓸건지 특정

        if(sessionMember == null ){

            return "redirect:/login";

        }

        reviewService.save(reviewDTO,sessionMember);

        return "redirect:/reviews";
    }







    // 리뷰상세 해당전문가 누적 별점 , 리뷰목록 공개 기능
    @GetMapping("/reviews/{id}" )
    public String reviewDetail(@PathVariable(name = "id") Long id,Model model){

        ReviewResponse.ReviewDetailDTO review = reviewService.detail(id);

        model.addAttribute("review",review);

        return "review/review-detail";
    }



    //별점 집계및 뱃지갱신



}
