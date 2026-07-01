package com.example.SevMerge.cancelrequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CancelRequestRepository extends JpaRepository<CancelRequest, Long> {

    boolean existsByProjectIdAndStatus(Long projectId, CancelStatus status);

    Optional<CancelRequest> findByProjectIdAndStatus(Long projectId, CancelStatus status);

    @Query("""
        SELECT cr FROM CancelRequest cr
        JOIN FETCH cr.project p
        JOIN FETCH cr.requester r
        WHERE cr.expert.id = :expertId AND cr.status = :status
        ORDER BY cr.id DESC
    """)
    List<CancelRequest> findPendingForExpert(Long expertId, CancelStatus status);
}
