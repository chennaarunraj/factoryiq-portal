package com.portal.production.controller;

import com.portal.production.model.ProductionLine;
import com.portal.production.model.WorkOrder;
import com.portal.production.model.WorkOrderOperation;
import com.portal.production.service.ProductionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/production")
@RequiredArgsConstructor
@Slf4j
public class ProductionController {

    private final ProductionService productionService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Production Service is running!");
    }

    // ── PRODUCTION LINES ──────────────────────
    @GetMapping("/lines")
    public ResponseEntity<List<ProductionLine>> getAllLines() {
        return ResponseEntity.ok(productionService.getAllLines());
    }

    @GetMapping("/lines/site/{siteId}")
    public ResponseEntity<List<ProductionLine>> getLinesBySite(@PathVariable UUID siteId) {
        return ResponseEntity.ok(productionService.getLinesBySite(siteId));
    }

    // ── WORK ORDERS ───────────────────────────
    @GetMapping("/work-orders")
    public ResponseEntity<List<WorkOrder>> getAllWorkOrders() {
        return ResponseEntity.ok(productionService.getAllWorkOrders());
    }

    @GetMapping("/work-orders/{id}")
    public ResponseEntity<WorkOrder> getWorkOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(productionService.getWorkOrderById(id));
    }

    @PostMapping("/work-orders")
    public ResponseEntity<WorkOrder> createWorkOrder(@RequestBody WorkOrder workOrder) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productionService.createWorkOrder(workOrder));
    }

    @PatchMapping("/work-orders/{id}/status")
    public ResponseEntity<WorkOrder> updateWorkOrderStatus(
            @PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(productionService.updateWorkOrderStatus(id, status));
    }

    // ── OPERATIONS ────────────────────────────
    @GetMapping("/operations/work-order/{workOrderId}")
    public ResponseEntity<List<WorkOrderOperation>> getOperationsByWorkOrder(
            @PathVariable UUID workOrderId) {
        return ResponseEntity.ok(productionService.getOperationsByWorkOrder(workOrderId));
    }

    @PostMapping("/operations")
    public ResponseEntity<WorkOrderOperation> createOperation(
            @RequestBody WorkOrderOperation operation) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productionService.createOperation(operation));
    }

    @PatchMapping("/operations/{id}/status")
    public ResponseEntity<WorkOrderOperation> updateOperationStatus(
            @PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(productionService.updateOperationStatus(id, status));
    }
}




// package com.portal.production.controller;

// import com.portal.production.model.ProductionLine;
// import com.portal.production.model.WorkOrderOperation;
// import com.portal.production.service.ProductionService;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.UUID;

// @RestController
// @RequestMapping("/api/v1/production")
// @RequiredArgsConstructor
// @Slf4j
// public class ProductionController {

//     private final ProductionService productionService;

//     @GetMapping("/health")
//     public ResponseEntity<String> health() {
//         return ResponseEntity.ok("Production Service is running!");
//     }

//     @GetMapping("/lines")
//     public ResponseEntity<List<ProductionLine>> getAllLines() {
//         return ResponseEntity.ok(productionService.getAllLines());
//     }

//     @GetMapping("/lines/site/{siteId}")
//     public ResponseEntity<List<ProductionLine>> getLinesBySite(@PathVariable UUID siteId) {
//         return ResponseEntity.ok(productionService.getLinesBySite(siteId));
//     }

//     @GetMapping("/operations/work-order/{workOrderId}")
//     public ResponseEntity<List<WorkOrderOperation>> getOperationsByWorkOrder(@PathVariable UUID workOrderId) {
//         return ResponseEntity.ok(productionService.getOperationsByWorkOrder(workOrderId));
//     }

//     @PostMapping("/operations")
//     public ResponseEntity<WorkOrderOperation> createOperation(@RequestBody WorkOrderOperation operation) {
//         return ResponseEntity.status(HttpStatus.CREATED).body(productionService.createOperation(operation));
//     }

//     @PatchMapping("/operations/{id}/status")
//     public ResponseEntity<WorkOrderOperation> updateOperationStatus(
//             @PathVariable UUID id, @RequestParam String status) {
//         return ResponseEntity.ok(productionService.updateOperationStatus(id, status));
//     }
// }