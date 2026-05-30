package com.example.SevMerge.review;

import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰목록 화면
    @GetMapping("/reviews")
    public String reviewList() {



         return "review/review-list";
    }

    // 리뷰작성 화면
    @GetMapping("/reviews/save")
    public String saveReviewForm() {
        return  "review/review-save";
    }

    // 리뷰 작성 후 저장
    @PostMapping("/reviews/save")
    public String saveReview(ReviewRequest.SaveReviewDTO reviewDTO, Model model, HttpSession session) {

        Member member = (Member) session.getAttribute("sessionMember"); // 누가 쓸건지 특정

        Review review = reviewService.save(reviewDTO);
        // 작성한 리뷰 뿌리기
        model.addAttribute("review",review);

        // 반환후 리뷰 리스트로 이동

        // 리뷰 등록·삭제 시마다 avgRating
        return "redirect:/review/list";
    }





    // 리뷰조회 해당전문가 누적 별점 , 리뷰목록 공개
    @GetMapping("/reviews/{id}" )
    public String reviewDetail(@PathVariable(name = "id") Long id){

        reviewService.findById(id);


        return "/review/review-detail";
    }



    //별점 집계및 뱃지갱신



    // 리뷰 페이지 테스트용
    @GetMapping ("reviewTest/detail")
    public String reviewTestDetail(){

        return "review/review-detailTEST";

    }

    @GetMapping ("reviewTest/list")
    public String reviewTestList(){

        return "review/review-listTEST";

    }

    @GetMapping ("reviewTest/save")
    public String reviewTestSave(){

        return "review/review-saveTEST";

    }

    @GetMapping ("reviewTest/update")
    public String reviewTestUpdate(){

        return "review/review-updateTEST";

    }

}
