package com.portal.quality.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NcrRequest {
    @NotBlank(message = "NCR number is required")
    private String ncrNumber;
    @NotBlank(message = "Title is required")
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
}