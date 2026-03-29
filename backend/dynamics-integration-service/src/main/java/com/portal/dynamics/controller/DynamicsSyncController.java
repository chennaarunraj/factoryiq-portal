package com.portal.dynamics.controller;

import com.portal.dynamics.dto.D365SyncResultDto;
import com.portal.dynamics.service.DynamicsSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dynamics")
@RequiredArgsConstructor
@Slf4j
public class DynamicsSyncController {

    private final DynamicsSyncService dynamicsSyncService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Dynamics 365 Sync Service is running!");
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "service", "Dynamics 365 Sync Service",
            "status", "running",
            "mockMode", true,
            "syncSchedule", "Every 1 hour",
            "modules", new String[]{"Customers→Accounts", "Programs→Opportunities",
                                    "WorkOrders→WorkOrders", "RMAs→Cases"},
            "timestamp", OffsetDateTime.now().toString()
        ));
    }

    @PostMapping("/sync/all")
    public ResponseEntity<Map<String, Object>> syncAll() {
        dynamicsSyncService.syncAll();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Full D365 sync completed",
            "timestamp", OffsetDateTime.now().toString()
        ));
    }

    @PostMapping("/sync/customers")
    public ResponseEntity<D365SyncResultDto> syncCustomers() {
        return ResponseEntity.ok(dynamicsSyncService.syncCustomers());
    }

    @PostMapping("/sync/programs")
    public ResponseEntity<D365SyncResultDto> syncPrograms() {
        return ResponseEntity.ok(dynamicsSyncService.syncPrograms());
    }

    @PostMapping("/sync/work-orders")
    public ResponseEntity<D365SyncResultDto> syncWorkOrders() {
        return ResponseEntity.ok(dynamicsSyncService.syncWorkOrders());
    }

    @PostMapping("/sync/rmas")
    public ResponseEntity<D365SyncResultDto> syncRmas() {
        return ResponseEntity.ok(dynamicsSyncService.syncRmas());
    }
}