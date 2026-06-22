package com.example.SevMerge.notification;

import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.core.exception.UnauthorizedException;
import com.example.SevMerge.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationSseService notificationSseService;
    private final NotificationRepository notificationRepository;

    // 기능별 알림
    // 새 제안서 도착 -> 의뢰인
    @Transactional
    public void notifyNewBid(Member client, String expertName, String projectTitle, Long projectId) {
        notify(client, NotificationType.NEW_BID,
                expertName + " 전문가가 '" + projectTitle + "' 프로젝트에 제안서를 제출했습니다.", "/projects/" + projectId );
    }

    // 낙찰 -> 전문가
    @Transactional
    public void notifyBidSelected(Member expert, String projectTitle) {
        notify(expert, NotificationType.BID_SELECTED,
                expert.getName() + " 전문가님 축하합니다! \n'" + projectTitle + "' 프로젝트에 제출한 제안서가 낙찰 되었습니다! ",
                "/bids/my-orders" );
    }

    // 제안서 거절(탈락) → 전문가
    @Transactional
    public void notifyBidRejected(Member expert, String projectTitle) {
        notify(expert, NotificationType.BID_REJECTED,
                "'" + projectTitle + "' 프로젝트에 제출한 제안서가 선정되지 않았습니다.",
                "/bids/my-list");
    }

    // 결제(에스크로) 완료 → 전문가
    @Transactional
    public void notifyPaymentCompleted(Member client, Member expert, String projectTitle) {
        notify(expert, NotificationType.PAYMENT_COMPLETED,
                client.getName() + "님의 '" + projectTitle + "' 프로젝트 대금 결제가 완료되었습니다. \n작업을 시작하세요!",
                "/bids/my-orders");
    }

    // 전문가 승인 → 전문가
    @Transactional
    public void notifyExpertApproved(Member expert) {
        notify(expert, NotificationType.EXPERT_APPROVED,
                "전문가 신청이 승인되었습니다. \n지금 바로 활동을 시작해보세요!",
                "/experts/dashboard");
    }

    // 전문가 거절 → 전문가
    @Transactional
    public void notifyExpertRejected(Member expert) {
        notify(expert, NotificationType.EXPERT_REJECTED,
                expert.getName() + "전문가님 아쉽게도 전문가 신청이 거부되었습니다. \n자세한 내용은 고객센터로 문의해주세요.",
                "/");
    }

    // 새 쪽지 → 수신자
    @Transactional
    public void notifyMessageReceived(Member receiver, String senderName, String messageTitle) {
        notify(receiver, NotificationType.MESSAGE_RECEIVED,
                senderName + "님이 '" + messageTitle + "' 쪽지를 보냈습니다.\n쪽지함을 확인하세요.",
                "/messages");
    }

    // 새 채팅 → 수신자
    @Transactional
    public void notifyChatReceived(Member receiver, String senderName, Long roomId) {
        notify(receiver, NotificationType.CHAT_RECEIVED,
                senderName + "님이 채팅을 보냈습니다.",
                "/chat/room/" + roomId);
    }

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
    public void deleteNotification(Long id, Member sessionMember) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("알림을 찾을 수 없습니다."));

        if (!notification.getReceiver().getId().equals(sessionMember.getId())) {
            throw new UnauthorizedException("알림을 삭제할 권한이 없습니다.");
        }

        notification.delete();

    }
    @Transactional
    public void deleteAllNotifications(Member receiver) {
        notificationRepository.changeAllDeleted(receiver);
    }

    @Transactional
    public void notify(Member receiver, NotificationType type, String content, String url) {
        Notification saved = notificationRepository.save(Notification.builder()
                .receiver(receiver)
                .content(content)
                .type(type)
                .url(url)
                .build());

        NotificationResponse.ListDTO dto = new NotificationResponse.ListDTO(saved);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                notificationSseService.sendNotification(receiver.getId(), dto);
            }
        });
    }

    // 전문가 계정 정지 → 진행 중 프로젝트 의뢰인
    @Transactional
    public void notifyExpertSuspended(Member client, String expertName, String projectTitle) {
        notify(client, NotificationType.EXPERT_SUSPENDED,
                "'" + projectTitle + "' 프로젝트의 담당 전문가(" + expertName + ")가 정지되었습니다.\n관리자에게 문의해주세요.",
                "/supports");
    }


}
