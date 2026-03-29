package com.portal.quality.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ncrs", schema = "portal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ncr {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ncr_number", nullable = false, unique = true, length = 50)
    private String ncrNumber;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "program_id")
    private UUID programId;

    @Column(name = "site_id")
    private UUID siteId;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "OPEN";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String severity = "MINOR";

    @Column(name = "detected_date")
    private LocalDate detectedDate;

    @Column(name = "containment_action", columnDefinition = "TEXT")
    private String containmentAction;

    @Column(name = "reported_by_id")
    private UUID reportedById;

    @Column(name = "assigned_to_id")
    private UUID assignedToId;

    @Column(name = "closed_date")
    private LocalDate closedDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}