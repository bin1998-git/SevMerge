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
        // [수정 1] and 앞에 공백을 반드시 넣어주세요!
        StringBuilder jpql = new StringBuilder("select p from Project p where 1=1 ");

        if (keyword != null && !keyword.isBlank()) {
            jpql.append("and p.title like :keyword ");
        }
        if (category != null && !category.isBlank()) {
            jpql.append("and p.category = :category ");
        }
        if (statusFilter != null && "CLOSED".equals(statusFilter)) {
            jpql.append("and p.projectStatus = :status ");
        }
        if (bidFilter != null && !bidFilter.isBlank()) {
            jpql.append("and p.bidFilter = :bidFilter ");
        }

        TypedQuery<Project> query = em.createQuery(jpql.toString(), Project.class);

        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        if (category != null && !category.isBlank()) {
            query.setParameter("category", Category.valueOf(category));
        }
        if (statusFilter != null && "CLOSED".equals(statusFilter)) {
            query.setParameter("status", ProjectStatus.CLOSED);
        }
        if (bidFilter != null && !bidFilter.isBlank()) {
            // [수정 2] "bidfilter" -> "bidFilter"로 수정
            query.setParameter("bidFilter", BidFilter.valueOf(bidFilter));
        }
        return query.getResultList();
    }
}
