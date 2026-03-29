package com.portal.supplychain.repository;

import com.portal.supplychain.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByItemCodeAndSiteId(String itemCode, UUID siteId);
    List<Inventory> findBySiteId(UUID siteId);
    List<Inventory> findByItemCode(String itemCode);

    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.minThreshold")
    List<Inventory> findBelowMinThreshold();
}