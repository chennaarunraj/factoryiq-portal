package com.portal.program.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramRequest {

    @NotBlank(message = "Code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private String description;

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    private UUID siteId;

    private String status;

    private String health;

    private LocalDate plannedStartDate;

    private LocalDate plannedEndDate;

    private String jiraProjectKey;
}