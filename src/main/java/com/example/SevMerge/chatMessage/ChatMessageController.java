package com.example.SevMerge.chatMessage;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.core.util.FileUtil;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MemberRepository memberRepository;

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageRequest reqDTO, SimpMessageHeaderAccessor accessor) {
        SessionUser sessionUser = (SessionUser) accessor.getSessionAttributes().get(Define.SESSION_USER);
        if (sessionUser == null) return;
        Member sender = memberRepository.findById(sessionUser.getId()).orElse(null);
        if (sender == null) return;

        ChatMessageResponse resDTO = chatMessageService.saveMessage(reqDTO, sender);

        // 브로드 캐스트
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + reqDTO.getChatRoomId(), resDTO);
    }

    @PostMapping("/chat/upload")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam MultipartFile file, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null) return ResponseEntity.status(401).build();
        if (!FileUtil.isImageFile(file)) return ResponseEntity.badRequest().body(Map.of("message", "이미지 파일만 업로드 가능합니다"));
        try {
            String savedName = FileUtil.saveFile(file, FileUtil.IMAGES_DIR);
            return ResponseEntity.ok(Map.of("imageUrl", "/images/" + savedName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "업로드에 실패했습니다"));
        }
    }

    @MessageMapping("/chat/message/delete")
    public void deleteMessage(ChatMessageRequest reqDTO, SimpMessageHeaderAccessor accessor) {
        SessionUser sessionUser = (SessionUser) accessor.getSessionAttributes().get(Define.SESSION_USER);
        if (sessionUser == null) return;
        Member sender = memberRepository.findById(sessionUser.getId()).orElse(null);
        if (sender == null) return;

        chatMessageService.deleteMessage(reqDTO.getMessageId(), sender, true);

        simpMessagingTemplate.convertAndSend(
                "/sub/chat/room/" + reqDTO.getChatRoomId(),
                ChatMessageResponse.builder()
                        .type("DELETE")
                        .id(reqDTO.getMessageId())
                        .build()
        );
    }

}
