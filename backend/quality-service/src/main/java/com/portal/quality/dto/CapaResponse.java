package com.portal.quality.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CapaResponse {
    private UUID id;
    private String capaNumber;
    private UUID ncrId;
    private String title;
    private String rootCause;
    private String correctiveAction;
    private String preventiveAction;
    private String status;
    private LocalDate dueDate;
    private LocalDate closedDate;
    private UUID ownerId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}