package com.conceptandcoding.LowLevelDesign.customer_support.service;

import com.conceptandcoding.LowLevelDesign.customer_support.enums.IssueStatus;
import com.conceptandcoding.LowLevelDesign.customer_support.model.Agent;
import com.conceptandcoding.LowLevelDesign.customer_support.model.Issue;
import com.conceptandcoding.LowLevelDesign.customer_support.repository.AgentRepository;
import com.conceptandcoding.LowLevelDesign.customer_support.repository.IssueRepository;
import com.conceptandcoding.LowLevelDesign.customer_support.strategy_assignment.AssignmentStrategy;

import java.util.ArrayList;
import java.util.List;

public class AssignmentService {
    private final AgentRepository agentRepository;
    private final IssueRepository issueRepository;
    private final AssignmentStrategy strategy;


    public AssignmentService(AgentRepository agentRepository, IssueRepository issueRepository, AssignmentStrategy strategy) {
        this.agentRepository = agentRepository;
        this.issueRepository = issueRepository;
        this.strategy = strategy;
    }

    public void assignIssue(String issueId){
        Issue issue = issueRepository.getById(issueId);
        if(issue == null) throw new IllegalStateException("Issue not found");

        List<Agent> agents = new ArrayList<>(agentRepository.getAll());
        Agent assigned = strategy.assign(agents,issue);

        if(assigned != null){
            assigned.setAssignedIssueId(issue.getId());
            issue.setAssignedAgentId(assigned.getId());
            System.out.println(">>> Issue " + issueId + " assigned to agent " + assigned.getId());
        }else {
            for(Agent agent : agents){
                if(agent.getExpertise().contains(issue.getIssueType())){
                    agent.getWaitingList().add(issue.getId());
                    issue.setStatus(IssueStatus.WAITING);
                    System.out.println(">>> Issue " + issueId + " added to waitList of Agent " + agent.getId());
                    return;
                }
            }
            System.out.println(">>> No agent found with expertise for issue " + issueId);
        }

    }
}
