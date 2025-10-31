package com.conceptandcoding.LowLevelDesign.MembershipProgram.service;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.MembershipPlan;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.PlanRepository;

import java.util.List;

public class PlanService {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public List<MembershipPlan> getAllPlans() {
        return planRepository.getAllPlans();
    }

    public MembershipPlan getPlan(PlanType type) {
        return planRepository.getByType(type);
    }
}


