package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.MembershipPlan;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.PlanKey;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.factory.PlanFactory;

import java.util.*;

/**
 * Repository for managing membership plans.
 * Uses Factory pattern to create plans cleanly, avoiding messy nested benefit configurations.
 */
public class PlanRepository {
    private final Map<PlanKey, MembershipPlan> planKeyToPlan = new HashMap<>();

    public PlanRepository() {
        seedDefaultPlans();
    }

    private void seedDefaultPlans() {
        // Use Factory pattern to create all plans cleanly
        PlanFactory planFactory = new PlanFactory();
        List<MembershipPlan> allPlans = planFactory.createAllPlans();
        
        for (MembershipPlan plan : allPlans) {
            PlanKey key = new PlanKey(plan.getMembershipType(), plan.getPlanType(), plan.getTier());
            planKeyToPlan.put(key, plan);
        }
    }

    public List<MembershipPlan> getAllPlans() {
        return Collections.unmodifiableList(new ArrayList<>(planKeyToPlan.values()));
    }

    public MembershipPlan getByKey(MembershipType membershipType, PlanType planType, MembershipTier tier) {
        PlanKey key = new PlanKey(membershipType, planType, tier);
        return planKeyToPlan.get(key);
    }

    // Backward compatibility: get all plans for a specific plan type
    public List<MembershipPlan> getByPlanType(PlanType planType) {
        List<MembershipPlan> plans = new ArrayList<>();
        for (Map.Entry<PlanKey, MembershipPlan> entry : planKeyToPlan.entrySet()) {
            if (entry.getKey().getPlanType() == planType) {
                plans.add(entry.getValue());
            }
        }
        return plans;
    }
}
