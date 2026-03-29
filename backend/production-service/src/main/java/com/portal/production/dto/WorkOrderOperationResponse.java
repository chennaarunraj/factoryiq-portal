package com.portal.production.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderOperationResponse {

    private UUID id;
    private UUID workOrderId;
    private UUID lineId;
    private String operationName;
    private Integer sequenceNo;
    private String status;
    private Integer plannedQty;
    private Integer completedQty;
    private Integer rejectedQty;
    private BigDecimal yieldPct;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}