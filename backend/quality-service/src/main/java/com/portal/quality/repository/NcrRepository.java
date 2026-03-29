package com.portal.quality.repository;

import com.portal.quality.model.Ncr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NcrRepository extends JpaRepository<Ncr, UUID> {
    Optional<Ncr> findByNcrNumber(String ncrNumber);
    List<Ncr> findByCustomerId(UUID customerId);
    List<Ncr> findByProgramId(UUID programId);
    List<Ncr> findByStatus(String status);
    List<Ncr> findBySeverity(String severity);
    List<Ncr> findByCustomerIdAndStatus(UUID customerId, String status);
    boolean existsByNcrNumber(String ncrNumber);
}