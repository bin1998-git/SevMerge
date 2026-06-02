//package com.example.SevMerge.chatRoom;
//
//import com.example.SevMerge.core.exception.UnauthorizedException;
//import com.example.SevMerge.core.util.ApiResponse;
//import com.example.SevMerge.member.Member;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//public class ChatRoomController {
//
//    private final ChatRoomService chatRoomService;
//
//    // 채팅방 페이지 렌더링
//    // http://localhost:8080/chat/room/{roomId}
//    @GetMapping("/chat/room/{roomId}")
//    public String chatRoomPage(@PathVariable Long roomId, HttpSession session, Model model) {
//
//        Member sessionMember = (Member) session.getAttribute("sessionMember");
//        if (sessionMember == null) {
//            return "redirect:/member/login";
//        }
//
//        // 권한 검증은 서비스에서 처리
//        ChatRoomResponse room = chatRoomService.getRoom(roomId, sessionMember);
//
//        model.addAttribute("roomId", roomId);
//        model.addAttribute("loginUserId", sessionMember.getId());
//        model.addAttribute("loginUserName", sessionMember.getName());
//        model.addAttribute("room", room);
//
//        return "chatRoom/chat";
//    }
//
//    // 채팅방 생성
//    // → POST /api/chat/rooms
//    // → 낙찰 처리 시 BidService에서 직접 호출하거나 이 API 사용
//    @PostMapping("/api/chat/rooms")
//    @ResponseBody
//    public ResponseEntity<ApiResponse<ChatRoomResponse>> createRoom(
//            @RequestBody ChatRoomRequest request,
//            HttpSession session) {
//
//        Member loginUser = (Member) session.getAttribute("sessionUser");
//        if (loginUser == null) {
//            throw new UnauthorizedException("로그인이 필요합니다.");
//        }
//
//        return ResponseEntity.ok(
//                ApiResponse.ok(chatRoomService.createRoom(request))
//        );
//    }
//
//}
