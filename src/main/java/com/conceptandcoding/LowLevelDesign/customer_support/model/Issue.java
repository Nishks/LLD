package com.conceptandcoding.LowLevelDesign.customer_support.model;

import com.conceptandcoding.LowLevelDesign.customer_support.enums.IssueStatus;
import com.conceptandcoding.LowLevelDesign.customer_support.enums.IssueType;

public class Issue {
    private final String id;
    private final String transactionId;
    private final IssueType issueType;
    private final String subject;
    private final String description;
    private final String email;

    private IssueStatus status;
    private String resolution;
    private String assignedAgentId;

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public String getId(){
        return id;
    }

    public void setAssignedAgentId(String assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getResolution() {
        return resolution;
    }

    public String getAssignedAgentId() {
        return assignedAgentId;
    }

    public Issue(String id, String transactionId, IssueType issueType, String subject, String description, String email) {
        this.id = id;
        this.transactionId = transactionId;
        this.issueType = issueType;
        this.subject = subject;
        this.description = description;
        this.email = email;
    }
}
