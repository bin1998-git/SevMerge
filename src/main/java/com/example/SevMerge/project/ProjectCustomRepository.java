package com.example.SevMerge.project;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class ProjectCustomRepository {

    private final EntityManager em;

    public List<Project> findByFilters(String keyword, String category, String statusFilter, String bidFilter) {
        // [수정] 1=1 이후 공백 추가하여 문법 오류 방지
        StringBuilder jpql = new StringBuilder("select p from Project p where p.isDeleted = false ");

        if (keyword != null && !keyword.isBlank()) {
            jpql.append("and p.title like :keyword ");
        }
        if (category != null && !category.isBlank()) {
            jpql.append("and p.category = :category ");
        }

        // [핵심] 낙찰 완료 건(CLOSED) 선택 시, CLOSED 상태와 DONE 상태를 모두 조회
        if (statusFilter != null && "CLOSED".equals(statusFilter)) {
            jpql.append("and p.projectStatus IN (:statusList) ");
        }

        if (bidFilter != null && !bidFilter.isBlank()) {
            jpql.append("and p.bidFilter = :bidFilter ");
        }

        TypedQuery<Project> query = em.createQuery(jpql.toString(), Project.class);

        // 파라미터 세팅
        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        if (category != null && !category.isBlank()) {
            query.setParameter("category", Category.valueOf(category));
        }

        // CLOSED와 DONE을 리스트로 넘겨서 처리 COMPLETED추가
        if (statusFilter != null && "CLOSED".equals(statusFilter)) {
            query.setParameter("statusList", List.of(ProjectStatus.IN_PROGRESS, ProjectStatus.COMPLETED, ProjectStatus.DONE));
        }

        if (bidFilter != null && !bidFilter.isBlank()) {
            query.setParameter("bidFilter", BidFilter.valueOf(bidFilter));
        }

        return query.getResultList();
    }
}
