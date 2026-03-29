package com.portal.program.event;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PortalEvent {

    private String eventId;
    private String eventType;      // PROGRAM_CREATED, NCR_CREATED, etc
    private String entityType;     // PROGRAM, NCR, CAPA, WORK_ORDER, RMA
    private String entityId;
    private String entityCode;
    private String action;         // CREATED, UPDATED, DELETED, STATUS_CHANGED
    private String oldStatus;
    private String newStatus;
    private Map<String, Object> payload;
    private String triggeredBy;
    private OffsetDateTime timestamp;
}
