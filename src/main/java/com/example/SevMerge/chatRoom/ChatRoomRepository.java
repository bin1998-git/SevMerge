package com.example.SevMerge.chatRoom;

import com.example.SevMerge.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 참여중인 채팅방 목록 조회 (내가 삭제한 방은 제외)
    @Query("""
           SELECT cr FROM ChatRoom cr
           WHERE (cr.client = :member AND cr.deletedByClient = false)
              OR (cr.expert = :member AND cr.deletedByExpert = false)
           ORDER BY cr.createdAt DESC
           """)
    List<ChatRoom> findAllByMember(@Param("member")Member member);

    // 같은 채팅방이 존재하는지 확인 용도
    @Query("""
           SELECT cr FROM ChatRoom cr
           WHERE cr.project.id = :projectId AND cr.client.id = :clientId AND cr.expert.id = :expertId
           """)
    Optional<ChatRoom> findByProjectAndMember(@Param("projectId") Long projectId,
                                              @Param("clientId") Long clientId,
                                              @Param("expertId") Long expertId);

    // project가 null인 프로필방용 메서드
    @Query("""                                                                                                                                                                                                   
           SELECT cr FROM ChatRoom cr
           WHERE cr.project IS NULL AND cr.client.id = :clientId AND cr.expert.id = :expertId
           """)
    Optional<ChatRoom> findProfileRoom(@Param("clientId") Long clientId, @Param("expertId") Long expertId);
}
