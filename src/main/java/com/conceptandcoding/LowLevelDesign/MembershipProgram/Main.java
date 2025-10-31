package com.conceptandcoding.LowLevelDesign.MembershipProgram;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.controller.MembershipController;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.MembershipPlan;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.Subscription;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.TierBenefits;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.PlanRepository;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.SubscriptionRepository;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.TierConfigRepository;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.service.BenefitService;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.service.PlanService;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.service.SubscriptionExpiryScheduler;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.service.SubscriptionService;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.strategy.*;

import java.math.BigDecimal;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Mock data sources for strategies
        Map<String, Integer> userMonthlyOrderCount = new HashMap<>();
        Map<String, BigDecimal> userMonthlySpend = new HashMap<>();
        Map<String, Set<String>> userToCohorts = new HashMap<>();
        Map<MembershipTier, String> requiredCohortByTier = new EnumMap<>(MembershipTier.class);

        // Example thresholds: cohort "VIP" grants Platinum, "LOYAL" grants Gold
        requiredCohortByTier.put(MembershipTier.GOLD, "LOYAL");
        requiredCohortByTier.put(MembershipTier.PLATINUM, "VIP");

        // Seed some users
        String userA = "userA";
        userMonthlyOrderCount.put(userA, 6);
        userMonthlySpend.put(userA, new BigDecimal("6200"));
        userToCohorts.put(userA, new HashSet<>(Arrays.asList("LOYAL")));

        String userB = "userB";
        userMonthlyOrderCount.put(userB, 12);
        userMonthlySpend.put(userB, new BigDecimal("12000"));
        userToCohorts.put(userB, new HashSet<>(Arrays.asList("VIP", "LOYAL")));

        // Wire repositories
        PlanRepository planRepository = new PlanRepository();
        SubscriptionRepository subscriptionRepository = new SubscriptionRepository();
        TierConfigRepository tierConfigRepository = new TierConfigRepository();

        // Wire services
        PlanService planService = new PlanService(planRepository);
        BenefitService benefitService = new BenefitService(tierConfigRepository);

        // Configure strategies
        TierEvaluationStrategy compositeStrategy = new CompositeTierEvaluationStrategy()
                .add(new OrderCountBasedStrategy(userMonthlyOrderCount))
                .add(new MonthlySpendBasedStrategy(userMonthlySpend))
                .add(new CohortBasedStrategy(userToCohorts, requiredCohortByTier));

        SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository, planService, compositeStrategy);
        SubscriptionExpiryScheduler expiryScheduler = new SubscriptionExpiryScheduler(subscriptionService);
        expiryScheduler.start(1, 60);

        // Controller (simulated API layer)
        MembershipController controller = new MembershipController(planService, subscriptionService);

        // Demo: list plans
        System.out.println("Available Plans:");
        for (MembershipPlan plan : controller.getPlans()) {
            System.out.println("- " + plan.getPlanType() + " | Price: " + plan.getPrice());
        }

        // Demo: subscribe userA to MONTHLY SILVER
        Subscription subA = controller.subscribe(userA, PlanType.MONTHLY, MembershipTier.SILVER);
        System.out.println("Subscribed userA: " + subA.getPlanType() + " - " + subA.getTier());

        // Demo: userA upgrade to GOLD (should qualify via orders/spend/cohort)
        controller.upgradeTier(userA, MembershipTier.GOLD).ifPresent(s ->
                System.out.println("userA tier after upgrade attempt: " + s.getTier())
        );

        // Demo: subscribe userB to YEARLY GOLD
        Subscription subB = controller.subscribe(userB, PlanType.YEARLY, MembershipTier.GOLD);
        System.out.println("Subscribed userB: " + subB.getPlanType() + " - " + subB.getTier());

        // Demo: userB upgrade to PLATINUM (VIP cohort qualifies)
        controller.upgradeTier(userB, MembershipTier.PLATINUM).ifPresent(s ->
                System.out.println("userB tier after upgrade attempt: " + s.getTier())
        );

        // Effective benefits examples
        TierBenefits tb = benefitService.effectiveBenefits(planService.getPlan(PlanType.YEARLY), MembershipTier.PLATINUM);
        System.out.println("Effective Benefits userB PLATINUM on YEARLY: discounts rules count = " + tb.getDiscountRules().size() +
                ", min order for free delivery = " + tb.getFreeDeliveryRule().getMinOrderAmount());

        // Cancel userA
        controller.cancel(userA).ifPresent(s -> System.out.println("userA status after cancel: " + s.getStatus()));

        // Note: expiryScheduler is left running for demo; call stop() on shutdown in real app
    }
}


