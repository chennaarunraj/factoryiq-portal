package com.portal.program.service;

import com.portal.program.dto.ProgramRequest;
import com.portal.program.dto.ProgramResponse;
import com.portal.program.dto.ProgramSummary;
import com.portal.program.exception.ResourceNotFoundException;
import com.portal.program.model.Customer;
import com.portal.program.model.Program;
import com.portal.program.model.Site;
import com.portal.program.repository.CustomerRepository;
import com.portal.program.repository.ProgramRepository;
import com.portal.program.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProgramService {

    private final ProgramRepository programRepository;
    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;

    // ─────────────────────────────────────────
    // GET ALL PROGRAMS (portfolio view)
    // ─────────────────────────────────────────
    public List<ProgramSummary> getAllPrograms() {
        log.debug("Fetching all programs");
        return programRepository.findAll()
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    // GET PROGRAMS BY CUSTOMER
    // ─────────────────────────────────────────
    public List<ProgramSummary> getProgramsByCustomer(UUID customerId) {
        log.debug("Fetching programs for customer: {}", customerId);
        return programRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────
    // GET PROGRAM BY ID
    // ─────────────────────────────────────────
    public ProgramResponse getProgramById(UUID id) {
        log.debug("Fetching program by id: {}", id);
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + id));
        return toResponse(program);
    }

    // ─────────────────────────────────────────
    // GET PROGRAM BY CODE
    // ─────────────────────────────────────────
    public ProgramResponse getProgramByCode(String code) {
        log.debug("Fetching program by code: {}", code);
        Program program = programRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with code: " + code));
        return toResponse(program);
    }

    // ─────────────────────────────────────────
    // CREATE PROGRAM
    // ─────────────────────────────────────────
    @Transactional
    public ProgramResponse createProgram(ProgramRequest request) {
        log.debug("Creating program with code: {}", request.getCode());

        // Check duplicate code
        if (programRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Program code already exists: " + request.getCode());
        }

        // Fetch customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.getCustomerId()));

        // Fetch site (optional)
        Site site = null;
        if (request.getSiteId() != null) {
            site = siteRepository.findById(request.getSiteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Site not found: " + request.getSiteId()));
        }

        // Build and save
        Program program = Program.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .customer(customer)
                .site(site)
                .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
                .health(request.getHealth() != null ? request.getHealth() : "GREEN")
                .plannedStartDate(request.getPlannedStartDate())
                .plannedEndDate(request.getPlannedEndDate())
                .jiraProjectKey(request.getJiraProjectKey())
                .build();

        Program saved = programRepository.save(program);
        log.info("Program created successfully: {}", saved.getId());
        return toResponse(saved);
    }

    // ─────────────────────────────────────────
    // UPDATE PROGRAM
    // ─────────────────────────────────────────
    @Transactional
    public ProgramResponse updateProgram(UUID id, ProgramRequest request) {
        log.debug("Updating program: {}", id);

        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found: " + id));

        // Update fields
        program.setName(request.getName());
        program.setDescription(request.getDescription());

        if (request.getStatus() != null) program.setStatus(request.getStatus());
        if (request.getHealth() != null) program.setHealth(request.getHealth());
        if (request.getPlannedStartDate() != null) program.setPlannedStartDate(request.getPlannedStartDate());
        if (request.getPlannedEndDate() != null) program.setPlannedEndDate(request.getPlannedEndDate());
        if (request.getJiraProjectKey() != null) program.setJiraProjectKey(request.getJiraProjectKey());

        // Update site if changed
        if (request.getSiteId() != null) {
            Site site = siteRepository.findById(request.getSiteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Site not found: " + request.getSiteId()));
            program.setSite(site);
        }

        Program updated = programRepository.save(program);
        log.info("Program updated successfully: {}", updated.getId());
        return toResponse(updated);
    }

    // ─────────────────────────────────────────
    // DELETE PROGRAM
    // ─────────────────────────────────────────
    @Transactional
    public void deleteProgram(UUID id) {
        log.debug("Deleting program: {}", id);
        if (!programRepository.existsById(id)) {
            throw new ResourceNotFoundException("Program not found: " + id);
        }
        programRepository.deleteById(id);
        log.info("Program deleted: {}", id);
    }

    // ─────────────────────────────────────────
    // MAPPERS
    // ─────────────────────────────────────────
    private ProgramResponse toResponse(Program p) {
        return ProgramResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .name(p.getName())
                .description(p.getDescription())
                .customerId(p.getCustomer().getId())
                .customerName(p.getCustomer().getName())
                .customerCode(p.getCustomer().getCode())
                .siteId(p.getSite() != null ? p.getSite().getId() : null)
                .siteName(p.getSite() != null ? p.getSite().getName() : null)
                .siteCode(p.getSite() != null ? p.getSite().getCode() : null)
                .status(p.getStatus())
                .health(p.getHealth())
                .plannedStartDate(p.getPlannedStartDate())
                .plannedEndDate(p.getPlannedEndDate())
                .actualStartDate(p.getActualStartDate())
                .actualEndDate(p.getActualEndDate())
                .jiraProjectKey(p.getJiraProjectKey())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private ProgramSummary toSummary(Program p) {
        return ProgramSummary.builder()
                .id(p.getId())
                .code(p.getCode())
                .name(p.getName())
                .status(p.getStatus())
                .health(p.getHealth())
                .customerName(p.getCustomer().getName())
                .customerCode(p.getCustomer().getCode())
                .siteName(p.getSite() != null ? p.getSite().getName() : null)
                .plannedStartDate(p.getPlannedStartDate())
                .plannedEndDate(p.getPlannedEndDate())
                .build();
    }
}