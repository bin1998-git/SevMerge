package com.example.SevMerge.payment;

import com.example.SevMerge.charge.ChargeService;
import com.example.SevMerge.core.util.ApiResponse;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * PaymentController — 에스크로 결제
 *
 * [엔드포인트]
 * GET  /payment/form              — 에스크로 계약 페이지 (잔액 확인)
 * POST /payment/escrow            — 에스크로 생성 (잔액 차감)
 * POST /payment/{id}/settle       — 정산 처리 (완료 확인)
 * POST /payment/{id}/refund       — 환불 요청
 * GET  /payment/my                — 결제/정산 내역
 * GET  /payment/project/{id}      — 프로젝트별 결제 조회 (JSON)
 */
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ChargeService  chargeService;

    // ── 에스크로 계약 페이지 ──

    /**
     * 낙찰 확정 후 이동하는 에스크로 계약 페이지
     * redirect:/payment/form?projectId=..&expertId=..&amount=..
     */
    @GetMapping("/form")
    public String paymentForm(@RequestParam Long projectId,
                              @RequestParam Long expertId,
                              @RequestParam Integer amount,
                              HttpSession session,
                              Model model) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        if (sessionUser == null) return "redirect:/login";

        int balance = chargeService.getBalance(sessionUser.getId());

        model.addAttribute("projectId",        projectId);
        model.addAttribute("expertId",         expertId);
        model.addAttribute("amount",           amount);
        model.addAttribute("balance",          balance);
        model.addAttribute("platformFee",      (int)(amount * 0.03));
        model.addAttribute("netAmount",        amount - (int)(amount * 0.03));
        model.addAttribute("balanceSufficient",   balance >= amount);
        model.addAttribute("balanceInsufficient", balance < amount);
        model.addAttribute("shortage",            Math.max(0, amount - balance));

        return "payment/form";
    }

    // ── 에스크로 생성 ──

    /**
     * POST /payment/escrow
     * 잔액에서 차감 후 에스크로 레코드 생성
     */
    @PostMapping("/escrow")
    public String createEscrow(@RequestParam Long projectId,
                               @RequestParam Long expertId,
                               @RequestParam Integer amount,
                               HttpSession session) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        if (sessionUser == null) return "redirect:/login";

        paymentService.createEscrow(sessionUser.getId(), projectId, expertId, amount);
        return "redirect:/payment/my";
    }

    // ── 정산 처리 ──

    @PostMapping("/{id}/settle")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> settle(
            @PathVariable Long id,
            HttpSession session) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        if (sessionUser == null)
            return ResponseEntity.status(401).body(ApiResponse.fail("로그인이 필요합니다."));

        PaymentResponse response = paymentService.settle(id, sessionUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── 환불 요청 ──

    @PostMapping("/{id}/refund")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(
            @PathVariable Long id,
            HttpSession session) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        if (sessionUser == null)
            return ResponseEntity.status(401).body(ApiResponse.fail("로그인이 필요합니다."));

        PaymentResponse response = paymentService.refund(id, sessionUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── 내역 조회 ──

    @GetMapping("/my")
    public String myPayments(HttpSession session, Model model) {
        Member sessionUser = (Member) session.getAttribute("sessionUser");
        if (sessionUser == null) return "redirect:/login";

        int balance = chargeService.getBalance(sessionUser.getId());
        model.addAttribute("balance", balance);

        if (sessionUser.isExpert()) {
            model.addAttribute("payments", paymentService.getExpertPayments(sessionUser.getId()));
            model.addAttribute("isExpert", true);
        } else {
            model.addAttribute("payments", paymentService.getClientPayments(sessionUser.getId()));
            model.addAttribute("isExpert", false);
        }

        return "payment/my";
    }

    // ── 타 도메인 연동용 ──

    @GetMapping("/project/{projectId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> getByProject(
            @PathVariable Long projectId) {

        PaymentResponse response = paymentService.getByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
