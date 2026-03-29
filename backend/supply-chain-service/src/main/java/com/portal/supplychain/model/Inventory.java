package com.portal.supplychain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory", schema = "portal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "item_code", nullable = false, length = 100)
    private String itemCode;
    @Column(length = 255)
    private String description;
    @Column(name = "site_id")
    private UUID siteId;
    @Column(nullable = false, precision = 15, scale = 3)
    @Builder.Default
    private BigDecimal quantity = BigDecimal.ZERO;
    @Column(name = "allocated_qty", nullable = false, precision = 15, scale = 3)
    @Builder.Default
    private BigDecimal allocatedQty = BigDecimal.ZERO;
    @Column(name = "unit_of_measure", length = 20)
    private String unitOfMeasure;
    @Column(name = "location_bin", length = 50)
    private String locationBin;
    @Column(name = "min_threshold", precision = 15, scale = 3)
    private BigDecimal minThreshold;
    @Column(name = "max_threshold", precision = 15, scale = 3)
    private BigDecimal maxThreshold;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}