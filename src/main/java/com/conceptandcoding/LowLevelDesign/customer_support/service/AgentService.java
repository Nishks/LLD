package com.conceptandcoding.LowLevelDesign.customer_support.service;

import com.conceptandcoding.LowLevelDesign.customer_support.enums.IssueType;
import com.conceptandcoding.LowLevelDesign.customer_support.model.Agent;
import com.conceptandcoding.LowLevelDesign.customer_support.repository.AgentRepository;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class AgentService {
    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public void addAgent(String email, String name, List<IssueType> issueTypes){
        String id = "A" + UUID.randomUUID().toString().substring(0,6);
        Agent agent = new Agent(id, email, name, new HashSet<>(issueTypes));
        agentRepository.save(agent);
        System.out.println(">>> Agent " + id + " created");
    }

    public void viewAgentsWorkHistory(){
        for(Agent agent : agentRepository.getAll()){
            System.out.println(agent.getId() + " -> " + agent.getHistory());
        }
    }
}
