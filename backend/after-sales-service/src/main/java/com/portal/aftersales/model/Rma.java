package com.portal.aftersales.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "rmas", schema = "portal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rma {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "rma_number", nullable = false, unique = true, length = 50)
    private String rmaNumber;
    @Column(name = "customer_id")
    private UUID customerId;
    @Column(name = "program_id")
    private UUID programId;
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "REQUESTED";
    @Column(name = "reason_code", length = 100)
    private String reasonCode;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "requested_by_id")
    private UUID requestedById;
    @Column(name = "assigned_to_id")
    private UUID assignedToId;
    @Column(name = "tracking_inbound", length = 100)
    private String trackingInbound;
    @Column(name = "tracking_outbound", length = 100)
    private String trackingOutbound;
    @Column(name = "received_date")
    private LocalDate receivedDate;
    @Column(name = "closed_date")
    private LocalDate closedDate;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}