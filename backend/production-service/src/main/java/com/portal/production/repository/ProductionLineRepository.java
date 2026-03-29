package com.portal.production.repository;

import com.portal.production.model.ProductionLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductionLineRepository extends JpaRepository<ProductionLine, UUID> {

    Optional<ProductionLine> findByCode(String code);

    List<ProductionLine> findBySiteId(UUID siteId);

    List<ProductionLine> findBySiteIdAndIsActiveTrue(UUID siteId);

    boolean existsByCode(String code);
}