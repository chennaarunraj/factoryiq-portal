package com.portal.production.repository;

import com.portal.production.model.WorkOrderOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkOrderOperationRepository extends JpaRepository<WorkOrderOperation, UUID> {

    // All operations for a work order ordered by sequence
    List<WorkOrderOperation> findByWorkOrderIdOrderBySequenceNoAsc(UUID workOrderId);

    // All operations on a specific line
    List<WorkOrderOperation> findByLineId(UUID lineId);

    // All operations by status
    List<WorkOrderOperation> findByStatus(String status);

    // All in-progress operations on a line
    List<WorkOrderOperation> findByLineIdAndStatus(UUID lineId, String status);

    // Count completed operations for a work order
    long countByWorkOrderIdAndStatus(UUID workOrderId, String status);

    // Get yield summary for a line
    @Query("SELECT AVG(w.yieldPct) FROM WorkOrderOperation w WHERE w.lineId = :lineId AND w.yieldPct IS NOT NULL")
    Double getAverageYieldByLine(@Param("lineId") UUID lineId);
}