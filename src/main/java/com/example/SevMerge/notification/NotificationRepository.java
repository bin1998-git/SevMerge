package com.example.SevMerge.notification;

import com.example.SevMerge.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림 리스트
    @Query("""
           SELECT n FROM Notification n WHERE n.receiver = :receiver ORDER BY n.createdAt DESC
           """)
    List<Notification> findAllNotificationDESC(@Param("receiver") Member receiver);

    // 읽지 않은 알림 갯수 조회
    @Query("""
           SELECT COUNT(n) FROM Notification n WHERE n.receiver = :receiver AND n.isRead = false
           """)
    Long countNotificationsByReceiver(@Param("receiver") Member receiver);

//    @Query("""
//           SELECT n FROM Notification n WHERE n.isRead = false
//           """)
//    List<Notification> findAllUnRead(Member receiver);

    // 전체 읽음 처리
    @Modifying(clearAutomatically = true)
    @Query("""
           UPDATE Notification n SET n.isRead = true WHERE n.receiver = :receiver AND n.isRead = false
           """)
    void changeAllRead(@Param("receiver") Member receiver);

}
