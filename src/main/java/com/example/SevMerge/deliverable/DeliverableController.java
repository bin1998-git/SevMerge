package com.example.SevMerge.deliverable;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
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

    @PostMapping("/projects/{id}/deliverables")
    public String submit(@PathVariable("id") Long projectId,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam(value = "note", required = false) String note,
                         @RequestParam(value = "isFinal", defaultValue = "false") boolean isFinal,
                         HttpSession session) throws IOException {
        Member user = (Member) session.getAttribute(Define.SESSION_USER);
        if (user == null) return "redirect:/login";
        deliverableService.submit(projectId, file, note, isFinal, user);
        return "redirect:/bids/my-orders";
    }

    @PostMapping("/deliverables/{id}/revision")
    public String revision(@PathVariable("id") Long deliverableId,
                           @RequestParam("reason") String reason,
                           @RequestParam("projectId") Long projectId,
                           HttpSession session) {
        Member user = (Member) session.getAttribute(Define.SESSION_USER);
        if (user == null) return "redirect:/login";
        deliverableService.requestRevision(deliverableId, reason, user);
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/deliverables/{id}/approve")
    public String approve(@PathVariable("id") Long deliverableId,
                          @RequestParam("projectId") Long projectId,
                          HttpSession session) {
        Member user = (Member) session.getAttribute(Define.SESSION_USER);
        if (user == null) return "redirect:/login";
        deliverableService.approve(deliverableId, user);
        return "redirect:/projects/" + projectId;
    }
}
