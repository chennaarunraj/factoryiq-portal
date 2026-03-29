package com.portal.quality.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CertificationRequest {

    @NotBlank(message = "Cert number is required")
    private String certNumber;

    @NotBlank(message = "Name is required")
    private String name;

    private String standard;

    private String issuingBody;

    private UUID siteId;

    private String status;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    private String documentUrl;
}