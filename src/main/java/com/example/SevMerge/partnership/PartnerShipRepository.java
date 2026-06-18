package com.example.SevMerge.partnership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

public interface PartnerShipRepository extends JpaRepository<PartnerShip, Long> {



    // 현재 시각
    @Modifying
    @Query("""
        DELETE  FROM PartnerShip p WHERE p.deletedAt < :now AND p.deletedAt IS NOT NULL
    """)

    void deletedAtByTime(@Param("now") Timestamp now);
}
