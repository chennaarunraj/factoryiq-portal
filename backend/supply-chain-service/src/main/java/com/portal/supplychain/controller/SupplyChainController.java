package com.portal.supplychain.controller;

import com.portal.supplychain.model.*;
import com.portal.supplychain.service.SupplyChainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/supply-chain")
@RequiredArgsConstructor
@Slf4j
public class SupplyChainController {

    private final SupplyChainService supplyChainService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Supply Chain Service is running!");
    }

    // ── SUPPLIERS ─────────────────────────────
    @GetMapping("/suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplyChainService.getAllSuppliers());
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable UUID id) {
        return ResponseEntity.ok(supplyChainService.getSupplierById(id));
    }

    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplyChainService.createSupplier(supplier));
    }

    // ── PURCHASE ORDERS ───────────────────────
    @GetMapping("/purchase-orders")
    public ResponseEntity<List<PurchaseOrder>> getAllPurchaseOrders() {
        return ResponseEntity.ok(supplyChainService.getAllPurchaseOrders());
    }

    @GetMapping("/purchase-orders/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(supplyChainService.getPurchaseOrderById(id));
    }

    @GetMapping("/purchase-orders/customer/{customerId}")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrdersByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(supplyChainService.getPurchaseOrdersByCustomer(customerId));
    }

    @PostMapping("/purchase-orders")
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrder po) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplyChainService.createPurchaseOrder(po));
    }

    @PatchMapping("/purchase-orders/{id}/status")
    public ResponseEntity<PurchaseOrder> updatePoStatus(
            @PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(supplyChainService.updatePoStatus(id, status));
    }

    // ── INVENTORY ─────────────────────────────
    @GetMapping("/inventory/site/{siteId}")
    public ResponseEntity<List<Inventory>> getInventoryBySite(@PathVariable UUID siteId) {
        return ResponseEntity.ok(supplyChainService.getInventoryBySite(siteId));
    }

    @GetMapping("/inventory/low-stock")
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        return ResponseEntity.ok(supplyChainService.getLowStockItems());
    }

    // ── SHIPMENTS ─────────────────────────────
    @GetMapping("/shipments")
    public ResponseEntity<List<Shipment>> getAllShipments() {
        return ResponseEntity.ok(supplyChainService.getAllShipments());
    }

    @GetMapping("/shipments/tracking/{trackingNumber}")
    public ResponseEntity<Shipment> getShipmentByTracking(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(supplyChainService.getShipmentByTracking(trackingNumber));
    }

    @PostMapping("/shipments")
    public ResponseEntity<Shipment> createShipment(@RequestBody Shipment shipment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplyChainService.createShipment(shipment));
    }

    @PatchMapping("/shipments/{id}/status")
    public ResponseEntity<Shipment> updateShipmentStatus(
            @PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(supplyChainService.updateShipmentStatus(id, status));
    }
}