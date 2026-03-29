package com.portal.jira.service;

import com.portal.jira.dto.JiraIssueDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Slf4j
public class JiraClientService {

    @Value("${jira.mock-mode:true}")
    private boolean mockMode;

    @Value("${jira.base-url:}")
    private String baseUrl;

    @Value("${jira.api-token:}")
    private String apiToken;

    @Value("${jira.user-email:}")
    private String userEmail;

    // ── CREATE ISSUE ──────────────────────────
    public JiraIssueDto createIssue(JiraIssueDto issue) {
        if (mockMode) {
            return mockCreateIssue(issue);
        }
        // TODO: implement real Jira API call when account is available
        return callJiraCreateIssue(issue);
    }

    // ── UPDATE ISSUE ──────────────────────────
    public JiraIssueDto updateIssue(String jiraKey, JiraIssueDto issue) {
        if (mockMode) {
            return mockUpdateIssue(jiraKey, issue);
        }
        return callJiraUpdateIssue(jiraKey, issue);
    }

    // ── GET ISSUE ─────────────────────────────
    public JiraIssueDto getIssue(String jiraKey) {
        if (mockMode) {
            return mockGetIssue(jiraKey);
        }
        return callJiraGetIssue(jiraKey);
    }

    // ── MOCK IMPLEMENTATIONS ──────────────────
    private JiraIssueDto mockCreateIssue(JiraIssueDto issue) {
        String mockKey = "MFG-" + (int)(Math.random() * 1000 + 100);
        log.info("[MOCK] Created Jira issue: {} for {} - {}",
                mockKey, issue.getPortalEntityType(), issue.getPortalEntityId());
        issue.setJiraKey(mockKey);
        issue.setStatus("To Do");
        issue.setSynced(true);
        issue.setCreatedAt(OffsetDateTime.now());
        issue.setUpdatedAt(OffsetDateTime.now());
        return issue;
    }

    private JiraIssueDto mockUpdateIssue(String jiraKey, JiraIssueDto issue) {
        log.info("[MOCK] Updated Jira issue: {} for {} - {}",
                jiraKey, issue.getPortalEntityType(), issue.getPortalEntityId());
        issue.setJiraKey(jiraKey);
        issue.setSynced(true);
        issue.setUpdatedAt(OffsetDateTime.now());
        return issue;
    }

    private JiraIssueDto mockGetIssue(String jiraKey) {
        log.info("[MOCK] Got Jira issue: {}", jiraKey);
        return JiraIssueDto.builder()
                .jiraKey(jiraKey)
                .status("In Progress")
                .synced(true)
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    // ── REAL JIRA API (implement when account available) ──
    private JiraIssueDto callJiraCreateIssue(JiraIssueDto issue) {
        // TODO: implement using RestTemplate or WebClient
        // POST {baseUrl}/rest/api/3/issue
        // Headers: Authorization: Basic base64(email:apiToken)
        // Body: { fields: { project: { key }, summary, description, issuetype: { name } } }
        log.warn("Real Jira API not implemented yet. Switch mock-mode to false only after implementation.");
        return issue;
    }

    private JiraIssueDto callJiraUpdateIssue(String jiraKey, JiraIssueDto issue) {
        // TODO: PUT {baseUrl}/rest/api/3/issue/{jiraKey}
        return issue;
    }

    private JiraIssueDto callJiraGetIssue(String jiraKey) {
        // TODO: GET {baseUrl}/rest/api/3/issue/{jiraKey}
        return JiraIssueDto.builder().jiraKey(jiraKey).build();
    }
}