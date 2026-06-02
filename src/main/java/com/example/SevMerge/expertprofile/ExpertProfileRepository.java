package com.example.SevMerge.expertprofile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExpertProfileRepository extends JpaRepository<ExpertProfile, Long> {

    @Query("""
    SELECT e FROM ExpertProfile e
    WHERE e.member.role = 'EXPERT'
""")
    List<ExpertProfile> findByExpert();
        // member_id로 전문가 프로필 조회
    Optional<ExpertProfile> findByMemberId(Long memberId);

    // 전문가 프로필 존재 여부 확인
    boolean existsByMemberId(Long memberId);
}
