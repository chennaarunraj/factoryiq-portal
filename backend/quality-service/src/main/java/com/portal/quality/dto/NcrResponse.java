package com.portal.quality.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NcrResponse {
    private UUID id;
    private String ncrNumber;
    private String title;
    private String description;
    private UUID customerId;
    private UUID programId;
    private UUID siteId;
    private String status;
    private String severity;
    private LocalDate detectedDate;
    private String containmentAction;
    private UUID assignedToId;
    private LocalDate closedDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}