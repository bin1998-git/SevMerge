package com.example.SevMerge.review;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("review/list")
    public String reviewPage() {
         return "review/review-list";
    }

    // 리뷰작성 화면
    @GetMapping("review/review-save")
    public String reviewSavePage() {
        return  "review/review-save";
    }

    // 리뷰 기능
    @PostMapping("review/save")
    public String reviewProc(Review review, Model model) {

        reviewService.save(review);

        model.addAttribute("review",review);

        return "redirect:/review/list";
    }

}
