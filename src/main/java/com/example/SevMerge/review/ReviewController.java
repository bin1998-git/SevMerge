package com.example.SevMerge.review;


import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // 전문가 리뷰 확인
    @GetMapping("/reviews/my")
    public String myReviews(HttpSession session, Model model,
                            @RequestParam(defaultValue = "1") int page) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "login-form";

        PageRequest pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<ReviewResponse.ReviewListDTO> reviewPage = reviewService.findMyReviewsPage(sessionUser.getId(), pageable);

        int totalPages = reviewPage.getTotalPages() == 0 ? 1 : reviewPage.getTotalPages();
        model.addAttribute("reviews", reviewPage.getContent());
        model.addAttribute("totalReviews", reviewPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < totalPages ? page + 1 : null);

        Double avgRaw = reviewService.avgRating(sessionUser.getId());
        model.addAttribute("avgStar", avgRaw != null ? String.format("%.1f", avgRaw) : "0.0");

        return "review/review-list";
    }

    // 리뷰작성 화면
    @GetMapping("/reviews/save")
    public String saveReviewForm(Model model, HttpSession session,
                                 @RequestParam(required = false) Long targetId,
                                 @RequestParam(required = false) Long projectId) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "login-form";

        Member targeter = memberService.findMemberById(targetId);

        model.addAttribute("targeter", targeter);
        model.addAttribute("reviewer", sessionUser);
        model.addAttribute("projectId", projectId);

        return "review/review-save";
    }


    // 리뷰 작성 후 저장
    @PostMapping("/reviews/save")
    public String saveReview(ReviewRequest.SaveReviewDTO reviewDTO, HttpSession session, Model model,
                             @RequestParam(required = false) Long targetId,
                             @RequestParam(required = false) Long projectId) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "login-form";

        reviewDTO.setProjectId(projectId);  // DTO에 projectId 세팅

        Member targeter = memberService.findMemberById(targetId);
        model.addAttribute("targeter", targeter);
        model.addAttribute("reviewer", sessionUser);

        Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
        reviewService.save(reviewDTO, member);

        return "redirect:/my-pages?tab=reviews";
    }

    // 리뷰상세 화면
    @GetMapping("/reviews/{reviewId}")
    public String reviewDetail(@PathVariable(name = "reviewId") Long reviewId, Model model, HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
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

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
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

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return ResponseEntity.status(401).build();
        }

        reviewService.updateReview(updateRequestDTO, reviewId, sessionUser.getId());

        return ResponseEntity.ok().build();
    }

    // 리뷰 삭제 기능
    @PostMapping("/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable(name = "reviewId") Long reviewId, HttpSession session) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return "login-form";
        }
        reviewService.deleteReview(reviewId,sessionUser.getId());
        // 삭제후 해당 전문가 리뷰 목록으로 돌아간다
        return "redirect:/my-pages?tab=reviews";

    }


}
