package com.example.SevMerge.deliverable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {

    @Query("SELECT d FROM Deliverable d WHERE d.project.id = :projectId ORDER BY d.round ASC")
    List<Deliverable> findByProjectIdOrderByRound(Long projectId);

    long countByProjectId(Long projectId);
}
