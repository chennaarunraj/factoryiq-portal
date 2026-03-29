package com.portal.program.repository;

import com.portal.program.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, UUID> {

    // Find by code
    Optional<Program> findByCode(String code);

    // Find all programs for a specific customer
    List<Program> findByCustomerId(UUID customerId);

    // Find by customer and status
    List<Program> findByCustomerIdAndStatus(UUID customerId, String status);

    // Find by site
    List<Program> findBySiteId(UUID siteId);

    // Find by health status
    List<Program> findByHealth(String health);

    // Find by Jira project key
    Optional<Program> findByJiraProjectKey(String jiraProjectKey);

    // Check if code exists
    boolean existsByCode(String code);

    // Find all active programs for a customer (custom query)
    @Query("SELECT p FROM Program p WHERE p.customer.id = :customerId AND p.status != 'CANCELLED'")
    List<Program> findActiveByCustomerId(@Param("customerId") UUID customerId);

    // Portfolio view - all programs with health summary
    @Query("SELECT p FROM Program p JOIN FETCH p.customer JOIN FETCH p.site WHERE p.site.id = :siteId")
    List<Program> findBySiteIdWithDetails(@Param("siteId") UUID siteId);
}