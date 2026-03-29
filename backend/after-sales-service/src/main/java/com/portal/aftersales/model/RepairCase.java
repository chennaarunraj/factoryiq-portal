package com.portal.aftersales.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "repair_cases", schema = "portal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepairCase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "case_number", nullable = false, unique = true, length = 50)
    private String caseNumber;
    @Column(name = "rma_id")
    private UUID rmaId;
    @Column(name = "customer_id")
    private UUID customerId;
    @Column(columnDefinition = "TEXT")
    private String diagnosis;
    @Column(name = "repair_action", columnDefinition = "TEXT")
    private String repairAction;
    @Column(name = "repair_cost", precision = 10, scale = 2)
    private BigDecimal repairCost;
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "OPEN";
    @Column(name = "technician_id")
    private UUID technicianId;
    @Column(name = "started_at")
    private OffsetDateTime startedAt;
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}