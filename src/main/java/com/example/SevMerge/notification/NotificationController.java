package com.example.SevMerge.notification;


import com.example.SevMerge.core.util.ApiResponse;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
    private final MemberRepository memberRepository;

    @GetMapping("/notifications")
    public String notificationPage(Model model, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        model.addAttribute("notifications", notificationService.findAllNotifications(member));
        return "notification/notification-list";
    }

    @PostMapping("/notifications/{id}/read")
    @ResponseBody
    public ApiResponse<?> readNotification(@PathVariable("id") Long notificationId, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        notificationService.markAsRead(notificationId, member);

        return ApiResponse.ok("알림을 읽음 처리 했습니다.");
    }

    @PostMapping("/notifications/read-all")
    public String readAll(HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        notificationService.changeAllRead(member);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/{id}/delete")
    @ResponseBody
    public ApiResponse<?> deleteNotification(@PathVariable("id") Long notificationId, HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        notificationService.deleteNotification(notificationId, member);
        return ApiResponse.ok("알림을 삭제했습니다.");
    }

    @PostMapping("/notifications/delete-all")
    public String deleteAll(HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        Member member = memberRepository.findById(sessionMember.getId()).orElseThrow();
        notificationService.deleteAllNotifications(member);
        return "redirect:/notifications";
    }

    @GetMapping(value = "/notifications/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createEmitter(HttpSession session) {
        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionMember == null) {
            SseEmitter emitter = new SseEmitter(0L);
            emitter.complete();
            return emitter;
        }
        return notificationSseService.createConnection(sessionMember.getId());
    }

}
