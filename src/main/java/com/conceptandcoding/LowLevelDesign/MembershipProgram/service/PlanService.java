package com.conceptandcoding.LowLevelDesign.MembershipProgram.service;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
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

    public MembershipPlan getPlan(MembershipType membershipType, PlanType planType, MembershipTier tier) {
        return planRepository.getByKey(membershipType, planType, tier);
    }

    // Backward compatibility: get all plans for a plan type
    public List<MembershipPlan> getPlansByType(PlanType planType) {
        return planRepository.getByPlanType(planType);
    }
}


