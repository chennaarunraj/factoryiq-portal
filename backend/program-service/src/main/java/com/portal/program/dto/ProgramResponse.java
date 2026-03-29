package com.portal.program.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;

    // Customer info
    private UUID customerId;
    private String customerName;
    private String customerCode;

    // Site info
    private UUID siteId;
    private String siteName;
    private String siteCode;

    private String status;
    private String health;

    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;

    private String jiraProjectKey;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}