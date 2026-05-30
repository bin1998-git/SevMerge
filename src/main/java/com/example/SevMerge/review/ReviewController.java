package com.example.SevMerge.review;

import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.project.Project;
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
    public String reviewList() {



         return "review/review-list";
    }

    // 리뷰작성 화면
    @GetMapping("/reviews/save")
    public String saveReviewForm(Model model , HttpSession session,@RequestParam Long expertId, @RequestParam Long projectId) {

        Member sessionMember = (Member) session.getAttribute("sessionUser");

        ReviewResponse.ReviewSaveDTO reviewSaveDTO = reviewService.savePage(sessionMember,expertId,projectId);


        model.addAttribute("expertProfile",reviewSaveDTO.getExpertProfile());
        model.addAttribute("project",reviewSaveDTO.getProject());

        return  "review/review-save";
    }

    // 리뷰 작성 후 저장
    @PostMapping("/reviews/save")
    public String saveReview(ReviewRequest.SaveReviewDTO reviewDTO, HttpSession session) {

        Member member = (Member) session.getAttribute("sessionUser"); // 누가 쓸건지 특정

        reviewService.save(reviewDTO,member);

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
