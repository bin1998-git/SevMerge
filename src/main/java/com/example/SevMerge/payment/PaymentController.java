package com.example.SevMerge.payment;

import com.example.SevMerge.core.util.ApiResponse;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PaymentController
 * 요구사항: PAY-001 ~ PAY-006
 *
 * [엔드포인트 목록]
 * GET  /payment/form              - PAY-001: 결제 페이지 (Mustache 뷰)
 * GET  /payment/success           - PAY-003: 토스 결제 완료 콜백
 * GET  /payment/fail              - PAY-003: 토스 결제 실패 콜백
 * POST /payment/{id}/settle       - PAY-004: 정산 처리 (JSON)
 * POST /payment/{id}/refund       - PAY-005: 환불 요청 (JSON)
 * GET  /payment/my                - PAY-006: 결제/정산 내역 페이지 (Mustache 뷰)
 * GET  /payment/project/{id}      - 프로젝트별 결제 조회 (JSON, 타 도메인 연동용)
 */
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${toss.client-key}")
    private String tossClientKey;

    // ===================== PAY-001: 결제 페이지 =====================

    /**
     * PAY-001: 결제 페이지 진입
     * 낙찰 처리 후 BidController에서 아래 URL로 리다이렉트:
     *   redirect:/payment/form?projectId={id}&expertId={id}&amount={금액}
     */
    @GetMapping("/form")
    public String paymentForm(@RequestParam Long projectId,
                              @RequestParam Long expertId,
                              @RequestParam Integer amount,
                              HttpSession session,
                              Model model) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        String orderId = "sev-project-" + projectId;

        model.addAttribute("projectId",  projectId);
        model.addAttribute("expertId",   expertId);
        model.addAttribute("amount",     amount);
        model.addAttribute("orderId",    orderId);
        model.addAttribute("clientId",   sessionUser.getId());
        model.addAttribute("clientKey",  tossClientKey);

        return "payment/form";
    }

    // ===================== PAY-003: 토스 결제 콜백 =====================

    /**
     * PAY-003: 토스 결제 성공 콜백
     * 토스 JS SDK 결제 완료 → successUrl(GET)로 리다이렉트
     * GET /payment/success?paymentKey=...&orderId=...&amount=...&expertId=...
     */
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam Integer amount,
                                 @RequestParam Long expertId,
                                 @RequestParam(required = false) String method,
                                 HttpSession session) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        paymentService.confirmTossPayment(
                sessionUser.getId(), paymentKey, orderId, amount, expertId, method);
        return "redirect:/payment/my";
    }

    /**
     * PAY-003: 토스 결제 실패 콜백
     * GET /payment/fail?code=...&message=...
     */
    @GetMapping("/fail")
    public String paymentFail(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("errorMessage", message);
        return "payment/fail";
    }

    // ===================== PAY-004: 정산 처리 =====================

    /**
     * PAY-004: 의뢰인이 프로젝트 완료 버튼 클릭 시 호출
     * POST /payment/{id}/settle
     */
    @PostMapping("/{id}/settle")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> settle(
            @PathVariable Long id,
            HttpSession session) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        PaymentResponse response = paymentService.settle(id, sessionUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ===================== PAY-005: 환불 요청 =====================

    /**
     * PAY-005: 환불 요청 (PAID 상태에서만 가능)
     * POST /payment/{id}/refund
     */
    @PostMapping("/{id}/refund")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(
            @PathVariable Long id,
            HttpSession session) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        PaymentResponse response = paymentService.refund(id, sessionUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ===================== PAY-006: 결제/정산 내역 조회 =====================

    /**
     * PAY-006: 결제/정산 내역 페이지
     * 의뢰인 → 결제 내역 / 전문가 → 정산 내역
     */
    @GetMapping("/my")
    public String myPayments(HttpSession session, Model model) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");

        if (sessionUser.isExpert()) {
            model.addAttribute("payments", paymentService.getExpertPayments(sessionUser.getId()));
            model.addAttribute("isExpert", true);
        } else {
            model.addAttribute("payments", paymentService.getClientPayments(sessionUser.getId()));
            model.addAttribute("isExpert", false);
        }

        return "payment/my";
    }

    // ===================== 타 도메인 연동용 =====================

    /**
     * 프로젝트 ID로 결제 단건 조회 (JSON)
     */
    @GetMapping("/project/{projectId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> getByProject(
            @PathVariable Long projectId) {

        PaymentResponse response = paymentService.getByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
