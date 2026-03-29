package com.portal.program.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramSummary {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private String health;
    private String customerName;
    private String customerCode;
    private String siteName;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
}