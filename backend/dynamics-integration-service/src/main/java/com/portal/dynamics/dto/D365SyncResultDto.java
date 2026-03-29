package com.portal.dynamics.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class D365SyncResultDto {
    private String entityType;
    private int totalProcessed;
    private int successCount;
    private int failureCount;
    private List<String> syncedIds;
    private List<String> failedIds;
    private OffsetDateTime syncedAt;
    private boolean mockMode;
}