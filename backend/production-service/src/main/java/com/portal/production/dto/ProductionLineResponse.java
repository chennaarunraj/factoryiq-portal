package com.portal.production.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionLineResponse {

    private UUID id;
    private UUID siteId;
    private String code;
    private String name;
    private Integer capacityPerShift;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}