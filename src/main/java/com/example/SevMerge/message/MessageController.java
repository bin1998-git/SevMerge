package com.example.SevMerge.message;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MemberRepository memberRepository;

    @GetMapping("/messages")
    public String messageList(@RequestParam(defaultValue = "received") String box,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "desc") String sort,
                              @RequestParam(required = false) String keyword,
                              Model model, HttpSession session) {

        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        Page<MessageResponse.ListDTO> messagePage = messageService.findMessages(member, box, page, sort, keyword);

        model.addAttribute("isReceived", box.equals("received"));
        model.addAttribute("isSent", box.equals("sent"));
        model.addAttribute("messages", messagePage.getContent());
        model.addAttribute("messageCount", messagePage.getTotalElements());
        model.addAttribute("totalPages", messagePage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("box", box);
        model.addAttribute("sort", sort);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("isSortAsc", "asc".equalsIgnoreCase(sort));
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < messagePage.getTotalPages() ? page + 1 : null);

        return "message/message-list";
    }

    @GetMapping("/messages/{id}")
    public String messageDetail(@PathVariable Long id, Model model, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        MessageResponse.DetailDTO message = messageService.findMessageByIdWithDetails(id, member);
        model.addAttribute("message", message);
        return "message/message-detail";
    }

    @GetMapping("/messages/send")
    public String messageSendPage(@RequestParam(required = false) Long receiverId,
                                  @RequestParam(required = false) Long projectId,
                                  Model model, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        List<MessageResponse.ContactDTO> contacts = messageService.findContacts(member);

        model.addAttribute("contacts", contacts);
        model.addAttribute("preReceive", receiverId != null ? String.valueOf(receiverId) : "");
        model.addAttribute("preProjectId", projectId != null ? String.valueOf(projectId) : "");
        return "message/message-send";
    }
    @PostMapping("/messages/send")
    public String sendMessage(MessageRequest.SendDTO reqDTO, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        messageService.sendMessage(member, reqDTO);
        return "redirect:/messages?box=sent";
    }

    @PostMapping("/messages/{id}/delete")
    public String deleteMessage(@PathVariable Long id, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        boolean isSenderDelete = messageService.deleteMessage(id, member);
        return isSenderDelete ? "redirect:/messages?box=sent" : "redirect:/messages?box=received";
    }

    @GetMapping("/messages/files/{messageFilesId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long messageFilesId, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        return messageService.downloadFile(messageFilesId, member);

    }

}
