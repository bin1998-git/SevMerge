package com.example.SevMerge.cancelrequest;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CancelRequestController {

    private final CancelRequestService cancelRequestService;
    private final MemberRepository memberRepository;

    @PostMapping("/projects/{id}/cancel-request")
    public String request(@PathVariable("id") Long projectId,
                          @RequestParam("reason") String reason,
                          HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (user == null) return "redirect:/login";
        try {
            Member member = memberRepository.findById(user.getId()).orElseThrow();
            cancelRequestService.requestCancel(projectId, member, reason);
        } catch (BadRequestException e) {
        }
        return "redirect:/my-pages?tab=projects";
    }

    @PostMapping("/cancel-requests/{id}/approve")
    public String approve(@PathVariable("id") Long requestId,
                          HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (user == null) return "redirect:/login";
        Member member = memberRepository.findById(user.getId()).orElseThrow();
        cancelRequestService.approveCancel(requestId, member);
        return "redirect:/bids/my-orders";
    }
}
