package com.example.SevMerge.notification;



import com.example.SevMerge.core.util.ApiResponse;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * GET  /notifications                    → 쪽지함 목록 페이지
 * POST /api/notifications/{id}/read      → 읽음 처리
 * POST /api/notifications/read-all       → 전체 읽음
 * GET  /api/notifications/unread-count   → 안읽은 개수 (헤더 뱃지용)
 */

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationSseService notificationSseService;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String notificationPage(Model model, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("notifications", notificationService.findAllNotifications(sessionMember));

        return "notification/notification-list";
    }

    @PostMapping("/notifications/{id}/read")
    @ResponseBody
    public ApiResponse<?> readNotification(@PathVariable("id") Long notificationId, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        notificationService.markAsRead(notificationId, sessionMember);

        return ApiResponse.ok("알림을 읽음 처리 했습니다.");
    }

    @PostMapping("/notifications/read-all")
    public String readAll(HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        notificationService.changeAllRead(sessionMember);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/{id}/delete")
    @ResponseBody
    public ApiResponse<?> deleteNotification(@PathVariable("id") Long notificationId, HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        notificationService.deleteNotification(notificationId, sessionMember);
        return ApiResponse.ok("알림을 삭제했습니다.");
    }

    @PostMapping("/notifications/delete-all")
    public String deleteAll(HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        notificationService.deleteAllNotifications(sessionMember);
        return "redirect:/notifications";
    }

    @GetMapping(value = "/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createEmitter(HttpSession session) {
        Member sessionMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null) {
            SseEmitter emitter = new SseEmitter(0L);
            emitter.complete();
            return emitter;
        }
        return notificationSseService.createConnection(sessionMember.getId());
    }

}
