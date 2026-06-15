package com.example.SevMerge.faq;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DialectOverride;

import java.sql.Timestamp;

@Table(name = "faq_tb")
@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class Faq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;
    @ColumnDefault("true")
    private Boolean isActive;

    public void delete (){
        isActive = false;
    }


}
