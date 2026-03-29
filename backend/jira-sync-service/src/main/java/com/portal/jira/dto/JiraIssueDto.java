package com.portal.jira.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JiraIssueDto {
    private String jiraKey;          // e.g. MFG-123
    private String projectKey;       // e.g. MFG
    private String issueType;        // Bug, Task, Story, Support
    private String summary;
    private String description;
    private String status;           // To Do, In Progress, Done
    private String priority;         // Highest, High, Medium, Low
    private String portalEntityType; // NCR, CAPA, WORK_ORDER, RMA
    private String portalEntityId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean synced;
    private String syncError;
}