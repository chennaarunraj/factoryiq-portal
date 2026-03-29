package com.portal.dynamics.service;

import com.portal.dynamics.dto.D365AccountDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class DynamicsClientService {

    @Value("${dynamics.mock-mode:true}")
    private boolean mockMode;

    @Value("${dynamics.resource-url:}")
    private String resourceUrl;

    // ── CREATE ACCOUNT ────────────────────────
    public D365AccountDto createAccount(D365AccountDto account) {
        if (mockMode) return mockCreateAccount(account);
        return callD365CreateAccount(account);
    }

    // ── UPDATE ACCOUNT ────────────────────────
    public D365AccountDto updateAccount(String d365Id, D365AccountDto account) {
        if (mockMode) return mockUpdateAccount(d365Id, account);
        return callD365UpdateAccount(d365Id, account);
    }

    // ── CREATE WORK ORDER ─────────────────────
    public Map<String, Object> createWorkOrder(Map<String, Object> workOrder) {
        if (mockMode) return mockCreateWorkOrder(workOrder);
        return callD365CreateWorkOrder(workOrder);
    }

    // ── CREATE CASE (RMA) ─────────────────────
    public Map<String, Object> createCase(Map<String, Object> caseData) {
        if (mockMode) return mockCreateCase(caseData);
        return callD365CreateCase(caseData);
    }

    // ── CREATE OPPORTUNITY (Program) ──────────
    public Map<String, Object> createOpportunity(Map<String, Object> opportunity) {
        if (mockMode) return mockCreateOpportunity(opportunity);
        return callD365CreateOpportunity(opportunity);
    }

    // ── MOCK IMPLEMENTATIONS ──────────────────
    private D365AccountDto mockCreateAccount(D365AccountDto account) {
        String mockId = "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[MOCK D365] Created Account: {} → {}", account.getName(), mockId);
        account.setD365Id(mockId);
        account.setSynced(true);
        return account;
    }

    private D365AccountDto mockUpdateAccount(String d365Id, D365AccountDto account) {
        log.info("[MOCK D365] Updated Account: {}", d365Id);
        account.setD365Id(d365Id);
        account.setSynced(true);
        return account;
    }

    private Map<String, Object> mockCreateWorkOrder(Map<String, Object> workOrder) {
        String mockId = "WO-D365-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[MOCK D365] Created Work Order: {} → {}", workOrder.get("orderNumber"), mockId);
        workOrder.put("d365Id", mockId);
        workOrder.put("synced", true);
        return workOrder;
    }

    private Map<String, Object> mockCreateCase(Map<String, Object> caseData) {
        String mockId = "CASE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[MOCK D365] Created Case: {} → {}", caseData.get("rmaNumber"), mockId);
        caseData.put("d365Id", mockId);
        caseData.put("synced", true);
        return caseData;
    }

    private Map<String, Object> mockCreateOpportunity(Map<String, Object> opportunity) {
        String mockId = "OPP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[MOCK D365] Created Opportunity: {} → {}", opportunity.get("name"), mockId);
        opportunity.put("d365Id", mockId);
        opportunity.put("synced", true);
        return opportunity;
    }

    // ── REAL D365 API (implement when account available) ──
    private D365AccountDto callD365CreateAccount(D365AccountDto account) {
        // TODO: POST {resourceUrl}/api/data/{apiVersion}/accounts
        // Headers: Authorization: Bearer {accessToken}
        // Body: { name, industrycode, address1_country }
        log.warn("Real D365 API not implemented. Set mock-mode: false only after implementation.");
        return account;
    }

    private D365AccountDto callD365UpdateAccount(String d365Id, D365AccountDto account) {
        // TODO: PATCH {resourceUrl}/api/data/{apiVersion}/accounts({d365Id})
        return account;
    }

    private Map<String, Object> callD365CreateWorkOrder(Map<String, Object> workOrder) {
        // TODO: POST {resourceUrl}/api/data/{apiVersion}/msdyn_workorders
        return workOrder;
    }

    private Map<String, Object> callD365CreateCase(Map<String, Object> caseData) {
        // TODO: POST {resourceUrl}/api/data/{apiVersion}/incidents
        return caseData;
    }

    private Map<String, Object> callD365CreateOpportunity(Map<String, Object> opportunity) {
        // TODO: POST {resourceUrl}/api/data/{apiVersion}/opportunities
        return opportunity;
    }
}