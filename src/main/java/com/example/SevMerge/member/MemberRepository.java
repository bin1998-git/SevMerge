package com.example.SevMerge.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    // PENDING 상태 전문가 목록
    List<Member> findByRoleAndStatus(Role role, Status status);

    // 이름 또는 이메일로 검색 (관리자용)
    @Query("SELECT m FROM Member m WHERE m.name LIKE %:keyword% OR m.email LIKE %:keyword%")
    List<Member> searchByKeyword(@Param("keyword") String keyword);

    // 소셜 로그인 회원 조회 (provider + providerId 조합)
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
}