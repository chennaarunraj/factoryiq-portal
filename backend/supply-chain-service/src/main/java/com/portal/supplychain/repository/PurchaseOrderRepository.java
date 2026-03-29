package com.portal.supplychain.repository;

import com.portal.supplychain.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    List<PurchaseOrder> findBySupplierId(UUID supplierId);
    List<PurchaseOrder> findByCustomerId(UUID customerId);
    List<PurchaseOrder> findByStatus(String status);
    Optional<PurchaseOrder> findByDynamicsPoId(String dynamicsPoId);
    boolean existsByPoNumber(String poNumber);
}