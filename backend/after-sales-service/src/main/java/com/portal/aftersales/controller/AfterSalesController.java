package com.portal.aftersales.controller;

import com.portal.aftersales.model.*;
import com.portal.aftersales.service.AfterSalesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/after-sales")
@RequiredArgsConstructor
@Slf4j
public class AfterSalesController {

    private final AfterSalesService afterSalesService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("After-Sales Service is running!");
    }

    // ── RMA ──────────────────────────────────
    @GetMapping("/rmas")
    public ResponseEntity<List<Rma>> getAllRmas() {
        return ResponseEntity.ok(afterSalesService.getAllRmas());
    }

    @GetMapping("/rmas/{id}")
    public ResponseEntity<Rma> getRmaById(@PathVariable UUID id) {
        return ResponseEntity.ok(afterSalesService.getRmaById(id));
    }

    @GetMapping("/rmas/customer/{customerId}")
    public ResponseEntity<List<Rma>> getRmasByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(afterSalesService.getRmasByCustomer(customerId));
    }

    @PostMapping("/rmas")
    public ResponseEntity<Rma> createRma(@RequestBody Rma rma) {
        return ResponseEntity.status(HttpStatus.CREATED).body(afterSalesService.createRma(rma));
    }

    @PatchMapping("/rmas/{id}/status")
    public ResponseEntity<Rma> updateRmaStatus(
            @PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(afterSalesService.updateRmaStatus(id, status));
    }

    // ── REPAIR CASES ─────────────────────────
    @GetMapping("/repairs")
    public ResponseEntity<List<RepairCase>> getAllRepairCases() {
        return ResponseEntity.ok(afterSalesService.getAllRepairCases());
    }

    @GetMapping("/repairs/rma/{rmaId}")
    public ResponseEntity<List<RepairCase>> getRepairCasesByRma(@PathVariable UUID rmaId) {
        return ResponseEntity.ok(afterSalesService.getRepairCasesByRma(rmaId));
    }

    @PostMapping("/repairs")
    public ResponseEntity<RepairCase> createRepairCase(@RequestBody RepairCase repairCase) {
        return ResponseEntity.status(HttpStatus.CREATED).body(afterSalesService.createRepairCase(repairCase));
    }

    // ── WARRANTY CLAIMS ───────────────────────
    @GetMapping("/warranty")
    public ResponseEntity<List<WarrantyClaim>> getAllWarrantyClaims() {
        return ResponseEntity.ok(afterSalesService.getAllWarrantyClaims());
    }

    @GetMapping("/warranty/customer/{customerId}")
    public ResponseEntity<List<WarrantyClaim>> getWarrantyClaimsByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(afterSalesService.getWarrantyClaimsByCustomer(customerId));
    }

    @PostMapping("/warranty")
    public ResponseEntity<WarrantyClaim> createWarrantyClaim(@RequestBody WarrantyClaim claim) {
        return ResponseEntity.status(HttpStatus.CREATED).body(afterSalesService.createWarrantyClaim(claim));
    }

    @PatchMapping("/warranty/{id}/status")
    public ResponseEntity<WarrantyClaim> updateClaimStatus(
            @PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(afterSalesService.updateClaimStatus(id, status));
    }
}