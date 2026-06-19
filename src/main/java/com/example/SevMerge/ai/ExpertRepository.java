package com.example.SevMerge.ai;

import com.example.SevMerge.expertprofile.ExpertProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertRepository extends JpaRepository<ExpertProfile, Long> {

    // 인기 전문가 조 (등급이 높은 순)

}
