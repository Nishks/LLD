package com.conceptandcoding.LowLevelDesign.customer_support.service;

import com.conceptandcoding.LowLevelDesign.customer_support.enums.IssueStatus;
import com.conceptandcoding.LowLevelDesign.customer_support.enums.IssueType;
import com.conceptandcoding.LowLevelDesign.customer_support.model.Agent;
import com.conceptandcoding.LowLevelDesign.customer_support.model.Issue;
import com.conceptandcoding.LowLevelDesign.customer_support.repository.AgentRepository;
import com.conceptandcoding.LowLevelDesign.customer_support.repository.IssueRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IssueService {
    private final IssueRepository issueRepository;
    private final AgentRepository agentRepository;

    public IssueService(IssueRepository issueRepository, AgentRepository agentRepository) {
        this.issueRepository = issueRepository;
        this.agentRepository = agentRepository;
    }

    public Issue createIssue(String transactionId, IssueType issueType, String subject, String description, String email){
        String id = "I" + UUID.randomUUID().toString().substring(0,6);
        Issue issue = new Issue(id,transactionId, issueType, subject, description, email);
        issueRepository.save(issue);
        System.out.println(">>> Issue " + issue.getId() + " created against transaction \"" + transactionId);
        return issue;
    }

    public List<Issue> getIssue(Map<String, String> filter){
        return issueRepository.getAll().stream().filter(issue -> {
            if(filter.containsKey("email") && !issue.getEmail().equalsIgnoreCase(filter.get("email"))) return false;
            if(filter.containsKey("type") && !issue.getIssueType().name().equalsIgnoreCase(filter.get("type"))) return false;
            if(filter.containsKey("status") && !issue.getStatus().name().equalsIgnoreCase(filter.get("status"))) return false;
            return true;
        }).toList();
    }

    public void updateIssue(String issueId, IssueStatus status, String resolution) throws Exception {
        Issue issue = issueRepository.getById(issueId);
        if(issue == null) throw new Exception("Issue not found");
        issue.setStatus(status);
        issue.setResolution(resolution);
        System.out.println(">>> " + issueId + " status update to " + issue.getStatus());
    }

    public void resolveIssue(String issueId, String resolution) throws Exception {
        Issue issue = issueRepository.getById(issueId);
        if(issue == null) throw new Exception("Issue not found");
        issue.setStatus(IssueStatus.RESOLVED);
        issue.setResolution(resolution);

        if(issue.getAssignedAgentId() != null){
            Agent agent = agentRepository.getById(issue.getAssignedAgentId());
            if(agent != null){
                agent.getHistory().add(issue.getId());
                agent.setAssignedIssueId(null);
            }
        }

        System.out.println(">>> " + issueId + " issue marked resolved ");
    }
}
