package com.conceptandcoding.LowLevelDesign.customer_support.strategy_assignment;

import com.conceptandcoding.LowLevelDesign.customer_support.model.Agent;
import com.conceptandcoding.LowLevelDesign.customer_support.model.Issue;

import java.util.List;

public class DefaultAssignmentStrategy implements AssignmentStrategy{
    @Override
    public Agent assign(List<Agent> agents, Issue issue) {
        for(Agent agent : agents){
            if(agent.isAvailable() && agent.getExpertise().contains(issue.getIssueType())){
                return agent;
            }
        }
        return null;
    }
}
