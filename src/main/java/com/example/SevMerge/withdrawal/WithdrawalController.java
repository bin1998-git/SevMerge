package com.example.SevMerge.withdrawal;

import com.example.SevMerge.charge.ChargeService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WithdrawalController — 전문가 출금
 *
 * [엔드포인트]
 * GET  /withdrawal/form     — 출금 신청 폼 (전문가 전용)
 * GET  /withdrawal/history  — 출금내역 목록 (전문가 전용)
 * POST /withdrawal/request  — 출금 신청 처리
 */
@Slf4j
@Controller
@RequestMapping("/withdrawal")
@RequiredArgsConstructor
public class WithdrawalController {

    private final ChargeService chargeService;
    private final WithdrawalService withdrawalService;

    /** 출금 신청 폼 */
    @GetMapping("/form")
    public String withdrawalForm(HttpSession session, Model model) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        model.addAttribute("balance", chargeService.getBalance(sessionUser.getId()));
        return "withdrawal/form";
    }

    /** 출금내역 */
    @GetMapping("/history")
    public String withdrawalHistory(HttpSession session, Model model) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        WithdrawalService.HistoryResult result =
                withdrawalService.getHistory(sessionUser.getId());

        model.addAttribute("withdrawals",  result.withdrawals());
        model.addAttribute("totalCount",   result.totalCount());
        model.addAttribute("totalAmount",  result.totalAmount());
        model.addAttribute("balance",      chargeService.getBalance(sessionUser.getId()));
        return "withdrawal/history";
    }

    /** 출금 신청 처리 */
    @PostMapping("/request")
    public String withdrawalRequest(@RequestParam String    bankName,
                                    @RequestParam String    accountNumber,
                                    @RequestParam String    accountHolder,
                                    @RequestParam Integer   amount,
                                    HttpSession             session,
                                    RedirectAttributes      redirectAttrs) {

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) return "redirect:/login";
        if (!sessionUser.isExpert()) return "redirect:/";

        try {
            withdrawalService.requestWithdrawal(
                    sessionUser.getId(), bankName, accountNumber, accountHolder, amount);
            redirectAttrs.addFlashAttribute("successMsg",
                    amount + "원 출금 신청이 완료되었습니다. 영업일 1~3일 내에 입금됩니다.");
            return "redirect:/withdrawal/history";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/withdrawal/form";
        }
    }
}
