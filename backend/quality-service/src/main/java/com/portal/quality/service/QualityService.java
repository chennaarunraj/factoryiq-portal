package com.portal.quality.service;

import com.portal.quality.dto.*;
import com.portal.quality.exception.ResourceNotFoundException;
import com.portal.quality.model.*;
import com.portal.quality.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QualityService {

    private final NcrRepository ncrRepository;
    private final CapaRepository capaRepository;
    private final CertificationRepository certificationRepository;

    // ── NCR ──────────────────────────────────
    public List<NcrResponse> getAllNcrs() {
        return ncrRepository.findAll().stream().map(this::toNcrResponse).collect(Collectors.toList());
    }

    public List<NcrResponse> getNcrsByCustomer(UUID customerId) {
        return ncrRepository.findByCustomerId(customerId).stream().map(this::toNcrResponse).collect(Collectors.toList());
    }

    public NcrResponse getNcrById(UUID id) {
        Ncr ncr = ncrRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NCR not found: " + id));
        return toNcrResponse(ncr);
    }

    @Transactional
    public NcrResponse createNcr(NcrRequest request) {
        if (ncrRepository.existsByNcrNumber(request.getNcrNumber())) {
            throw new IllegalArgumentException("NCR number already exists: " + request.getNcrNumber());
        }
        Ncr ncr = Ncr.builder()
                .ncrNumber(request.getNcrNumber())
                .title(request.getTitle())
                .description(request.getDescription())
                .customerId(request.getCustomerId())
                .programId(request.getProgramId())
                .siteId(request.getSiteId())
                .status(request.getStatus() != null ? request.getStatus() : "OPEN")
                .severity(request.getSeverity() != null ? request.getSeverity() : "MINOR")
                .detectedDate(request.getDetectedDate() != null ? request.getDetectedDate() : LocalDate.now())
                .containmentAction(request.getContainmentAction())
                .assignedToId(request.getAssignedToId())
                .build();
        return toNcrResponse(ncrRepository.save(ncr));
    }

    @Transactional
    public NcrResponse updateNcrStatus(UUID id, String status) {
        Ncr ncr = ncrRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NCR not found: " + id));
        ncr.setStatus(status);
        if ("CLOSED".equals(status)) ncr.setClosedDate(LocalDate.now());
        return toNcrResponse(ncrRepository.save(ncr));
    }

    // ── CAPA ─────────────────────────────────
    public List<CapaResponse> getAllCapas() {
        return capaRepository.findAll().stream().map(this::toCapaResponse).collect(Collectors.toList());
    }

    public List<CapaResponse> getCapasByNcr(UUID ncrId) {
        return capaRepository.findByNcrId(ncrId).stream().map(this::toCapaResponse).collect(Collectors.toList());
    }

    @Transactional
    public CapaResponse createCapa(CapaRequest request) {
        if (capaRepository.existsByCapaNumber(request.getCapaNumber())) {
            throw new IllegalArgumentException("CAPA number already exists: " + request.getCapaNumber());
        }
        Capa capa = Capa.builder()
                .capaNumber(request.getCapaNumber())
                .ncrId(request.getNcrId())
                .title(request.getTitle())
                .rootCause(request.getRootCause())
                .correctiveAction(request.getCorrectiveAction())
                .preventiveAction(request.getPreventiveAction())
                .status(request.getStatus() != null ? request.getStatus() : "OPEN")
                .dueDate(request.getDueDate())
                .ownerId(request.getOwnerId())
                .build();
        return toCapaResponse(capaRepository.save(capa));
    }

    // ── CERTIFICATIONS ────────────────────────
    public List<CertificationResponse> getAllCertifications() {
        return certificationRepository.findAll().stream().map(this::toCertResponse).collect(Collectors.toList());
    }

    public List<CertificationResponse> getExpiringSoon(int days) {
        LocalDate threshold = LocalDate.now().plusDays(days);
        return certificationRepository.findExpiringSoon(threshold).stream().map(this::toCertResponse).collect(Collectors.toList());
    }

    // ── MAPPERS ───────────────────────────────
    private NcrResponse toNcrResponse(Ncr n) {
        return NcrResponse.builder()
                .id(n.getId()).ncrNumber(n.getNcrNumber()).title(n.getTitle())
                .description(n.getDescription()).customerId(n.getCustomerId())
                .programId(n.getProgramId()).siteId(n.getSiteId())
                .status(n.getStatus()).severity(n.getSeverity())
                .detectedDate(n.getDetectedDate()).containmentAction(n.getContainmentAction())
                .assignedToId(n.getAssignedToId()).closedDate(n.getClosedDate())
                .createdAt(n.getCreatedAt()).updatedAt(n.getUpdatedAt()).build();
    }

    private CapaResponse toCapaResponse(Capa c) {
        return CapaResponse.builder()
                .id(c.getId()).capaNumber(c.getCapaNumber()).ncrId(c.getNcrId())
                .title(c.getTitle()).rootCause(c.getRootCause())
                .correctiveAction(c.getCorrectiveAction()).preventiveAction(c.getPreventiveAction())
                .status(c.getStatus()).dueDate(c.getDueDate()).closedDate(c.getClosedDate())
                .ownerId(c.getOwnerId()).createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt()).build();
    }

    private CertificationResponse toCertResponse(Certification c) {
        return CertificationResponse.builder()
                .id(c.getId()).certNumber(c.getCertNumber()).name(c.getName())
                .standard(c.getStandard()).issuingBody(c.getIssuingBody())
                .siteId(c.getSiteId()).status(c.getStatus())
                .issueDate(c.getIssueDate()).expiryDate(c.getExpiryDate())
                .documentUrl(c.getDocumentUrl())
                .createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt()).build();
    }
}