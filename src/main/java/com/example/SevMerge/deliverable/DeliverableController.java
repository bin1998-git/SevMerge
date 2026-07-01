package com.example.SevMerge.deliverable;

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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class DeliverableController {

    private final DeliverableService deliverableService;
    private final MemberRepository memberRepository;

    @PostMapping("/projects/{id}/deliverables")
    public String submit(@PathVariable("id") Long projectId,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam(value = "note", required = false) String note,
                         @RequestParam(value = "isFinal", defaultValue = "false") boolean isFinal,
                         HttpSession session) throws IOException {
        SessionUser user = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (user == null) return "redirect:/login";
        Member member = memberRepository.findById(user.getId()).orElseThrow();
        deliverableService.submit(projectId, file, note, isFinal, member);
        return "redirect:/bids/my-orders";
    }

    @PostMapping("/deliverables/{id}/revision")
    public String revision(@PathVariable("id") Long deliverableId,
                           @RequestParam("reason") String reason,
                           @RequestParam("projectId") Long projectId,
                           HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (user == null) return "redirect:/login";
        Member member = memberRepository.findById(user.getId()).orElseThrow();
        deliverableService.requestRevision(deliverableId, reason, member);
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/deliverables/{id}/approve")
    public String approve(@PathVariable("id") Long deliverableId,
                          @RequestParam("projectId") Long projectId,
                          HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (user == null) return "redirect:/login";
        Member member = memberRepository.findById(user.getId()).orElseThrow();
        deliverableService.approve(deliverableId, member);
        return "redirect:/projects/" + projectId;
    }
}
