package com.portal.production.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_order_operations", schema = "portal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "work_order_id", nullable = false)
    private UUID workOrderId;

    @Column(name = "line_id")
    private UUID lineId;

    @Column(name = "operation_name", nullable = false, length = 255)
    private String operationName;

    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "planned_qty", nullable = false)
    @Builder.Default
    private Integer plannedQty = 0;

    @Column(name = "completed_qty", nullable = false)
    @Builder.Default
    private Integer completedQty = 0;

    @Column(name = "rejected_qty", nullable = false)
    @Builder.Default
    private Integer rejectedQty = 0;

    @Column(name = "yield_pct", precision = 5, scale = 2)
    private BigDecimal yieldPct;

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