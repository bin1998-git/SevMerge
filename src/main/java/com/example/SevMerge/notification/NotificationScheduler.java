package com.example.SevMerge.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;

    // 매일 새벽 4시 (초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void cleanUpOldNotifications() {
        Timestamp threshold = Timestamp.valueOf(LocalDateTime.now().minusDays(30));
        int deleted = notificationRepository.hardDeleteOldNotifications(threshold);
        log.info("[알림 정리] {}건 물리 삭제", deleted);
    }

}
