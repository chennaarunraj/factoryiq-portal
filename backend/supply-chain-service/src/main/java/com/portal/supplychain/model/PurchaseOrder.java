package com.portal.supplychain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders", schema = "portal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "po_number", nullable = false, unique = true, length = 100)
    private String poNumber;
    @Column(name = "supplier_id")
    private UUID supplierId;
    @Column(name = "customer_id")
    private UUID customerId;
    @Column(name = "program_id")
    private UUID programId;
    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "DRAFT";
    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;
    @Column(length = 10)
    @Builder.Default
    private String currency = "USD";
    @Column(name = "order_date")
    private LocalDate orderDate;
    @Column(name = "expected_date")
    private LocalDate expectedDate;
    @Column(name = "received_date")
    private LocalDate receivedDate;
    @Column(name = "dynamics_po_id", length = 100)
    private String dynamicsPoId;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}