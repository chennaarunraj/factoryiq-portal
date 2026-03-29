package com.portal.production.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderOperationRequest {

    @NotNull(message = "Work Order ID is required")
    private UUID workOrderId;

    private UUID lineId;

    @NotBlank(message = "Operation name is required")
    @Size(max = 255)
    private String operationName;

    @NotNull(message = "Sequence number is required")
    private Integer sequenceNo;

    private String status;

    @Min(0)
    private Integer plannedQty;

    @Min(0)
    private Integer completedQty;

    @Min(0)
    private Integer rejectedQty;

    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal yieldPct;
}