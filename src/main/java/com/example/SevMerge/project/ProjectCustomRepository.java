package com.example.SevMerge.project;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class ProjectCustomRepository {

    private final EntityManager em;

    public Page<Project> findByFilters(String keyword, String category, String statusFilter, String bidFilter, Pageable pageable) {

        // 1. WHERE 절과 파라미터 세팅 로직을 활용해 쿼리 조건 문자열 생성
        String whereClause = buildWhereClause(keyword, category, statusFilter, bidFilter);
        String orderBy = " ORDER BY p.createdAt DESC";

        // 2. 데이터 조회 쿼리 생성
        TypedQuery<Project> query = em.createQuery(
                "SELECT p FROM Project p " + whereClause + orderBy, Project.class);
        setParameters(query, keyword, category, statusFilter, bidFilter);

        // [핵심] 페이징 적용 (offset: 시작점, limit: 개수)
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Project> content = query.getResultList();

        // 3. 전체 개수 조회 (페이징 버튼 생성을 위해 필수)
        TypedQuery<Long> countQuery = em.createQuery(
                "SELECT COUNT(p) FROM Project p " + whereClause, Long.class);
        setParameters(countQuery, keyword, category, statusFilter, bidFilter);

        long total = countQuery.getSingleResult();

        // 4. PageImpl 객체로 반환
        return new PageImpl<>(content, pageable, total);
    }

    // 조건절을 만드는 공통 메서드
    private String buildWhereClause(String keyword, String category, String statusFilter, String bidFilter) {
        StringBuilder where = new StringBuilder(" WHERE p.isDeleted = false ");
        if (keyword != null && !keyword.isBlank()) where.append(" AND p.title LIKE :keyword ");
        if (category != null && !category.isBlank()) where.append(" AND p.category = :category ");
        if (statusFilter != null && "CLOSED".equals(statusFilter)) where.append(" AND p.projectStatus IN (:statusList) ");
        if (bidFilter != null && !bidFilter.isBlank()) where.append(" AND p.bidFilter = :bidFilter ");
        return where.toString();
    }

    // 파라미터를 세팅하는 공통 메서드
    private void setParameters(jakarta.persistence.Query query, String keyword, String category, String statusFilter, String bidFilter) {
        if (keyword != null && !keyword.isBlank()) query.setParameter("keyword", "%" + keyword + "%");
        if (category != null && !category.isBlank()) query.setParameter("category", Category.valueOf(category));
        if (statusFilter != null && "CLOSED".equals(statusFilter)) query.setParameter("statusList", List.of(ProjectStatus.IN_PROGRESS, ProjectStatus.COMPLETED, ProjectStatus.DONE));
        if (bidFilter != null && !bidFilter.isBlank()) query.setParameter("bidFilter", BidFilter.valueOf(bidFilter));
    }
}
