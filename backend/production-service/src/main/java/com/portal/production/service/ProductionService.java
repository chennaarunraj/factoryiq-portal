// package com.portal.production.service;

// import com.portal.production.dto.*;
// import com.portal.production.exception.ResourceNotFoundException;
// import com.portal.production.model.ProductionLine;
// import com.portal.production.model.WorkOrderOperation;
// import com.portal.production.repository.ProductionLineRepository;
// import com.portal.production.repository.WorkOrderOperationRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.OffsetDateTime;
// import java.util.List;
// import java.util.UUID;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// @Transactional(readOnly = true)
// public class ProductionService {

//     private final ProductionLineRepository productionLineRepository;
//     private final WorkOrderOperationRepository workOrderOperationRepository;
//     private final KafkaTemplate<String, Object> kafkaTemplate;

//     // ─────────────────────────────────────────
//     // PRODUCTION LINES
//     // ─────────────────────────────────────────

//     public List<ProductionLineResponse> getAllLines() {
//         return productionLineRepository.findAll()
//                 .stream()
//                 .map(this::toLineResponse)
//                 .collect(Collectors.toList());
//     }

//     public List<ProductionLineResponse> getLinesBySite(UUID siteId) {
//         return productionLineRepository.findBySiteId(siteId)
//                 .stream()
//                 .map(this::toLineResponse)
//                 .collect(Collectors.toList());
//     }

//     public ProductionLineResponse getLineById(UUID id) {
//         ProductionLine line = productionLineRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Production line not found: " + id));
//         return toLineResponse(line);
//     }

//     @Transactional
//     public ProductionLineResponse createLine(ProductionLineRequest request) {
//         if (productionLineRepository.existsByCode(request.getCode())) {
//             throw new IllegalArgumentException("Line code already exists: " + request.getCode());
//         }
//         ProductionLine line = ProductionLine.builder()
//                 .siteId(request.getSiteId())
//                 .code(request.getCode())
//                 .name(request.getName())
//                 .capacityPerShift(request.getCapacityPerShift())
//                 .build();
//         ProductionLine saved = productionLineRepository.save(line);
//         log.info("Production line created: {}", saved.getId());
//         return toLineResponse(saved);
//     }

//     // ─────────────────────────────────────────
//     // WORK ORDER OPERATIONS
//     // ─────────────────────────────────────────

//     public List<WorkOrderOperationResponse> getOperationsByWorkOrder(UUID workOrderId) {
//         return workOrderOperationRepository
//                 .findByWorkOrderIdOrderBySequenceNoAsc(workOrderId)
//                 .stream()
//                 .map(this::toOperationResponse)
//                 .collect(Collectors.toList());
//     }

//     public List<WorkOrderOperationResponse> getOperationsByLine(UUID lineId) {
//         return workOrderOperationRepository.findByLineId(lineId)
//                 .stream()
//                 .map(this::toOperationResponse)
//                 .collect(Collectors.toList());
//     }

//     public WorkOrderOperationResponse getOperationById(UUID id) {
//         WorkOrderOperation op = workOrderOperationRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Operation not found: " + id));
//         return toOperationResponse(op);
//     }

//     @Transactional
//     public WorkOrderOperationResponse createOperation(WorkOrderOperationRequest request) {
//         WorkOrderOperation op = WorkOrderOperation.builder()
//                 .workOrderId(request.getWorkOrderId())
//                 .lineId(request.getLineId())
//                 .operationName(request.getOperationName())
//                 .sequenceNo(request.getSequenceNo())
//                 .status(request.getStatus() != null ? request.getStatus() : "PENDING")
//                 .plannedQty(request.getPlannedQty() != null ? request.getPlannedQty() : 0)
//                 .completedQty(request.getCompletedQty() != null ? request.getCompletedQty() : 0)
//                 .rejectedQty(request.getRejectedQty() != null ? request.getRejectedQty() : 0)
//                 .yieldPct(request.getYieldPct())
//                 .build();
//         WorkOrderOperation saved = workOrderOperationRepository.save(op);
//         log.info("Operation created: {}", saved.getId());

//         // Publish Kafka event for real-time dashboard updates
//         kafkaTemplate.send("production.wip", saved.getWorkOrderId().toString(),
//                 toOperationResponse(saved));
//         log.debug("Kafka event published: production.wip");

//         return toOperationResponse(saved);
//     }

//     @Transactional
//     public WorkOrderOperationResponse updateOperationStatus(UUID id, String status) {
//         WorkOrderOperation op = workOrderOperationRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Operation not found: " + id));

//         op.setStatus(status);

//         if ("IN_PROGRESS".equals(status) && op.getStartedAt() == null) {
//             op.setStartedAt(OffsetDateTime.now());
//         }
//         if ("COMPLETED".equals(status)) {
//             op.setCompletedAt(OffsetDateTime.now());
//         }

//         WorkOrderOperation updated = workOrderOperationRepository.save(op);

//         // Publish Kafka event
//         kafkaTemplate.send("production.wip", updated.getWorkOrderId().toString(),
//                 toOperationResponse(updated));

//         log.info("Operation {} status updated to {}", id, status);
//         return toOperationResponse(updated);
//     }

//     // ─────────────────────────────────────────
//     // MAPPERS
//     // ─────────────────────────────────────────
//     private ProductionLineResponse toLineResponse(ProductionLine l) {
//         return ProductionLineResponse.builder()
//                 .id(l.getId())
//                 .siteId(l.getSiteId())
//                 .code(l.getCode())
//                 .name(l.getName())
//                 .capacityPerShift(l.getCapacityPerShift())
//                 .isActive(l.getIsActive())
//                 .createdAt(l.getCreatedAt())
//                 .updatedAt(l.getUpdatedAt())
//                 .build();
//     }

//     private WorkOrderOperationResponse toOperationResponse(WorkOrderOperation op) {
//         return WorkOrderOperationResponse.builder()
//                 .id(op.getId())
//                 .workOrderId(op.getWorkOrderId())
//                 .lineId(op.getLineId())
//                 .operationName(op.getOperationName())
//                 .sequenceNo(op.getSequenceNo())
//                 .status(op.getStatus())
//                 .plannedQty(op.getPlannedQty())
//                 .completedQty(op.getCompletedQty())
//                 .rejectedQty(op.getRejectedQty())
//                 .yieldPct(op.getYieldPct())
//                 .startedAt(op.getStartedAt())
//                 .completedAt(op.getCompletedAt())
//                 .createdAt(op.getCreatedAt())
//                 .updatedAt(op.getUpdatedAt())
//                 .build();
//     }
// }


package com.portal.production.service;

import com.portal.production.exception.ResourceNotFoundException;
import com.portal.production.model.ProductionLine;
import com.portal.production.model.WorkOrder;
import com.portal.production.model.WorkOrderOperation;
import com.portal.production.repository.ProductionLineRepository;
import com.portal.production.repository.WorkOrderOperationRepository;
import com.portal.production.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductionService {

    private final ProductionLineRepository productionLineRepository;
    private final WorkOrderOperationRepository workOrderOperationRepository;
    private final WorkOrderRepository workOrderRepository;

    // ── PRODUCTION LINES ──────────────────────
    public List<ProductionLine> getAllLines() {
        return productionLineRepository.findAll();
    }

    public List<ProductionLine> getLinesBySite(UUID siteId) {
        return productionLineRepository.findBySiteId(siteId);
    }

    // ── WORK ORDERS ───────────────────────────
    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    public List<WorkOrder> getWorkOrdersByStatus(String status) {
        return workOrderRepository.findByStatus(status);
    }

    public WorkOrder getWorkOrderById(UUID id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found: " + id));
    }

    @Transactional
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        if (workOrderRepository.existsByOrderNumber(workOrder.getOrderNumber())) {
            throw new IllegalArgumentException("Order number already exists: " + workOrder.getOrderNumber());
        }
        return workOrderRepository.save(workOrder);
    }

    @Transactional
    public WorkOrder updateWorkOrderStatus(UUID id, String status) {
        WorkOrder wo = workOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work order not found: " + id));
        wo.setStatus(status);
        return workOrderRepository.save(wo);
    }

    // ── OPERATIONS ────────────────────────────
    public List<WorkOrderOperation> getOperationsByWorkOrder(UUID workOrderId) {
        return workOrderOperationRepository.findByWorkOrderIdOrderBySequenceNoAsc(workOrderId);
    }

    @Transactional
    public WorkOrderOperation createOperation(WorkOrderOperation operation) {
        return workOrderOperationRepository.save(operation);
    }

    @Transactional
    public WorkOrderOperation updateOperationStatus(UUID id, String status) {
        WorkOrderOperation op = workOrderOperationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found: " + id));
        op.setStatus(status);
        return workOrderOperationRepository.save(op);
    }
}