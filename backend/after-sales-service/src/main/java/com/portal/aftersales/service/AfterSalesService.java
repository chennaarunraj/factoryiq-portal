package com.portal.aftersales.service;

import com.portal.aftersales.exception.ResourceNotFoundException;
import com.portal.aftersales.model.*;
import com.portal.aftersales.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AfterSalesService {

    private final RmaRepository rmaRepository;
    private final RepairCaseRepository repairCaseRepository;
    private final WarrantyClaimRepository warrantyClaimRepository;

    // ── RMA ──────────────────────────────────
    public List<Rma> getAllRmas() {
        return rmaRepository.findAll();
    }

    public List<Rma> getRmasByCustomer(UUID customerId) {
        return rmaRepository.findByCustomerId(customerId);
    }

    public Rma getRmaById(UUID id) {
        return rmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RMA not found: " + id));
    }

    @Transactional
    public Rma createRma(Rma rma) {
        if (rmaRepository.existsByRmaNumber(rma.getRmaNumber())) {
            throw new IllegalArgumentException("RMA number already exists: " + rma.getRmaNumber());
        }
        return rmaRepository.save(rma);
    }

    @Transactional
    public Rma updateRmaStatus(UUID id, String status) {
        Rma rma = rmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RMA not found: " + id));
        rma.setStatus(status);
        if ("CLOSED".equals(status)) rma.setClosedDate(LocalDate.now());
        return rmaRepository.save(rma);
    }

    // ── REPAIR CASES ─────────────────────────
    public List<RepairCase> getAllRepairCases() {
        return repairCaseRepository.findAll();
    }

    public List<RepairCase> getRepairCasesByRma(UUID rmaId) {
        return repairCaseRepository.findByRmaId(rmaId);
    }

    @Transactional
    public RepairCase createRepairCase(RepairCase repairCase) {
        if (repairCaseRepository.existsByCaseNumber(repairCase.getCaseNumber())) {
            throw new IllegalArgumentException("Case number already exists: " + repairCase.getCaseNumber());
        }
        return repairCaseRepository.save(repairCase);
    }

    // ── WARRANTY CLAIMS ───────────────────────
    public List<WarrantyClaim> getAllWarrantyClaims() {
        return warrantyClaimRepository.findAll();
    }

    public List<WarrantyClaim> getWarrantyClaimsByCustomer(UUID customerId) {
        return warrantyClaimRepository.findByCustomerId(customerId);
    }

    @Transactional
    public WarrantyClaim createWarrantyClaim(WarrantyClaim claim) {
        if (warrantyClaimRepository.existsByClaimNumber(claim.getClaimNumber())) {
            throw new IllegalArgumentException("Claim number already exists: " + claim.getClaimNumber());
        }
        return warrantyClaimRepository.save(claim);
    }

    @Transactional
    public WarrantyClaim updateClaimStatus(UUID id, String status) {
        WarrantyClaim claim = warrantyClaimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warranty claim not found: " + id));
        claim.setStatus(status);
        if ("CLOSED".equals(status)) claim.setResolvedDate(LocalDate.now());
        return warrantyClaimRepository.save(claim);
    }
}