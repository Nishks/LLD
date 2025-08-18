package com.conceptandcoding.LowLevelDesign.customer_support.model;

import com.conceptandcoding.LowLevelDesign.customer_support.enums.IssueType;

import java.util.*;

public class Agent {
    private final String id;
    private final String email;
    private final String name;
    private final Set<IssueType> expertise;

    private String assignedIssueId;
    private final Queue<String> waitingList = new LinkedList<>();
    private final List<String> history = new ArrayList<>();

    public void setAssignedIssueId(String assignedIssueId) {
        this.assignedIssueId = assignedIssueId;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Set<IssueType> getExpertise() {
        return expertise;
    }

    public String getAssignedIssueId() {
        return assignedIssueId;
    }

    public Agent(String id, String email, String name, Set<IssueType> expertise) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.expertise = expertise;
    }

    public boolean isAvailable(){
        return assignedIssueId == null;
    }

    public Queue<String> getWaitingList() {
        return waitingList;
    }

    public List<String> getHistory() {
        return history;
    }
}
