package com.example.SevMerge.advertisement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    @Query("SELECT a FROM Advertisement a " +
            "WHERE a.placement = :placement " +
            "AND a.status = 'ACTIVE' " +
            "AND a.startDate <= :now AND a.endDate >= :now " +
            "ORDER BY a.createdAt DESC")
    List<Advertisement> findActiveByPlacement(@Param("placement") AdvertisementPlacement placement,
                                              @Param("now") Timestamp now);

    List<Advertisement> findByExpertIdOrderByCreatedAtDesc(Long expertId);

    @Query("SELECT a FROM Advertisement a WHERE a.status = 'ACTIVE' AND a.endDate < :now")
    List<Advertisement> findExpiredButStillActive(@Param("now") Timestamp now);
}