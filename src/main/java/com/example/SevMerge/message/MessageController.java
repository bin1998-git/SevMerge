package com.example.SevMerge.message;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/messages")
    public String messageList(@RequestParam(defaultValue = "received") String box,
                              @RequestParam(defaultValue = "1") int page,
                              Model model, HttpSession session) {

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);

        Page<MessageResponse.ListDTO> messagePage = messageService.findMessages(sessionMember, box, page);

        model.addAttribute("isReceived", box.equals("received"));
        model.addAttribute("isSent", box.equals("sent"));
        model.addAttribute("messages", messagePage.getContent());
        model.addAttribute("messageCount", messagePage.getTotalElements());
        model.addAttribute("totalPages", messagePage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("box", box);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < messagePage.getTotalPages() ? page + 1 : null);

        return "message/message-list";
    }

    @GetMapping("/messages/{id}")
    public String messageDetail(@PathVariable Long id, Model model, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        MessageResponse.DetailDTO message = messageService.findMessageByIdWithDetails(id, sessionMember);
        model.addAttribute("message", message);
        return "message/message-detail";
    }

    @GetMapping("/messages/send")
    public String messageSendPage(@RequestParam(required = false) Long receiverId,
                                  @RequestParam(required = false) Long projectId,
                                  Model model, HttpSession session) {

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        List<MessageResponse.ContactDTO> contacts = messageService.findContacts(sessionMember);

        model.addAttribute("contacts", contacts);
        model.addAttribute("preReceive", receiverId != null ? String.valueOf(receiverId) : "");
        model.addAttribute("preProjectId", projectId != null ? String.valueOf(projectId) : "");
        return "message/message-send";
    }
    @PostMapping("/messages/send")
    public String sendMessage(MessageRequest.SendDTO reqDTO, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        messageService.sendMessage(sessionMember, reqDTO);
        return "redirect:/messages?box=sent";
    }
}
