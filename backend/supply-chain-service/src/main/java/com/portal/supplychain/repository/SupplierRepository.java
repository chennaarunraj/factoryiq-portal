package com.portal.supplychain.repository;

import com.portal.supplychain.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    Optional<Supplier> findByCode(String code);
    List<Supplier> findByIsActiveTrue();
    boolean existsByCode(String code);
}