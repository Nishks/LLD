package com.conceptandcoding.LowLevelDesign.MembershipProgram;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
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
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        // Mock data sources for strategies
        Map<String, Integer> userMonthlyOrderCount = new HashMap<>();
        Map<String, BigDecimal> userMonthlySpend = new HashMap<>();



//         Do not have clarity what the COHORT is and how it is different from membership
//          Map<String, Set<String>> userToCohorts = new HashMap<>();
//        Map<MembershipTier, String> requiredCohortByTier = new EnumMap<>(MembershipTier.class);

        // Seed some users
        String userA = "userA";
        userMonthlyOrderCount.put(userA, 6);
        userMonthlySpend.put(userA, new BigDecimal("6200"));
//        userToCohorts.put(userA, new HashSet<>(Arrays.asList("LOYAL")));

        String userB = "userB";
        userMonthlyOrderCount.put(userB, 12);
        userMonthlySpend.put(userB, new BigDecimal("12000"));
//        userToCohorts.put(userB, new HashSet<>(Arrays.asList("VIP", "LOYAL")));

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
                .add(new MonthlySpendBasedStrategy(userMonthlySpend));
//                .add(new CohortBasedStrategy(userToCohorts, requiredCohortByTier));

        SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository, planService, compositeStrategy);
        SubscriptionExpiryScheduler expiryScheduler = new SubscriptionExpiryScheduler(subscriptionService);
        expiryScheduler.start(1, 60);

        // Controller (simulated API layer)
        MembershipController controller = new MembershipController(planService, subscriptionService);

        // Show available plans in organized format
        System.out.println("\n========== AVAILABLE MEMBERSHIP PLANS ==========\n");
        List<MembershipPlan> allPlans = controller.getPlans();
        
        // Group plans by membership type, then tier, then plan type
        Map<MembershipType, Map<MembershipTier, Map<PlanType, MembershipPlan>>> organizedPlans = new LinkedHashMap<>();
        
        for (MembershipPlan plan : allPlans) {
            organizedPlans
                .computeIfAbsent(plan.getMembershipType(), k -> new LinkedHashMap<>())
                .computeIfAbsent(plan.getTier(), k -> new LinkedHashMap<>())
                .put(plan.getPlanType(), plan);
        }
        
        // Display in order: VIP -> LOYAL -> STANDARD
        MembershipType[] membershipTypeOrder = {MembershipType.VIP, MembershipType.LOYAL, MembershipType.STANDARD};
        MembershipTier[] tierOrder = {MembershipTier.SILVER, MembershipTier.GOLD, MembershipTier.PLATINUM};
        PlanType[] planTypeOrder = {PlanType.MONTHLY, PlanType.QUARTERLY, PlanType.YEARLY};
        
        for (MembershipType memType : membershipTypeOrder) {
            if (!organizedPlans.containsKey(memType)) continue;
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("  " + memType + " MEMBERSHIP");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            Map<MembershipTier, Map<PlanType, MembershipPlan>> tiers = organizedPlans.get(memType);
            
            for (MembershipTier tier : tierOrder) {
                if (!tiers.containsKey(tier)) continue;
                
                System.out.println("  ┌─ " + tier + " Tier");
                
                Map<PlanType, MembershipPlan> plans = tiers.get(tier);
                
                for (PlanType planType : planTypeOrder) {
                    if (!plans.containsKey(planType)) continue;
                    
                    MembershipPlan plan = plans.get(planType);
                    System.out.println("  │   • " + String.format("%-10s", planType) + " → ₹" + plan.getPrice());
                }
                
                System.out.println();
            }
            System.out.println();
        }
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Total Plans Available: " + allPlans.size());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Add third userC
        String userC = "userC";
        userMonthlyOrderCount.put(userC, 1);
        userMonthlySpend.put(userC, new BigDecimal("500"));
//        userToCohorts.put(userC, new HashSet<>());

        // Simulate concurrent flows for A, B, C
        ExecutorService exec = Executors.newFixedThreadPool(3);

        // UserA: Subscribe to LOYAL membership with SILVER tier, then auto-upgrade within LOYAL
        Runnable flowA = () -> simulateUserFlow(
                controller,
                benefitService,
                planService,
                userA,
                PlanType.MONTHLY,
                MembershipType.LOYAL,
                MembershipTier.SILVER,
                new BigDecimal("1200"),
                userMonthlyOrderCount,
                userMonthlySpend
        );

        // UserB: Subscribe to VIP membership with GOLD tier, then auto-upgrade within VIP
        Runnable flowB = () -> simulateUserFlow(
                controller,
                benefitService,
                planService,
                userB,
                PlanType.YEARLY,
                MembershipType.VIP,
                MembershipTier.GOLD,
                new BigDecimal("450"),
                userMonthlyOrderCount,
                userMonthlySpend
        );

        // UserC: Subscribe to STANDARD membership with SILVER tier, then auto-upgrade within STANDARD
        Runnable flowC = () -> simulateUserFlow(
                controller,
                benefitService,
                planService,
                userC,
                PlanType.QUARTERLY,
                MembershipType.STANDARD,
                MembershipTier.SILVER,
                new BigDecimal("800"),
                userMonthlyOrderCount,
                userMonthlySpend
        );

        List<Future<?>> futures = new ArrayList<>();
        futures.add(exec.submit(flowA));
        futures.add(exec.submit(flowB));
        futures.add(exec.submit(flowC));

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (Exception ignored) {
            }
        }

        exec.shutdown();

        // Cleanup example: cancel userA
        controller.cancel(userA).ifPresent(s -> System.out.println("[" + userA + "] status after cancel: " + s.getStatus()));

        // Note: expiryScheduler is left running for demo; call stop() on shutdown in real app
    }

    private static void simulateUserFlow(
            MembershipController controller,
            BenefitService benefitService,
            PlanService planService,
            String userId,
            PlanType planType,
            MembershipType membershipType,
            MembershipTier tier,
            BigDecimal cartTotal,
            Map<String, Integer> userMonthlyOrderCount,
            Map<String, BigDecimal> userMonthlySpend
    ) {
        // Subscribe with membership type and tier
        Subscription subscription = controller.subscribe(userId, planType, membershipType, tier);
        System.out.println("[" + userId + "] subscribed: " + subscription.getPlanType() + " | " + subscription.getMembershipType() + " | " + subscription.getTier());

        // Effective benefits for current membership type and tier
        MembershipPlan plan = planService.getPlan(subscription.getMembershipType(), subscription.getPlanType(), subscription.getTier());
        TierBenefits benefits = benefitService.effectiveBenefits(plan); // Simplified - plan already knows its benefits
        if (benefits != null) {
            BigDecimal minFreeDelivery = benefits.getFreeDeliveryRule() != null ? benefits.getFreeDeliveryRule().getMinOrderAmount() : BigDecimal.valueOf(Long.MAX_VALUE);
            boolean freeDelivery = cartTotal.compareTo(minFreeDelivery) >= 0;

            BigDecimal percentTotal = BigDecimal.ZERO;
            BigDecimal flatTotal = BigDecimal.ZERO;
            for (var rule : benefits.getDiscountRules()) {
                switch (rule.getType()) {
                    case PERCENT:
                        percentTotal = percentTotal.add(rule.getValue());
                        break;
                    case FLAT:
                        flatTotal = flatTotal.add(rule.getValue());
                        break;
                }
            }

            BigDecimal percentDiscount = cartTotal.multiply(percentTotal).divide(new BigDecimal("100"));
            BigDecimal totalDiscount = percentDiscount.add(flatTotal);
            if (totalDiscount.compareTo(cartTotal) > 0) totalDiscount = cartTotal;
            BigDecimal payable = cartTotal.subtract(totalDiscount);

            // Update user stats after checkout (simulating order completion)
            userMonthlyOrderCount.put(userId, userMonthlyOrderCount.getOrDefault(userId, 0) + 1);
            userMonthlySpend.put(userId, userMonthlySpend.getOrDefault(userId, BigDecimal.ZERO).add(payable));

            System.out.println("[" + userId + "] cart=" + cartTotal + ", freeDelivery=" + freeDelivery + ", discount=" + totalDiscount + ", payable=" + payable);
        }

        // Upgrade logic - upgrades only within same membership type
        MembershipTier tierBeforeUpgrade = subscription.getTier(); // Capture BEFORE upgrade
        MembershipType membershipTypeBefore = subscription.getMembershipType();
        // Auto-upgrade: system automatically finds highest qualifying tier within same membership type
        controller.autoUpgradeToHighest(userId).ifPresent(s -> {
            if (!s.getTier().equals(tierBeforeUpgrade)) {
                System.out.println("[" + userId + "] AUTO-UPGRADED within " + membershipTypeBefore + " membership: " + tierBeforeUpgrade + " -> " + s.getTier());
            } else {
                System.out.println("[" + userId + "] No auto-upgrade available, remains at " + membershipTypeBefore + " " + s.getTier());
            }
        });
    }
}


