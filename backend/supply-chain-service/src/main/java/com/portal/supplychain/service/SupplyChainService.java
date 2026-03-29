package com.portal.supplychain.service;

import com.portal.supplychain.exception.ResourceNotFoundException;
import com.portal.supplychain.model.*;
import com.portal.supplychain.repository.*;
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
public class SupplyChainService {

    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final ShipmentRepository shipmentRepository;

    // ── SUPPLIERS ─────────────────────────────
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByIsActiveTrue();
    }

    public Supplier getSupplierById(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        if (supplierRepository.existsByCode(supplier.getCode())) {
            throw new IllegalArgumentException("Supplier code already exists: " + supplier.getCode());
        }
        return supplierRepository.save(supplier);
    }

    // ── PURCHASE ORDERS ───────────────────────
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public List<PurchaseOrder> getPurchaseOrdersByCustomer(UUID customerId) {
        return purchaseOrderRepository.findByCustomerId(customerId);
    }

    public PurchaseOrder getPurchaseOrderById(UUID id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found: " + id));
    }

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder po) {
        if (purchaseOrderRepository.existsByPoNumber(po.getPoNumber())) {
            throw new IllegalArgumentException("PO number already exists: " + po.getPoNumber());
        }
        return purchaseOrderRepository.save(po);
    }

    @Transactional
    public PurchaseOrder updatePoStatus(UUID id, String status) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found: " + id));
        po.setStatus(status);
        return purchaseOrderRepository.save(po);
    }

    // ── INVENTORY ─────────────────────────────
    public List<Inventory> getInventoryBySite(UUID siteId) {
        return inventoryRepository.findBySiteId(siteId);
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findBelowMinThreshold();
    }

    // ── SHIPMENTS ─────────────────────────────
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    public Shipment getShipmentByTracking(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + trackingNumber));
    }

    @Transactional
    public Shipment createShipment(Shipment shipment) {
        if (shipmentRepository.existsByTrackingNumber(shipment.getTrackingNumber())) {
            throw new IllegalArgumentException("Tracking number already exists: " + shipment.getTrackingNumber());
        }
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment updateShipmentStatus(UUID id, String status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + id));
        shipment.setStatus(status);
        return shipmentRepository.save(shipment);
    }
}