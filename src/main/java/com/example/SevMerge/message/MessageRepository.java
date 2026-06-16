package com.example.SevMerge.message;

import com.example.SevMerge.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message,Long> {

    // 쪽지 상세조회
    @Query("SELECT m FROM Message m JOIN FETCH m.sender JOIN FETCH m.receiver LEFT JOIN FETCH m.project WHERE m.id = :id")
    Optional<Message> findByIdWithDetails(@Param("id") Long id);

    // 쪽지 검색 기능
    @Query(value = """
           SELECT m FROM Message m JOIN FETCH m.sender JOIN FETCH m.receiver
           WHERE m.receiver = :receiver AND m.isDeletedByReceiver = false
           AND (:keyword IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
           """,
           countQuery = """
           SELECT COUNT(m) FROM Message m                                                                                                                                                                  \s
           WHERE m.receiver = :receiver AND m.isDeletedByReceiver = false                                                                                                                                  \s
           AND (:keyword IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
           """)
    Page<Message> findAllReceivedMessagesByPages(@Param("receiver") Member receiver,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);

    @Query(value = """                                                                                                                                                                                       
           SELECT m FROM Message m JOIN FETCH m.sender JOIN FETCH m.receiver
           WHERE m.sender = :sender AND m.isDeletedBySender = false
           AND (:keyword IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
           """,
           countQuery = """                                                                                                                                                                                 
           SELECT COUNT(m) FROM Message m
           WHERE m.sender = :sender AND m.isDeletedBySender = false
           AND (:keyword IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
           """)
    Page<Message> findAllSentMessagesByPages(@Param("sender") Member sender,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);

    // 마이페이지 메시지 카운트
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :receiver AND m.isRead = false AND m.isDeletedByReceiver = false")
    long countUnreadMessages(@Param("receiver") Member receiver);
}
