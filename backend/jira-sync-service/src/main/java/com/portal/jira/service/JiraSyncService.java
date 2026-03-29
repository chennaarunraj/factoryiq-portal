package com.portal.jira.service;

import com.portal.jira.dto.JiraIssueDto;
import com.portal.jira.dto.JiraSyncResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JiraSyncService {

    private final JiraClientService jiraClientService;
    private final JdbcTemplate jdbcTemplate;

    @Value("${jira.mock-mode:true}")
    private boolean mockMode;

    @Value("${jira.project-key:MFG}")
    private String projectKey;

    // ── SYNC ALL — runs every 30 minutes ──────
    @Scheduled(fixedDelay = 1800000)
    public void syncAll() {
        log.info("Starting full Jira sync (mock-mode: {})", mockMode);
        syncNcrs();
        syncCapas();
        syncWorkOrders();
        syncRmas();
        log.info("Full Jira sync completed");
    }

    // ── SYNC NCRs ─────────────────────────────
    public JiraSyncResultDto syncNcrs() {
        log.info("Syncing NCRs to Jira...");
        List<Map<String, Object>> ncrs = jdbcTemplate.queryForList(
            "SELECT id, ncr_number, title, description, severity, status " +
            "FROM portal.ncrs WHERE status != 'CLOSED' LIMIT 50"
        );

        List<String> synced = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map<String, Object> ncr : ncrs) {
            try {
                JiraIssueDto issue = JiraIssueDto.builder()
                    .projectKey(projectKey)
                    .issueType("Bug")
                    .summary("[NCR] " + ncr.get("ncr_number") + " - " + ncr.get("title"))
                    .description(buildNcrDescription(ncr))
                    .priority(mapSeverityToPriority((String) ncr.get("severity")))
                    .portalEntityType("NCR")
                    .portalEntityId(ncr.get("id").toString())
                    .build();

                JiraIssueDto result = jiraClientService.createIssue(issue);
                synced.add(ncr.get("id").toString());
                log.info("NCR {} synced as Jira issue {}", ncr.get("ncr_number"), result.getJiraKey());
            } catch (Exception e) {
                failed.add(ncr.get("id").toString());
                log.error("Failed to sync NCR {}: {}", ncr.get("ncr_number"), e.getMessage());
            }
        }

        return JiraSyncResultDto.builder()
            .entityType("NCR")
            .totalProcessed(ncrs.size())
            .successCount(synced.size())
            .failureCount(failed.size())
            .syncedIds(synced)
            .failedIds(failed)
            .syncedAt(OffsetDateTime.now())
            .mockMode(mockMode)
            .build();
    }

    // ── SYNC CAPAs ────────────────────────────
    public JiraSyncResultDto syncCapas() {
        log.info("Syncing CAPAs to Jira...");
        List<Map<String, Object>> capas = jdbcTemplate.queryForList(
            "SELECT id, capa_number, title, root_cause, corrective_action, status " +
            "FROM portal.capas WHERE status != 'CLOSED' LIMIT 50"
        );

        List<String> synced = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map<String, Object> capa : capas) {
            try {
                JiraIssueDto issue = JiraIssueDto.builder()
                    .projectKey(projectKey)
                    .issueType("Task")
                    .summary("[CAPA] " + capa.get("capa_number") + " - " + capa.get("title"))
                    .description(buildCapaDescription(capa))
                    .priority("High")
                    .portalEntityType("CAPA")
                    .portalEntityId(capa.get("id").toString())
                    .build();

                JiraIssueDto result = jiraClientService.createIssue(issue);
                synced.add(capa.get("id").toString());
                log.info("CAPA {} synced as Jira issue {}", capa.get("capa_number"), result.getJiraKey());
            } catch (Exception e) {
                failed.add(capa.get("id").toString());
                log.error("Failed to sync CAPA {}: {}", capa.get("capa_number"), e.getMessage());
            }
        }

        return JiraSyncResultDto.builder()
            .entityType("CAPA")
            .totalProcessed(capas.size())
            .successCount(synced.size())
            .failureCount(failed.size())
            .syncedIds(synced)
            .failedIds(failed)
            .syncedAt(OffsetDateTime.now())
            .mockMode(mockMode)
            .build();
    }

    // ── SYNC WORK ORDERS ──────────────────────
    public JiraSyncResultDto syncWorkOrders() {
        log.info("Syncing Work Orders to Jira...");
        List<Map<String, Object>> workOrders = jdbcTemplate.queryForList(
            "SELECT id, order_number, description, status, planned_qty " +
            "FROM portal.work_orders WHERE status IN ('PLANNED','IN_PROGRESS') LIMIT 50"
        );

        List<String> synced = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map<String, Object> wo : workOrders) {
            try {
                JiraIssueDto issue = JiraIssueDto.builder()
                    .projectKey(projectKey)
                    .issueType("Story")
                    .summary("[WO] " + wo.get("order_number") + " - " + wo.get("description"))
                    .description(buildWoDescription(wo))
                    .priority("Medium")
                    .portalEntityType("WORK_ORDER")
                    .portalEntityId(wo.get("id").toString())
                    .build();

                JiraIssueDto result = jiraClientService.createIssue(issue);
                synced.add(wo.get("id").toString());
                log.info("Work Order {} synced as Jira issue {}", wo.get("order_number"), result.getJiraKey());
            } catch (Exception e) {
                failed.add(wo.get("id").toString());
                log.error("Failed to sync Work Order {}: {}", wo.get("order_number"), e.getMessage());
            }
        }

        return JiraSyncResultDto.builder()
            .entityType("WORK_ORDER")
            .totalProcessed(workOrders.size())
            .successCount(synced.size())
            .failureCount(failed.size())
            .syncedIds(synced)
            .failedIds(failed)
            .syncedAt(OffsetDateTime.now())
            .mockMode(mockMode)
            .build();
    }

    // ── SYNC RMAs ─────────────────────────────
    public JiraSyncResultDto syncRmas() {
        log.info("Syncing RMAs to Jira...");
        List<Map<String, Object>> rmas = jdbcTemplate.queryForList(
            "SELECT id, rma_number, description, status, reason_code " +
            "FROM portal.rmas WHERE status NOT IN ('CLOSED','CANCELLED') LIMIT 50"
        );

        List<String> synced = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map<String, Object> rma : rmas) {
            try {
                JiraIssueDto issue = JiraIssueDto.builder()
                    .projectKey(projectKey)
                    .issueType("Support")
                    .summary("[RMA] " + rma.get("rma_number") + " - " + rma.get("reason_code"))
                    .description(buildRmaDescription(rma))
                    .priority("High")
                    .portalEntityType("RMA")
                    .portalEntityId(rma.get("id").toString())
                    .build();

                JiraIssueDto result = jiraClientService.createIssue(issue);
                synced.add(rma.get("id").toString());
                log.info("RMA {} synced as Jira issue {}", rma.get("rma_number"), result.getJiraKey());
            } catch (Exception e) {
                failed.add(rma.get("id").toString());
                log.error("Failed to sync RMA {}: {}", rma.get("rma_number"), e.getMessage());
            }
        }

        return JiraSyncResultDto.builder()
            .entityType("RMA")
            .totalProcessed(rmas.size())
            .successCount(synced.size())
            .failureCount(failed.size())
            .syncedIds(synced)
            .failedIds(failed)
            .syncedAt(OffsetDateTime.now())
            .mockMode(mockMode)
            .build();
    }

    // ── HELPERS ───────────────────────────────
    private String mapSeverityToPriority(String severity) {
        if (severity == null) return "Medium";
        return switch (severity) {
            case "CRITICAL" -> "Highest";
            case "MAJOR" -> "High";
            case "MINOR" -> "Low";
            default -> "Medium";
        };
    }

    private String buildNcrDescription(Map<String, Object> ncr) {
        return "NCR Number: " + ncr.get("ncr_number") + "\n" +
               "Severity: " + ncr.get("severity") + "\n" +
               "Status: " + ncr.get("status") + "\n" +
               "Description: " + ncr.get("description") + "\n\n" +
               "Synced from Manufacturing Excellence Portal";
    }

    private String buildCapaDescription(Map<String, Object> capa) {
        return "CAPA Number: " + capa.get("capa_number") + "\n" +
               "Root Cause: " + capa.get("root_cause") + "\n" +
               "Corrective Action: " + capa.get("corrective_action") + "\n" +
               "Status: " + capa.get("status") + "\n\n" +
               "Synced from Manufacturing Excellence Portal";
    }

    private String buildWoDescription(Map<String, Object> wo) {
        return "Work Order: " + wo.get("order_number") + "\n" +
               "Planned Qty: " + wo.get("planned_qty") + "\n" +
               "Status: " + wo.get("status") + "\n\n" +
               "Synced from Manufacturing Excellence Portal";
    }

    private String buildRmaDescription(Map<String, Object> rma) {
        return "RMA Number: " + rma.get("rma_number") + "\n" +
               "Reason: " + rma.get("reason_code") + "\n" +
               "Status: " + rma.get("status") + "\n" +
               "Description: " + rma.get("description") + "\n\n" +
               "Synced from Manufacturing Excellence Portal";
    }
}