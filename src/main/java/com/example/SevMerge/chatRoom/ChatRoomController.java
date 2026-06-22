package com.example.SevMerge.chatRoom;

import com.example.SevMerge.chatMessage.ChatMessageService;
import com.example.SevMerge.core.exception.UnauthorizedException;
import com.example.SevMerge.core.util.ApiResponse;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    @GetMapping("/chat/room")
    public String showChatRoom(HttpSession session, Model model) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("rooms", chatRoomService.findMyRooms(sessionMember));
        return "chatRoom/chatRoom-list";
    }

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    // 채팅방 페이지 렌더링
    // http://localhost:8080/chat/room/{roomId}
    @GetMapping("/chat/room/{roomId}")
    public String chatRoomPage(@PathVariable Long roomId, HttpSession session, Model model) {

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null) {
            return "redirect:/login";
        }

        // 권한 검증은 서비스에서 처리
        ChatRoomResponse room = chatRoomService.getRoom(roomId, sessionMember);

        // 입장 시 받은 메시지 읽음 처리
        chatMessageService.markRoomAsRead(roomId, sessionMember);

        // 채팅방 표시 이름: 프로젝트 방이면 프로젝트 제목, 프로필 방이면 상대방 이름
        String roomName;
        if (room.getProjectTitle() != null) {
            roomName = room.getProjectTitle();
        } else {
            roomName = sessionMember.getId().equals(room.getClientId())
                    ? room.getExpertName()   // 내가 의뢰인 → 상대 전문가 이름
                    : room.getClientName();  // 내가 전문가 → 상대 의뢰인 이름
        }

        model.addAttribute("roomId", roomId);
        model.addAttribute("roomName", roomName);
        model.addAttribute("loginUserId", sessionMember.getId());
        model.addAttribute("loginUserName", sessionMember.getName());
        model.addAttribute("room", room);
        model.addAttribute("rooms", chatRoomService.findMyRooms(sessionMember));      // 사이드바 목록
        model.addAttribute("messages", chatMessageService.getRoomMessages(roomId, sessionMember)); // 과거 메시지

        return "chatRoom/chat";
    }

    // 채팅방 생성
    // → POST /api/chat/rooms
    // → 낙찰 처리 시 BidService에서 직접 호출하거나 이 API 사용
    @PostMapping("/api/chat/rooms")
    @ResponseBody
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(
            @RequestBody ChatRoomRequest request,
            HttpSession session) {

        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        return ResponseEntity.ok(
                ApiResponse.ok(chatRoomService.createChatRoom(request))
        );
    }

    // 전문가 프로필에서 : 의뢰인이 문의
    @GetMapping("/chat/start/expert/{expertId}")
    public String startProfileChat(@PathVariable Long expertId, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        return "redirect:/chat/room/" + chatRoomService.getOrCreateProfileRoom(sessionMember, expertId);
    }

    // 프로젝트 문의 (양방향) : 상대 회원 id 전달
    @GetMapping("/chat/start/project/{projectId}")
    public String startProjectChat(@PathVariable Long projectId,
                                   @RequestParam Long withMemberId, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        return "redirect:/chat/room/" + chatRoomService.getOrCreateProjectRoom(projectId, sessionMember, withMemberId);
    }

    // 채팅방 삭제 (나에게서, 양쪽 다 삭제 시 하드딜리트)
    @PostMapping("/chat/room/{roomId}/delete")
    public String deleteRoom(@PathVariable Long roomId, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        chatRoomService.deleteRoom(roomId, sessionMember);
        return "redirect:/chat/room";
    }

    // 메시지 삭제 (forAll=true: 모두에게서, false: 나에게서)
    @PostMapping("/chat/messages/{messageId}/delete")
    @ResponseBody
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId,
                                              @RequestParam(defaultValue = "false") boolean forAll,
                                              HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        chatMessageService.deleteMessage(messageId, sessionMember, forAll);
        return ResponseEntity.ok().build();
    }

}
