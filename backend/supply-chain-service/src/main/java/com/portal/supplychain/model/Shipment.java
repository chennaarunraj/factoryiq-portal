package com.portal.supplychain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments", schema = "portal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "tracking_number", nullable = false, unique = true, length = 100)
    private String trackingNumber;
    @Column(name = "po_id")
    private UUID poId;
    @Column(length = 100)
    private String carrier;
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "PENDING";
    @Column(length = 255)
    private String origin;
    @Column(length = 255)
    private String destination;
    @Column(name = "shipped_date")
    private LocalDate shippedDate;
    @Column(name = "estimated_date")
    private LocalDate estimatedDate;
    @Column(name = "delivered_date")
    private LocalDate deliveredDate;
    @Column(name = "aftership_id", length = 100)
    private String aftershipId;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}