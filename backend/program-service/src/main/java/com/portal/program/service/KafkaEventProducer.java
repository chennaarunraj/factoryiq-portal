package com.portal.program.service;

import com.portal.program.event.PortalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // ── TOPICS ────────────────────────────────
    public static final String PROGRAM_EVENTS = "program-events";
    public static final String QUALITY_EVENTS = "quality-events";
    public static final String PRODUCTION_EVENTS = "production-events";
    public static final String SUPPLY_CHAIN_EVENTS = "supply-chain-events";
    public static final String AFTER_SALES_EVENTS = "after-sales-events";
    public static final String NOTIFICATION_EVENTS = "notification-events";

    // ── PUBLISH EVENT ─────────────────────────
    public void publishEvent(String topic, PortalEvent event) {
        event.setEventId(UUID.randomUUID().toString());
        event.setTimestamp(OffsetDateTime.now());

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(topic, event.getEntityId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event published to topic {}: {} - {}",
                    topic, event.getEventType(), event.getEntityId());
            } else {
                log.error("Failed to publish event to topic {}: {}",
                    topic, ex.getMessage());
            }
        });
    }

    // ── HELPER METHODS ────────────────────────
    public void publishProgramCreated(String programId, String programCode) {
        publishEvent(PROGRAM_EVENTS, PortalEvent.builder()
            .eventType("PROGRAM_CREATED")
            .entityType("PROGRAM")
            .entityId(programId)
            .entityCode(programCode)
            .action("CREATED")
            .build());
    }

    public void publishProgramUpdated(String programId, String programCode,
                                       String oldStatus, String newStatus) {
        publishEvent(PROGRAM_EVENTS, PortalEvent.builder()
            .eventType("PROGRAM_STATUS_CHANGED")
            .entityType("PROGRAM")
            .entityId(programId)
            .entityCode(programCode)
            .action("STATUS_CHANGED")
            .oldStatus(oldStatus)
            .newStatus(newStatus)
            .build());
    }

    public void publishNcrCreated(String ncrId, String ncrNumber) {
        publishEvent(QUALITY_EVENTS, PortalEvent.builder()
            .eventType("NCR_CREATED")
            .entityType("NCR")
            .entityId(ncrId)
            .entityCode(ncrNumber)
            .action("CREATED")
            .build());
    }

    public void publishCapaCreated(String capaId, String capaNumber) {
        publishEvent(QUALITY_EVENTS, PortalEvent.builder()
            .eventType("CAPA_CREATED")
            .entityType("CAPA")
            .entityId(capaId)
            .entityCode(capaNumber)
            .action("CREATED")
            .build());
    }

    public void publishWorkOrderCreated(String woId, String orderNumber) {
        publishEvent(PRODUCTION_EVENTS, PortalEvent.builder()
            .eventType("WORK_ORDER_CREATED")
            .entityType("WORK_ORDER")
            .entityId(woId)
            .entityCode(orderNumber)
            .action("CREATED")
            .build());
    }

    public void publishRmaCreated(String rmaId, String rmaNumber) {
        publishEvent(AFTER_SALES_EVENTS, PortalEvent.builder()
            .eventType("RMA_CREATED")
            .entityType("RMA")
            .entityId(rmaId)
            .entityCode(rmaNumber)
            .action("CREATED")
            .build());
    }
}
