package com.example.SevMerge.faq;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq , Long> {

    @Query("""
        SELECT f FROM Faq f WHERE f.isActive = true
    """)
    List<Faq> findAllIsActive();

}
