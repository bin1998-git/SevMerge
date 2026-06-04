package com.example.SevMerge.notification;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * GET  /notifications                    → 쪽지함 목록 페이지
 * POST /api/notifications/{id}/read      → 읽음 처리
 * POST /api/notifications/read-all       → 전체 읽음
 * GET  /api/notifications/unread-count   → 안읽은 개수 (헤더 뱃지용)
 */

@Controller
public class NotificationController {

    @GetMapping("/notifications")
    public String notificationPage() {
        return null;
    }

    @PostMapping("/api/notifications/{id}/read")
    public String readNotification(@PathVariable("id") Long memberId) {
        return null;
    }

//    @PostMapping("/api/notifications/read-all")
}
