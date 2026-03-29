package com.portal.production.repository;

import com.portal.production.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {

    Optional<WorkOrder> findByOrderNumber(String orderNumber);

    List<WorkOrder> findByCustomerId(UUID customerId);

    List<WorkOrder> findBySiteId(UUID siteId);

    List<WorkOrder> findByStatus(String status);

    List<WorkOrder> findByProgramId(UUID programId);

    boolean existsByOrderNumber(String orderNumber);
}