package com.example.SevMerge.review;


import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;

import com.example.SevMerge.member.MemberService;
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
    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final BidService bidService;

    // 리뷰작성 화면
    @GetMapping("/reviews/save")
    public String saveReviewForm(Model model , HttpSession session,
            @RequestParam(required = false) Long targetId
    ) {

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        if(sessionMember == null ){
            return "redirect:/login";
        }

        Member targeter = memberService.findMemberById(targetId);

        System.out.println(targeter.getName());


        model.addAttribute("targeter", targeter);
        model.addAttribute("reviewer",sessionMember);

        return  "review/review-save";
    }


    // 리뷰 작성 후 저장
    @PostMapping("/reviews/save")
    public String saveReview(ReviewRequest.SaveReviewDTO reviewDTO, HttpSession session,Model model,
                             @RequestParam(required = false) Long targetId) {

        Member sessionMember = (Member) session.getAttribute("sessionUser"); // 누가 쓸건지 특정

        if(sessionMember == null ){
            return "redirect:/login";
        }
        Member targeter = memberService.findMemberById(targetId);

        model.addAttribute("targeter",targeter);
        model.addAttribute("reviewer",sessionMember);

        reviewService.save(reviewDTO,sessionMember);

        return "redirect:/mypage/tab?=reviews";
    }

    // 리뷰상세 화면
    @GetMapping("/reviews/{reviewId}")
    public String reviewDetail(@PathVariable(name = "reviewId") Long reviewId,Model model, HttpSession session){

        Member sessionMember = (Member) session.getAttribute("sessionUser");
        if(sessionMember == null){
            return "redirect:/login";
        }
        ReviewResponse.ReviewDetailDTO review = reviewService.detail(reviewId);

        model.addAttribute("review",review);

        return "review/review-detail";
    }

    // 리뷰 수정 화면
    @GetMapping("/reviews/{reviewId}/edit")
    public String editReviewForm(@PathVariable(name = "reviewId") Long reviewId, Model model , HttpSession session){

        Member sessionMember = (Member) session.getAttribute("sessionUser");
        if(sessionMember == null){
            return "redirect:/login";
        }
        return "review/review-update";
    }


    // 리뷰 수정 기능
    @PostMapping("/reviews/{reviewId}/update")
    public String updateReview(@PathVariable(name = "reviewId") Long reviewId, ReviewRequest.UpdateRequestDTO updateRequestDTO,HttpSession session) {

        Member sessionMember = (Member) session.getAttribute("sessionUser");
        if(sessionMember == null){
            return "redirect:/login";
        }

        reviewService.updateReview(updateRequestDTO,reviewId);

        return "redirect:/mypage/tab?=reviews";

    }

    // 리뷰 삭제 기능
    @PostMapping("/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable(name = "reviewId") Long reviewId,HttpSession session) {

        Member sessionMember = (Member) session.getAttribute("sessionUser");
        if(sessionMember == null){
            return "redirect:/login";
        }
        reviewService.deleteReview(reviewId);
        // 삭제후 해당 전문가 리뷰 목록으로 돌아간다
        return "redirect:/reviews?expertId=";

    }


}
