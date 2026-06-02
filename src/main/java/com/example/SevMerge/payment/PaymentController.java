package com.example.SevMerge.payment;

import com.example.SevMerge.core.util.ApiResponse;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PaymentController
 * 요구사항: PAY-001 ~ PAY-006
 *
 * [인터셉터 적용 현황 - WebMvcConfig 기준]
 * - /payment/** : LoginInterceptor 적용 (로그인 필수)
 * - /admin/**   : AdminInterceptor 적용 (관리자 전용)
 * → 별도 인터셉터 추가 없이 현재 설정으로 동작합니다.
 *
 * [엔드포인트 목록]
 * GET  /payment/form          - PAY-001: 결제 페이지 (Mustache 뷰)
 * POST /payment/confirm       - PAY-003: 포트원 결제 완료 확인 (JSON)
 * POST /payment/{id}/settle   - PAY-004: 정산 처리 (JSON)
 * POST /payment/{id}/refund   - PAY-005: 환불 요청 (JSON)
 * GET  /payment/my            - PAY-006: 결제/정산 내역 페이지 (Mustache 뷰)
 * GET  /payment/project/{id}  - 프로젝트별 결제 조회 (JSON, 타 도메인 연동용)
 */
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ===================== PAY-001: 결제 페이지 =====================

    /**
     * PAY-001: 결제 페이지 진입
     * 낙찰 처리 후 팀장(BidController)에서 아래 URL로 리다이렉트:
     *   redirect:/payment/form?projectId={id}&expertId={id}&amount={금액}
     *
     * GET /payment/form?projectId=1&expertId=2&amount=500000
     */
    @GetMapping("/form")
    public String paymentForm(@RequestParam Long projectId,
                              @RequestParam Long expertId,
                              @RequestParam Integer amount,
                              HttpSession session,
                              Model model) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        String merchantUid = paymentService.generateMerchantUid(projectId);

        model.addAttribute("projectId",   projectId);
        model.addAttribute("expertId",    expertId);
        model.addAttribute("amount",      amount);
        model.addAttribute("merchantUid", merchantUid);
        model.addAttribute("clientId",    sessionUser.getId());

        // templates/payment/form.mustache
        return "payment/form";
    }

    // ===================== PAY-003: 결제 완료 확인 =====================

    /**
     * PAY-003: 포트원 결제 완료 후 프론트에서 호출 (AJAX)
     * 포트원 JS SDK → 결제 완료 → fetch("POST /payment/confirm", body)
     *
     * POST /payment/confirm
     * Body: { impUid, merchantUid, paidAmount, expertId, payMethod }
     */
    @PostMapping("/confirm")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @RequestBody @Valid PaymentRequest.ConfirmRequest req,
            HttpSession session) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");
        PaymentResponse response = paymentService.completePayment(sessionUser.getId(), req);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ===================== PAY-004: 정산 처리 =====================

    /**
     * PAY-004: 프로젝트 완료 확인 후 정산 요청
     * 의뢰인이 프로젝트 완료 버튼을 누르면 호출 (마이페이지 또는 프로젝트 상세)
     *
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
     * PAY-005: 환불 요청
     * 프로젝트 시작 전(PAID 상태)에만 전액 환불 가능
     *
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
     * 의뢰인 → 결제 내역, 전문가 → 정산 내역 구분 렌더링
     *
     * GET /payment/my
     */
    @GetMapping("/my")
    public String myPayments(HttpSession session, Model model) {

        Member sessionUser = (Member) session.getAttribute("sessionUser");

        if (sessionUser.isExpert()) {
            List<PaymentResponse> payments = paymentService.getExpertPayments(sessionUser.getId());
            model.addAttribute("payments", payments);
            model.addAttribute("isExpert", true);
        } else {
            List<PaymentResponse> payments = paymentService.getClientPayments(sessionUser.getId());
            model.addAttribute("payments", payments);
            model.addAttribute("isExpert", false);
        }

        // templates/payment/my.mustache
        return "payment/my";
    }

    // ===================== 타 도메인 연동용 (JSON) =====================

    /**
     * 프로젝트 ID로 결제 단건 조회 (JSON)
     * 팀장(ProjectController), 보조E(마이페이지) 등에서 호출 가능
     *
     * GET /payment/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    @ResponseBody
    public ResponseEntity<ApiResponse<PaymentResponse>> getByProject(
            @PathVariable Long projectId) {

        PaymentResponse response = paymentService.getByProjectId(projectId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
