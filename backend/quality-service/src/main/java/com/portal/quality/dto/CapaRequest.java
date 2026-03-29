package com.portal.quality.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CapaRequest {
    @NotBlank(message = "CAPA number is required")
    private String capaNumber;
    private UUID ncrId;
    @NotBlank(message = "Title is required")
    private String title;
    private String rootCause;
    private String correctiveAction;
    private String preventiveAction;
    private String status;
    private LocalDate dueDate;
    private UUID ownerId;
}