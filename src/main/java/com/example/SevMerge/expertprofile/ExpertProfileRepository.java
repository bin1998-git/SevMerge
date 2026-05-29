package com.example.SevMerge.expertprofile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpertProfileRepository extends JpaRepository<ExpertProfile, Long> {

    Optional<ExpertProfile> findByMemberId(Long memberId);

    // 인증 전문가 목록 (입찰 필터링용)
    List<ExpertProfile> findByIsCertifiedTrue();
}