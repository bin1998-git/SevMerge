package com.example.SevMerge.notification;

import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.core.exception.UnauthorizedException;
import com.example.SevMerge.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    // 알림 리스트 반환
    public List<NotificationResponse.ListDTO> findAllNotifications(Member receiver) {
        List<Notification> notifications = notificationRepository.findAllNotificationDESC(receiver);
        List<NotificationResponse.ListDTO> responseDTOList = notifications.stream()
                .map(NotificationResponse.ListDTO::new).toList();

        return responseDTOList;
    }

    // 안 읽은 알림 갯수 반환
    public long countUnRead(Member receiver) {
        return notificationRepository.countNotificationsByReceiver(receiver);
    }

    // 메세지 읽음 처리 (단건)
    @Transactional
    public void markAsRead(Long id, Member sessionMember) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("알림을 찾을 수 없습니다."));

        if (!notification.getReceiver().getId().equals(sessionMember.getId())) {
            throw new UnauthorizedException("알림을 읽을 권한이 없습니다.");
        }
        notification.read();
    }

    // 전체 읽음 처리
    @Transactional
    public void changeAllRead(Member receiver) {
        notificationRepository.changeAllRead(receiver);
    }

    @Transactional
    public void notify(Member receiver, NotificationType type, String content, String url) {
        notificationRepository.save(Notification.builder()
                        .receiver(receiver)
                        .content(content)
                        .type(type)
                        .url(url)
                        .build());
    }

    @Transactional
    public void deleteNotification(Long id, Member sessionMember) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("알림을 찾을 수 없습니다."));

        if (!notification.getReceiver().getId().equals(sessionMember.getId())) {
            throw new UnauthorizedException("알림을 삭제할 권한이 없습니다.");
        }

        notification.delete();

    }

    public void deleteAllNotifications(Member receiver) {
        notificationRepository.deleteAll();
    }

}
