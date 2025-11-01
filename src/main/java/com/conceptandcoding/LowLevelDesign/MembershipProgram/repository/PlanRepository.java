package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.DiscountType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.*;

import java.math.BigDecimal;
import java.util.*;

public class PlanRepository {
    private final Map<PlanKey, MembershipPlan> planKeyToPlan = new HashMap<>();

    public PlanRepository() {
        seedDefaultPlans();
    }

    private void seedDefaultPlans() {
        // Monthly plan - benefits for STANDARD, LOYAL, VIP memberships with their tiers
        BenefitConfig monthlyBenefits = new BenefitConfig();
        
        // STANDARD membership tiers
        monthlyBenefits.addTierBenefits(MembershipType.STANDARD, new TierBenefits(
                MembershipTier.SILVER,
                new FreeDeliveryRule(new BigDecimal("999")),
                Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("3"), Collections.emptySet(), new HashSet<>(Arrays.asList("CAT_EVERYDAY")))),
                Arrays.asList(new CouponRule("STD_SILV5", new DiscountRule(DiscountType.FLAT, new BigDecimal("50"), Collections.emptySet(), Collections.emptySet())))
        )).addTierBenefits(MembershipType.STANDARD, new TierBenefits(
                MembershipTier.GOLD,
                new FreeDeliveryRule(new BigDecimal("699")),
                Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("5"), Collections.emptySet(), new HashSet<>(Arrays.asList("CAT_EVERYDAY", "CAT_FRESH")))),
                Arrays.asList(new CouponRule("STD_GOLD10", new DiscountRule(DiscountType.PERCENT, new BigDecimal("10"), Collections.emptySet(), Collections.emptySet())))
        )).addTierBenefits(MembershipType.STANDARD, new TierBenefits(
                MembershipTier.PLATINUM,
                new FreeDeliveryRule(new BigDecimal("499")),
                Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("8"), Collections.emptySet(), Collections.emptySet())),
                Arrays.asList(new CouponRule("STD_PLAT20", new DiscountRule(DiscountType.PERCENT, new BigDecimal("20"), Collections.emptySet(), Collections.emptySet())))
        ));
        
        // LOYAL membership tiers (better benefits)
        monthlyBenefits.addTierBenefits(MembershipType.LOYAL, new TierBenefits(
                MembershipTier.SILVER,
                new FreeDeliveryRule(new BigDecimal("799")),
                Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("5"), Collections.emptySet(), Collections.emptySet())),
                Arrays.asList(new CouponRule("LOYAL_SILV10", new DiscountRule(DiscountType.PERCENT, new BigDecimal("10"), Collections.emptySet(), Collections.emptySet())))
        )).addTierBenefits(MembershipType.LOYAL, new TierBenefits(
                MembershipTier.GOLD,
                new FreeDeliveryRule(new BigDecimal("499")),
                Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("8"), Collections.emptySet(), Collections.emptySet())),
                Arrays.asList(new CouponRule("LOYAL_GOLD15", new DiscountRule(DiscountType.PERCENT, new BigDecimal("15"), Collections.emptySet(), Collections.emptySet())))
        )).addTierBenefits(MembershipType.LOYAL, new TierBenefits(
                MembershipTier.PLATINUM,
                new FreeDeliveryRule(new BigDecimal("0")),
                Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("12"), Collections.emptySet(), Collections.emptySet())),
                Arrays.asList(new CouponRule("LOYAL_PLAT25", new DiscountRule(DiscountType.PERCENT, new BigDecimal("25"), Collections.emptySet(), Collections.emptySet())))
        ));
        
        // VIP membership tiers (best benefits)
        monthlyBenefits.addTierBenefits(MembershipType.VIP, new TierBenefits(
                MembershipTier.SILVER,
                new FreeDeliveryRule(new BigDecimal("599")),
                Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("8"), Collections.emptySet(), Collections.emptySet())),
                Arrays.asList(new CouponRule("VIP_SILV15", new DiscountRule(DiscountType.PERCENT, new BigDecimal("15"), Collections.emptySet(), Collections.emptySet())))
        )).addTierBenefits(MembershipType.VIP, new TierBenefits(
                MembershipTier.GOLD,
                new FreeDeliveryRule(new BigDecimal("299")),
                Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("12"), Collections.emptySet(), Collections.emptySet())),
                Arrays.asList(new CouponRule("VIP_GOLD20", new DiscountRule(DiscountType.PERCENT, new BigDecimal("20"), Collections.emptySet(), Collections.emptySet())))
        )).addTierBenefits(MembershipType.VIP, new TierBenefits(
                MembershipTier.PLATINUM,
                new FreeDeliveryRule(new BigDecimal("0")),
                Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("15"), Collections.emptySet(), Collections.emptySet())),
                Arrays.asList(new CouponRule("VIP_PLAT30", new DiscountRule(DiscountType.PERCENT, new BigDecimal("30"), Collections.emptySet(), Collections.emptySet())))
        ));

        // Create plans for each membership type and tier combination for MONTHLY
        for (MembershipType memType : MembershipType.values()) {
            for (MembershipTier tier : MembershipTier.values()) {
                PlanKey key = new PlanKey(memType, PlanType.MONTHLY, tier);
                String planId = "plan_" + memType.name().toLowerCase() + "_monthly_" + tier.name().toLowerCase();
                BigDecimal price = calculatePrice(PlanType.MONTHLY, memType, tier);
                planKeyToPlan.put(key, new MembershipPlan(planId, memType, PlanType.MONTHLY, tier, price, monthlyBenefits));
            }
        }

        // Quarterly plan - similar structure with better benefits
        BenefitConfig quarterlyBenefits = new BenefitConfig();
        // STANDARD
        quarterlyBenefits.addTierBenefits(MembershipType.STANDARD, new TierBenefits(MembershipTier.SILVER, new FreeDeliveryRule(new BigDecimal("799")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("4"), Collections.emptySet(), new HashSet<>(Arrays.asList("CAT_EVERYDAY")))), Collections.emptyList()))
                .addTierBenefits(MembershipType.STANDARD, new TierBenefits(MembershipTier.GOLD, new FreeDeliveryRule(new BigDecimal("499")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("7"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("STD_QGOLD15", new DiscountRule(DiscountType.PERCENT, new BigDecimal("15"), Collections.emptySet(), Collections.emptySet())))))
                .addTierBenefits(MembershipType.STANDARD, new TierBenefits(MembershipTier.PLATINUM, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("10"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("STD_QPLAT25", new DiscountRule(DiscountType.PERCENT, new BigDecimal("25"), Collections.emptySet(), Collections.emptySet())))));
        // LOYAL
        quarterlyBenefits.addTierBenefits(MembershipType.LOYAL, new TierBenefits(MembershipTier.SILVER, new FreeDeliveryRule(new BigDecimal("599")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("6"), Collections.emptySet(), Collections.emptySet())), Collections.emptyList()))
                .addTierBenefits(MembershipType.LOYAL, new TierBenefits(MembershipTier.GOLD, new FreeDeliveryRule(new BigDecimal("299")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("10"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("LOYAL_QGOLD20", new DiscountRule(DiscountType.PERCENT, new BigDecimal("20"), Collections.emptySet(), Collections.emptySet())))))
                .addTierBenefits(MembershipType.LOYAL, new TierBenefits(MembershipTier.PLATINUM, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("14"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("LOYAL_QPLAT30", new DiscountRule(DiscountType.PERCENT, new BigDecimal("30"), Collections.emptySet(), Collections.emptySet())))));
        // VIP
        quarterlyBenefits.addTierBenefits(MembershipType.VIP, new TierBenefits(MembershipTier.SILVER, new FreeDeliveryRule(new BigDecimal("399")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("10"), Collections.emptySet(), Collections.emptySet())), Collections.emptyList()))
                .addTierBenefits(MembershipType.VIP, new TierBenefits(MembershipTier.GOLD, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("15"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("VIP_QGOLD25", new DiscountRule(DiscountType.PERCENT, new BigDecimal("25"), Collections.emptySet(), Collections.emptySet())))))
                .addTierBenefits(MembershipType.VIP, new TierBenefits(MembershipTier.PLATINUM, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("18"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("VIP_QPLAT35", new DiscountRule(DiscountType.PERCENT, new BigDecimal("35"), Collections.emptySet(), Collections.emptySet())))));

        // Create plans for each membership type and tier combination for QUARTERLY
        for (MembershipType memType : MembershipType.values()) {
            for (MembershipTier tier : MembershipTier.values()) {
                PlanKey key = new PlanKey(memType, PlanType.QUARTERLY, tier);
                String planId = "plan_" + memType.name().toLowerCase() + "_quarterly_" + tier.name().toLowerCase();
                BigDecimal price = calculatePrice(PlanType.QUARTERLY, memType, tier);
                planKeyToPlan.put(key, new MembershipPlan(planId, memType, PlanType.QUARTERLY, tier, price, quarterlyBenefits));
            }
        }

        // Yearly plan - best benefits
        BenefitConfig yearlyBenefits = new BenefitConfig();
        // STANDARD
        yearlyBenefits.addTierBenefits(MembershipType.STANDARD, new TierBenefits(MembershipTier.SILVER, new FreeDeliveryRule(new BigDecimal("599")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("5"), Collections.emptySet(), Collections.emptySet())), Collections.emptyList()))
                .addTierBenefits(MembershipType.STANDARD, new TierBenefits(MembershipTier.GOLD, new FreeDeliveryRule(new BigDecimal("299")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("9"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("STD_YGOLD20", new DiscountRule(DiscountType.PERCENT, new BigDecimal("20"), Collections.emptySet(), Collections.emptySet())))))
                .addTierBenefits(MembershipType.STANDARD, new TierBenefits(MembershipTier.PLATINUM, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("12"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("STD_YPLAT30", new DiscountRule(DiscountType.PERCENT, new BigDecimal("30"), Collections.emptySet(), Collections.emptySet())))));
        // LOYAL
        yearlyBenefits.addTierBenefits(MembershipType.LOYAL, new TierBenefits(MembershipTier.SILVER, new FreeDeliveryRule(new BigDecimal("399")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("8"), Collections.emptySet(), Collections.emptySet())), Collections.emptyList()))
                .addTierBenefits(MembershipType.LOYAL, new TierBenefits(MembershipTier.GOLD, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("12"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("LOYAL_YGOLD25", new DiscountRule(DiscountType.PERCENT, new BigDecimal("25"), Collections.emptySet(), Collections.emptySet())))))
                .addTierBenefits(MembershipType.LOYAL, new TierBenefits(MembershipTier.PLATINUM, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("16"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("LOYAL_YPLAT35", new DiscountRule(DiscountType.PERCENT, new BigDecimal("35"), Collections.emptySet(), Collections.emptySet())))));
        // VIP
        yearlyBenefits.addTierBenefits(MembershipType.VIP, new TierBenefits(MembershipTier.SILVER, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("12"), Collections.emptySet(), Collections.emptySet())), Collections.emptyList()))
                .addTierBenefits(MembershipType.VIP, new TierBenefits(MembershipTier.GOLD, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("18"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("VIP_YGOLD30", new DiscountRule(DiscountType.PERCENT, new BigDecimal("30"), Collections.emptySet(), Collections.emptySet())))))
                .addTierBenefits(MembershipType.VIP, new TierBenefits(MembershipTier.PLATINUM, new FreeDeliveryRule(new BigDecimal("0")), Arrays.asList(new DiscountRule(DiscountType.PERCENT, new BigDecimal("22"), Collections.emptySet(), Collections.emptySet())), Arrays.asList(new CouponRule("VIP_YPLAT40", new DiscountRule(DiscountType.PERCENT, new BigDecimal("40"), Collections.emptySet(), Collections.emptySet())))));

        // Create plans for each membership type and tier combination for YEARLY
        for (MembershipType memType : MembershipType.values()) {
            for (MembershipTier tier : MembershipTier.values()) {
                PlanKey key = new PlanKey(memType, PlanType.YEARLY, tier);
                String planId = "plan_" + memType.name().toLowerCase() + "_yearly_" + tier.name().toLowerCase();
                BigDecimal price = calculatePrice(PlanType.YEARLY, memType, tier);
                planKeyToPlan.put(key, new MembershipPlan(planId, memType, PlanType.YEARLY, tier, price, yearlyBenefits));
            }
        }
    }

    private BigDecimal calculatePrice(PlanType planType, MembershipType membershipType, MembershipTier tier) {
        // Base prices by plan type
        BigDecimal basePrice;
        switch (planType) {
            case MONTHLY:
                basePrice = new BigDecimal("199.00");
                break;
            case QUARTERLY:
                basePrice = new BigDecimal("499.00");
                break;
            case YEARLY:
                basePrice = new BigDecimal("1499.00");
                break;
            default:
                basePrice = BigDecimal.ZERO;
        }

        // Premium multipliers by membership type
        BigDecimal multiplier;
        switch (membershipType) {
            case STANDARD:
                multiplier = new BigDecimal("1.0");
                break;
            case LOYAL:
                multiplier = new BigDecimal("1.2"); // 20% premium
                break;
            case VIP:
                multiplier = new BigDecimal("1.5"); // 50% premium
                break;
            default:
                multiplier = new BigDecimal("1.0");
        }

        // Tier multipliers
        BigDecimal tierMultiplier;
        switch (tier) {
            case SILVER:
                tierMultiplier = new BigDecimal("1.0");
                break;
            case GOLD:
                tierMultiplier = new BigDecimal("1.1"); // 10% premium
                break;
            case PLATINUM:
                tierMultiplier = new BigDecimal("1.25"); // 25% premium
                break;
            default:
                tierMultiplier = new BigDecimal("1.0");
        }

        return basePrice.multiply(multiplier).multiply(tierMultiplier);
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


