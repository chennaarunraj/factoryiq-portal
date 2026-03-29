package com.portal.aftersales.repository;

import com.portal.aftersales.model.RepairCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepairCaseRepository extends JpaRepository<RepairCase, UUID> {
    Optional<RepairCase> findByCaseNumber(String caseNumber);
    List<RepairCase> findByRmaId(UUID rmaId);
    List<RepairCase> findByStatus(String status);
    boolean existsByCaseNumber(String caseNumber);
}