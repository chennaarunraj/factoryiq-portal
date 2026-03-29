package com.portal.aftersales.repository;

import com.portal.aftersales.model.Rma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RmaRepository extends JpaRepository<Rma, UUID> {
    Optional<Rma> findByRmaNumber(String rmaNumber);
    List<Rma> findByCustomerId(UUID customerId);
    List<Rma> findByStatus(String status);
    boolean existsByRmaNumber(String rmaNumber);
}