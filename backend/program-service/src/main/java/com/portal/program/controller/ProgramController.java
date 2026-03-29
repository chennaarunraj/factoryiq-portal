package com.portal.program.controller;

import com.portal.program.dto.ProgramRequest;
import com.portal.program.dto.ProgramResponse;
import com.portal.program.dto.ProgramSummary;
import com.portal.program.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
@Slf4j
public class ProgramController {

    private final ProgramService programService;

    // ─────────────────────────────────────────
    // GET ALL - Portfolio view
    // GET /api/v1/programs
    // ─────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<ProgramSummary>> getAllPrograms() {
        log.debug("GET /api/v1/programs");
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    // ─────────────────────────────────────────
    // GET BY CUSTOMER
    // GET /api/v1/programs/customer/{customerId}
    // ─────────────────────────────────────────
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ProgramSummary>> getProgramsByCustomer(
            @PathVariable UUID customerId) {
        log.debug("GET /api/v1/programs/customer/{}", customerId);
        return ResponseEntity.ok(programService.getProgramsByCustomer(customerId));
    }

    // ─────────────────────────────────────────
    // GET BY ID
    // GET /api/v1/programs/{id}
    // ─────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ProgramResponse> getProgramById(
            @PathVariable UUID id) {
        log.debug("GET /api/v1/programs/{}", id);
        return ResponseEntity.ok(programService.getProgramById(id));
    }

    // ─────────────────────────────────────────
    // GET BY CODE
    // GET /api/v1/programs/code/{code}
    // ─────────────────────────────────────────
    @GetMapping("/code/{code}")
    public ResponseEntity<ProgramResponse> getProgramByCode(
            @PathVariable String code) {
        log.debug("GET /api/v1/programs/code/{}", code);
        return ResponseEntity.ok(programService.getProgramByCode(code));
    }

    // ─────────────────────────────────────────
    // CREATE
    // POST /api/v1/programs
    // ─────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ProgramResponse> createProgram(
            @Valid @RequestBody ProgramRequest request) {
        log.debug("POST /api/v1/programs - code: {}", request.getCode());
        ProgramResponse response = programService.createProgram(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─────────────────────────────────────────
    // UPDATE
    // PUT /api/v1/programs/{id}
    // ─────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ProgramResponse> updateProgram(
            @PathVariable UUID id,
            @Valid @RequestBody ProgramRequest request) {
        log.debug("PUT /api/v1/programs/{}", id);
        return ResponseEntity.ok(programService.updateProgram(id, request));
    }

    // ─────────────────────────────────────────
    // DELETE
    // DELETE /api/v1/programs/{id}
    // ─────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(
            @PathVariable UUID id) {
        log.debug("DELETE /api/v1/programs/{}", id);
        programService.deleteProgram(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────
    // HEALTH CHECK
    // GET /api/v1/programs/health
    // ─────────────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Program Service is running!");
    }
}