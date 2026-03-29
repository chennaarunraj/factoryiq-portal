package com.portal.aftersales.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "warranty_claims", schema = "portal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WarrantyClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "claim_number", nullable = false, unique = true, length = 50)
    private String claimNumber;
    @Column(name = "customer_id")
    private UUID customerId;
    @Column(name = "rma_id")
    private UUID rmaId;
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "SUBMITTED";
    @Column(name = "failure_description", columnDefinition = "TEXT")
    private String failureDescription;
    @Column(name = "claim_amount", precision = 10, scale = 2)
    private BigDecimal claimAmount;
    @Column(name = "approved_amount", precision = 10, scale = 2)
    private BigDecimal approvedAmount;
    @Column(name = "submitted_date")
    private LocalDate submittedDate;
    @Column(name = "resolved_date")
    private LocalDate resolvedDate;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}