package com.portal.document.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents", schema = "portal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 255)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "doc_type", nullable = false, length = 50)
    @Builder.Default
    private String docType = "OTHER";
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "DRAFT";
    @Column(name = "customer_id")
    private UUID customerId;
    @Column(name = "program_id")
    private UUID programId;
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;
    @Column(name = "file_size")
    private Long fileSize;
    @Column(name = "content_type", length = 100)
    private String contentType;
    @Column(name = "minio_bucket", nullable = false, length = 100)
    private String minioBucket;
    @Column(name = "minio_key", nullable = false, length = 500)
    private String minioKey;
    @Column(nullable = false)
    @Builder.Default
    private Integer version = 1;
    @Column(name = "uploaded_by_id")
    private UUID uploadedById;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}