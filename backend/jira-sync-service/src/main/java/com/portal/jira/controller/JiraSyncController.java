package com.portal.jira.controller;

import com.portal.jira.dto.JiraSyncResultDto;
import com.portal.jira.service.JiraSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/jira")
@RequiredArgsConstructor
@Slf4j
public class JiraSyncController {

    private final JiraSyncService jiraSyncService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Jira Sync Service is running!");
    }

    // ── MANUAL SYNC TRIGGERS ──────────────────
    @PostMapping("/sync/all")
    public ResponseEntity<Map<String, Object>> syncAll() {
        log.info("Manual full sync triggered");
        jiraSyncService.syncAll();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Full sync completed",
            "timestamp", OffsetDateTime.now().toString()
        ));
    }

    @PostMapping("/sync/ncrs")
    public ResponseEntity<JiraSyncResultDto> syncNcrs() {
        return ResponseEntity.ok(jiraSyncService.syncNcrs());
    }

    @PostMapping("/sync/capas")
    public ResponseEntity<JiraSyncResultDto> syncCapas() {
        return ResponseEntity.ok(jiraSyncService.syncCapas());
    }

    @PostMapping("/sync/work-orders")
    public ResponseEntity<JiraSyncResultDto> syncWorkOrders() {
        return ResponseEntity.ok(jiraSyncService.syncWorkOrders());
    }

    @PostMapping("/sync/rmas")
    public ResponseEntity<JiraSyncResultDto> syncRmas() {
        return ResponseEntity.ok(jiraSyncService.syncRmas());
    }

    // ── WEBHOOK FROM JIRA ─────────────────────
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleJiraWebhook(
            @RequestBody Map<String, Object> payload) {
        log.info("Received Jira webhook: {}", payload.get("webhookEvent"));
        // Process Jira webhook events
        String event = (String) payload.get("webhookEvent");
        if (event != null) {
            switch (event) {
                case "jira:issue_updated" -> log.info("Issue updated in Jira");
                case "jira:issue_created" -> log.info("Issue created in Jira");
                case "jira:issue_deleted" -> log.info("Issue deleted in Jira");
            }
        }
        return ResponseEntity.ok(Map.of("status", "received"));
    }

    // ── SYNC STATUS ───────────────────────────
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSyncStatus() {
        return ResponseEntity.ok(Map.of(
            "service", "Jira Sync Service",
            "status", "running",
            "mockMode", true,
            "syncSchedule", "Every 30 minutes",
            "modules", new String[]{"NCRs", "CAPAs", "Work Orders", "RMAs"},
            "timestamp", OffsetDateTime.now().toString()
        ));
    }
}