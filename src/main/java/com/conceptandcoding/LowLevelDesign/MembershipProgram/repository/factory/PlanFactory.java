package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.factory;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.BenefitConfig;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.MembershipPlan;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.TierBenefits;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlanFactory {
    private final BenefitConfigurationFactory benefitFactory;
    private final PriceCalculator priceCalculator;

    public PlanFactory() {
        this.benefitFactory = new BenefitConfigurationFactory();
        this.priceCalculator = new PriceCalculator();
    }

    public List<MembershipPlan> createAllPlans() {
        List<MembershipPlan> plans = new ArrayList<>();
        
        for (PlanType planType : PlanType.values()) {
            BenefitConfig benefitConfig = benefitFactory.createForPlanType(planType);
            
            for (MembershipType membershipType : MembershipType.values()) {
                for (MembershipTier tier : MembershipTier.values()) {
                    // Extract only the specific TierBenefits for this plan
                    TierBenefits tierBenefits = benefitConfig.getBenefitsForMembershipAndTier(membershipType, tier);
                    
                    String planId = generatePlanId(membershipType, planType, tier);
                    BigDecimal price = priceCalculator.calculate(planType, membershipType, tier);
                    
                    plans.add(new MembershipPlan(
                            planId,
                            membershipType,
                            planType,
                            tier,
                            price,
                            tierBenefits // Store only the specific benefits
                    ));
                }
            }
        }
        
        return plans;
    }

    private String generatePlanId(MembershipType membershipType, PlanType planType, MembershipTier tier) {
        return String.format("plan_%s_%s_%s",
                membershipType.name().toLowerCase(),
                planType.name().toLowerCase(),
                tier.name().toLowerCase());
    }
}

