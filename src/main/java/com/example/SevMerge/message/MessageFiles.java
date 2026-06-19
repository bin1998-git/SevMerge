package com.example.SevMerge.message;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "message_files_tb")
@NoArgsConstructor
public class MessageFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Message message;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    private Long fileSize;

    @Builder
    public MessageFiles(Message message, String filename, String originalFilename, Long fileSize) {
        this.message = message;
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.fileSize = fileSize;
    }

}
