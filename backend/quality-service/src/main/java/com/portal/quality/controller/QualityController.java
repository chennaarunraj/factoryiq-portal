package com.portal.quality.controller;

import com.portal.quality.dto.*;
import com.portal.quality.service.QualityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quality")
@RequiredArgsConstructor
@Slf4j
public class QualityController {

    private final QualityService qualityService;

    // ── HEALTH ────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Quality Service is running!");
    }

    // ── NCR ──────────────────────────────────
    @GetMapping("/ncrs")
    public ResponseEntity<List<NcrResponse>> getAllNcrs() {
        return ResponseEntity.ok(qualityService.getAllNcrs());
    }

    @GetMapping("/ncrs/{id}")
    public ResponseEntity<NcrResponse> getNcrById(@PathVariable UUID id) {
        return ResponseEntity.ok(qualityService.getNcrById(id));
    }

    @GetMapping("/ncrs/customer/{customerId}")
    public ResponseEntity<List<NcrResponse>> getNcrsByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(qualityService.getNcrsByCustomer(customerId));
    }

    @PostMapping("/ncrs")
    public ResponseEntity<NcrResponse> createNcr(@Valid @RequestBody NcrRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(qualityService.createNcr(request));
    }

    @PatchMapping("/ncrs/{id}/status")
    public ResponseEntity<NcrResponse> updateNcrStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        return ResponseEntity.ok(qualityService.updateNcrStatus(id, status));
    }

    // ── CAPA ─────────────────────────────────
    @GetMapping("/capas")
    public ResponseEntity<List<CapaResponse>> getAllCapas() {
        return ResponseEntity.ok(qualityService.getAllCapas());
    }

    @GetMapping("/capas/ncr/{ncrId}")
    public ResponseEntity<List<CapaResponse>> getCapasByNcr(@PathVariable UUID ncrId) {
        return ResponseEntity.ok(qualityService.getCapasByNcr(ncrId));
    }

    @PostMapping("/capas")
    public ResponseEntity<CapaResponse> createCapa(@Valid @RequestBody CapaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(qualityService.createCapa(request));
    }

    // ── CERTIFICATIONS ────────────────────────
    @GetMapping("/certifications")
    public ResponseEntity<List<CertificationResponse>> getAllCertifications() {
        return ResponseEntity.ok(qualityService.getAllCertifications());
    }

    @GetMapping("/certifications/expiring")
    public ResponseEntity<List<CertificationResponse>> getExpiringSoon(
            @RequestParam(defaultValue = "90") int days) {
        return ResponseEntity.ok(qualityService.getExpiringSoon(days));
    }
}