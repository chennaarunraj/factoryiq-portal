package com.portal.program.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class KafkaEventConsumer {

    // ── PROGRAM EVENTS ────────────────────────
    @KafkaListener(topics = "program-events", groupId = "portal-notification-group")
    public void consumeProgramEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        String entityCode = (String) event.get("entityCode");
        log.info("📨 Received program event: {} for {}", eventType, entityCode);

        switch (eventType != null ? eventType : "") {
            case "PROGRAM_CREATED" ->
                log.info("✅ New program created: {} — triggering Jira sync", entityCode);
            case "PROGRAM_STATUS_CHANGED" ->
                log.info("🔄 Program {} status changed: {} → {}",
                    entityCode, event.get("oldStatus"), event.get("newStatus"));
            default ->
                log.info("📌 Program event received: {}", eventType);
        }
    }

    // ── QUALITY EVENTS ────────────────────────
    @KafkaListener(topics = "quality-events", groupId = "portal-notification-group")
    public void consumeQualityEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        String entityCode = (String) event.get("entityCode");
        log.info("📨 Received quality event: {} for {}", eventType, entityCode);

        switch (eventType != null ? eventType : "") {
            case "NCR_CREATED" ->
                log.info("🚨 New NCR: {} — triggering Jira Bug creation", entityCode);
            case "CAPA_CREATED" ->
                log.info("📋 New CAPA: {} — triggering Jira Task creation", entityCode);
            default ->
                log.info("📌 Quality event received: {}", eventType);
        }
    }

    // ── PRODUCTION EVENTS ─────────────────────
    @KafkaListener(topics = "production-events", groupId = "portal-notification-group")
    public void consumeProductionEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        String entityCode = (String) event.get("entityCode");
        log.info("📨 Received production event: {} for {}", eventType, entityCode);

        switch (eventType != null ? eventType : "") {
            case "WORK_ORDER_CREATED" ->
                log.info("🏭 New Work Order: {} — syncing to D365", entityCode);
            case "WORK_ORDER_COMPLETED" ->
                log.info("✅ Work Order completed: {} — notifying stakeholders", entityCode);
            default ->
                log.info("📌 Production event received: {}", eventType);
        }
    }

    // ── AFTER SALES EVENTS ────────────────────
    @KafkaListener(topics = "after-sales-events", groupId = "portal-notification-group")
    public void consumeAfterSalesEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        String entityCode = (String) event.get("entityCode");
        log.info("📨 Received after-sales event: {} for {}", eventType, entityCode);

        switch (eventType != null ? eventType : "") {
            case "RMA_CREATED" ->
                log.info("📦 New RMA: {} — creating D365 Case", entityCode);
            case "RMA_APPROVED" ->
                log.info("✅ RMA approved: {} — notifying customer", entityCode);
            default ->
                log.info("📌 After-sales event received: {}", eventType);
        }
    }

    // ── SUPPLY CHAIN EVENTS ───────────────────
    @KafkaListener(topics = "supply-chain-events", groupId = "portal-notification-group")
    public void consumeSupplyChainEvents(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        String entityCode = (String) event.get("entityCode");
        log.info("📨 Received supply-chain event: {} for {}", eventType, entityCode);

        switch (eventType != null ? eventType : "") {
            case "PO_CREATED" ->
                log.info("📄 New PO: {} — syncing to D365", entityCode);
            case "SHIPMENT_DELIVERED" ->
                log.info("🚚 Shipment delivered: {} — updating inventory", entityCode);
            default ->
                log.info("📌 Supply chain event received: {}", eventType);
        }
    }
}
