package com.example.SevMerge.refund;

import com.example.SevMerge.core.util.ApiResponse;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RefundApplicationController {

    private final RefundApplicationService refundApplicationService;

    //의뢰인 환불 신청
    @PostMapping("/refund-applications")
    public String apply(
            @RequestParam Long paymentId,
            @RequestParam String reasonCategory,
            @RequestParam String reason,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return "redirect:/login";

        try {
            refundApplicationService.apply(loginMember.getId(), paymentId, reasonCategory, reason);
            redirectAttrs.addFlashAttribute("successMsg", "환불 신청이 접수되었습니다. 관리자 검토 후 처리됩니다.");
            return "redirect:/my-pages?tab=refundHistory";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/refund-applications/form?paymentId=" + paymentId;
        }
    }

    // 관리자 전체/대기 목록
    @GetMapping("/admin/refund-applications")
    public String adminRefundList(@RequestParam(required = false) String status,
                                  HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null || !loginMember.isAdmin()) {
            return "redirect:/login";
        }

        List<RefundApplicationResponse> applications;
        if ("pending".equalsIgnoreCase(status)) {
            applications = refundApplicationService.getPendingApplications();
        } else {
            applications = refundApplicationService.getAllApplications();
        }

        model.addAttribute("applications", applications);
        model.addAttribute("isAdmin", true);
        return "admin/refund-list";
    }

    // 관리자 승인
    @PostMapping("/admin/refund-applications/{id}/approve")
    @ResponseBody
    public ResponseEntity<ApiResponse<RefundApplicationResponse>> approve(
            @PathVariable Long id,
            @RequestParam(required = false) String comment,
            HttpSession session) {

        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null || !loginMember.isAdmin()) {
            return ResponseEntity.status(403).body(ApiResponse.fail("관리자 권한이 필요합니다."));
        }

        try {
            RefundApplicationResponse response = refundApplicationService.approve(id, comment);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    // 관리자 거절
    @PostMapping("/admin/refund-applications/{id}/reject")
    @ResponseBody
    public ResponseEntity<ApiResponse<RefundApplicationResponse>> reject(
            @PathVariable Long id,
            @RequestParam String comment,
            HttpSession session) {

        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null || !loginMember.isAdmin()) {
            return ResponseEntity.status(403).body(ApiResponse.fail("관리자 권한이 필요합니다."));
        }

        try {
            RefundApplicationResponse response = refundApplicationService.reject(id, comment);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }

    // 사유 카테고리
    @GetMapping("/refund-applications/form")
    public String refundForm(@RequestParam Long paymentId, HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return "redirect:/login";

        model.addAttribute("paymentId", paymentId);
        return "refund/form";
    }
}