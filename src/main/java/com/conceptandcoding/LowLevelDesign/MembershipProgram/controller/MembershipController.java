package com.conceptandcoding.LowLevelDesign.MembershipProgram.controller;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.MembershipPlan;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.Subscription;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.service.PlanService;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.service.SubscriptionService;

import java.util.List;
import java.util.Optional;

public class MembershipController {
    private final PlanService planService;
    private final SubscriptionService subscriptionService;

    public MembershipController(PlanService planService, SubscriptionService subscriptionService) {
        this.planService = planService;
        this.subscriptionService = subscriptionService;
    }

    // GET /membership/plans
    public List<MembershipPlan> getPlans() {
        return planService.getAllPlans();
    }

    // POST /membership/subscribe
    public Subscription subscribe(String userId, PlanType planType, MembershipTier tier) {
        return subscriptionService.subscribe(userId, planType, tier);
    }

    // POST /membership/upgrade
    public Optional<Subscription> upgradeTier(String userId, MembershipTier targetTier) {
        return subscriptionService.upgradeTier(userId, targetTier);
    }

    // POST /membership/downgrade
    public Optional<Subscription> downgradeTier(String userId, MembershipTier targetTier) {
        return subscriptionService.downgradeTier(userId, targetTier);
    }

    // POST /membership/cancel
    public Optional<Subscription> cancel(String userId) {
        return subscriptionService.cancel(userId);
    }

    // GET /membership/current
    public Optional<Subscription> getCurrent(String userId) {
        return subscriptionService.getCurrent(userId);
    }
}


