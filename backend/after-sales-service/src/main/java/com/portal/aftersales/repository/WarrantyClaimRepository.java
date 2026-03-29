package com.portal.aftersales.repository;

import com.portal.aftersales.model.WarrantyClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarrantyClaimRepository extends JpaRepository<WarrantyClaim, UUID> {
    Optional<WarrantyClaim> findByClaimNumber(String claimNumber);
    List<WarrantyClaim> findByCustomerId(UUID customerId);
    List<WarrantyClaim> findByStatus(String status);
    boolean existsByClaimNumber(String claimNumber);
}