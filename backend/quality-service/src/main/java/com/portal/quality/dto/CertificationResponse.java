package com.portal.quality.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CertificationResponse {
    private UUID id;
    private String certNumber;
    private String name;
    private String standard;
    private String issuingBody;
    private UUID siteId;
    private String status;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String documentUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}