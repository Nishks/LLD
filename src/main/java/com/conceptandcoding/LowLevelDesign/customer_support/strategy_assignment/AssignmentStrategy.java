package com.conceptandcoding.LowLevelDesign.customer_support.strategy_assignment;

import com.conceptandcoding.LowLevelDesign.customer_support.model.Agent;
import com.conceptandcoding.LowLevelDesign.customer_support.model.Issue;

import java.util.List;

public interface AssignmentStrategy {
    public Agent assign(List<Agent> agent, Issue issue);
}
