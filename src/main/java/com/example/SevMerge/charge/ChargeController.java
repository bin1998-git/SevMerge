package com.example.SevMerge.charge;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ChargeController — 지갑 충전
 *
 * [엔드포인트]
 * GET /charge/form     — 충전 페이지 (Toss SDK)
 * GET /charge/success  — Toss 결제 완료 콜백
 * GET /charge/fail     — Toss 결제 실패 콜백
 */
@Controller
@RequestMapping("/charge")
@RequiredArgsConstructor
public class ChargeController {

    private final ChargeService chargeService;

    @Value("${toss.client-key}")
    private String tossClientKey;

    /** 충전 폼 */
    @GetMapping("/form")
    public String chargeForm(HttpSession session, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";

        model.addAttribute("balance",   chargeService.getBalance(sessionUser.getId()));
        model.addAttribute("clientKey", tossClientKey);
        model.addAttribute("memberId",  sessionUser.getId());
        return "charge/form";
    }

    /** Toss 결제 성공 콜백 */
    @GetMapping("/success")
    public String chargeSuccess(@RequestParam String paymentKey,
                                @RequestParam String orderId,
                                @RequestParam Integer amount,
                                HttpSession session,
                                Model model) {

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";

        chargeService.confirmCharge(sessionUser.getId(), paymentKey, orderId, amount);

        // 세션 갱신 — balance가 바뀌었으므로 재조회
        model.addAttribute("newBalance", chargeService.getBalance(sessionUser.getId()));
        model.addAttribute("amount", amount);
        return "charge/success";
    }

    /** Toss 결제 실패 콜백 */
    @GetMapping("/fail")
    public String chargeFail(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("errorMessage", message);
        return "charge/fail";
    }
}
