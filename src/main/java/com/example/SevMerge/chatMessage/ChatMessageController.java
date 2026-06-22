package com.example.SevMerge.chatMessage;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageRequest reqDTO, SimpMessageHeaderAccessor accessor) {
        Member sender = (Member) accessor.getSessionAttributes().get(Define.SESSION_USER);
        if (sender == null) return;

        ChatMessageResponse resDTO = chatMessageService.saveMessage(reqDTO, sender);

        // 브로드 캐스트
        simpMessagingTemplate.convertAndSend("/sub/chat/room/" + reqDTO.getChatRoomId(), resDTO);
    }

}
