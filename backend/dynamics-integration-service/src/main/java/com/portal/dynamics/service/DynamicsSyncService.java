package com.portal.dynamics.service;

import com.portal.dynamics.dto.D365AccountDto;
import com.portal.dynamics.dto.D365SyncResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicsSyncService {

    private final DynamicsClientService dynamicsClientService;
    private final JdbcTemplate jdbcTemplate;

    @Value("${dynamics.mock-mode:true}")
    private boolean mockMode;

    // ── SYNC ALL — runs every hour ────────────
    @Scheduled(fixedDelay = 3600000)
    public void syncAll() {
        log.info("Starting full Dynamics 365 sync (mock-mode: {})", mockMode);
        syncCustomers();
        syncPrograms();
        syncWorkOrders();
        syncRmas();
        log.info("Full Dynamics 365 sync completed");
    }

    // ── SYNC CUSTOMERS → D365 ACCOUNTS ───────
    public D365SyncResultDto syncCustomers() {
        log.info("Syncing Customers to D365 Accounts...");
        List<Map<String, Object>> customers = jdbcTemplate.queryForList(
            "SELECT id, code, name, industry, country FROM portal.customers WHERE is_active = true"
        );

        List<String> synced = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map<String, Object> customer : customers) {
            try {
                D365AccountDto account = D365AccountDto.builder()
                    .name((String) customer.get("name"))
                    .industry((String) customer.get("industry"))
                    .country((String) customer.get("country"))
                    .portalCustomerId(customer.get("id").toString())
                    .build();

                D365AccountDto result = dynamicsClientService.createAccount(account);
                synced.add(customer.get("id").toString());
                log.info("Customer {} synced as D365 Account {}", customer.get("code"), result.getD365Id());
            } catch (Exception e) {
                failed.add(customer.get("id").toString());
                log.error("Failed to sync Customer {}: {}", customer.get("code"), e.getMessage());
            }
        }

        return buildResult("CUSTOMER", customers.size(), synced, failed);
    }

    // ── SYNC PROGRAMS → D365 OPPORTUNITIES ───
    public D365SyncResultDto syncPrograms() {
        log.info("Syncing Programs to D365 Opportunities...");
        List<Map<String, Object>> programs = jdbcTemplate.queryForList(
            "SELECT id, code, name, status, health FROM portal.programs WHERE status = 'ACTIVE'"
        );

        List<String> synced = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map<String, Object> program : programs) {
            try {
                Map<String, Object> opportunity = new HashMap<>();
                opportunity.put("name", program.get("name"));
                opportunity.put("code", program.get("code"));
                opportunity.put("status", program.get("status"));
                opportunity.put("portalProgramId", program.get("id").toString());

                Map<String, Object> result = dynamicsClientService.createOpportunity(opportunity);
                synced.add(program.get("id").toString());
                log.info("Program {} synced as D365 Opportunity {}", program.get("code"), result.get("d365Id"));
            } catch (Exception e) {
                failed.add(program.get("id").toString());
                log.error("Failed to sync Program {}: {}", program.get("code"), e.getMessage());
            }
        }

        return buildResult("PROGRAM", programs.size(), synced, failed);
    }

    // ── SYNC WORK ORDERS → D365 WORK ORDERS ──
    public D365SyncResultDto syncWorkOrders() {
        log.info("Syncing Work Orders to D365...");
        List<Map<String, Object>> workOrders = jdbcTemplate.queryForList(
            "SELECT id, order_number, description, status, planned_qty " +
            "FROM portal.work_orders WHERE status IN ('PLANNED','IN_PROGRESS') LIMIT 50"
        );

        List<String> synced = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map<String, Object> wo : workOrders) {
            try {
                Map<String, Object> d365Wo = new HashMap<>();
                d365Wo.put("orderNumber", wo.get("order_number"));
                d365Wo.put("description", wo.get("description"));
                d365Wo.put("status", wo.get("status"));
                d365Wo.put("plannedQty", wo.get("planned_qty"));
                d365Wo.put("portalWorkOrderId", wo.get("id").toString());

                Map<String, Object> result = dynamicsClientService.createWorkOrder(d365Wo);
                synced.add(wo.get("id").toString());
                log.info("Work Order {} synced as D365 WO {}", wo.get("order_number"), result.get("d365Id"));
            } catch (Exception e) {
                failed.add(wo.get("id").toString());
                log.error("Failed to sync Work Order {}: {}", wo.get("order_number"), e.getMessage());
            }
        }

        return buildResult("WORK_ORDER", workOrders.size(), synced, failed);
    }

    // ── SYNC RMAs → D365 CASES ────────────────
    public D365SyncResultDto syncRmas() {
        log.info("Syncing RMAs to D365 Cases...");
        List<Map<String, Object>> rmas = jdbcTemplate.queryForList(
            "SELECT id, rma_number, description, status, reason_code " +
            "FROM portal.rmas WHERE status NOT IN ('CLOSED','CANCELLED') LIMIT 50"
        );

        List<String> synced = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map<String, Object> rma : rmas) {
            try {
                Map<String, Object> caseData = new HashMap<>();
                caseData.put("rmaNumber", rma.get("rma_number"));
                caseData.put("description", rma.get("description"));
                caseData.put("status", rma.get("status"));
                caseData.put("reasonCode", rma.get("reason_code"));
                caseData.put("portalRmaId", rma.get("id").toString());

                Map<String, Object> result = dynamicsClientService.createCase(caseData);
                synced.add(rma.get("id").toString());
                log.info("RMA {} synced as D365 Case {}", rma.get("rma_number"), result.get("d365Id"));
            } catch (Exception e) {
                failed.add(rma.get("id").toString());
                log.error("Failed to sync RMA {}: {}", rma.get("rma_number"), e.getMessage());
            }
        }

        return buildResult("RMA", rmas.size(), synced, failed);
    }

    // ── HELPER ────────────────────────────────
    private D365SyncResultDto buildResult(String entityType, int total,
                                          List<String> synced, List<String> failed) {
        return D365SyncResultDto.builder()
            .entityType(entityType)
            .totalProcessed(total)
            .successCount(synced.size())
            .failureCount(failed.size())
            .syncedIds(synced)
            .failedIds(failed)
            .syncedAt(OffsetDateTime.now())
            .mockMode(mockMode)
            .build();
    }
}