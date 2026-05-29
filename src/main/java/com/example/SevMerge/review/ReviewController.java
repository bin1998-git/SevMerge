package com.example.SevMerge.review;

import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰목록 화면
    @GetMapping("review/list")
    public String reviewPage() {
         return "review/review-list";
    }

    // 리뷰작성 화면
    @GetMapping("review/review-save")
    public String reviewSavePage() {
        return  "review/review-save";
    }

    // 리뷰 작성 기능
    @PostMapping("review/save")
    public String reviewSaveProc(ReviewRequest.SaveReviewDTO reviewDTO, Model model, HttpSession session) {

        Member member = (Member) session.getAttribute("sessionMember"); // 누가 쓸건지 특정

        Review review = reviewService.save(reviewDTO);
        // 작성한 리뷰 뿌리기
        model.addAttribute("review",review);

        // 반환후 리뷰 리스트로 이동

        // 리뷰 등록·삭제 시마다 avgRating
        return "redirect:/review/list";
    }


    // 리뷰조회 해당전문가 누적 별점 , 리뷰목록 공개




    //별점 집계및 뱃지갱신


}
