package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.factory;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;

import java.math.BigDecimal;

public class PriceCalculator {
    private static final BigDecimal MONTHLY_BASE = new BigDecimal("199.00");
    private static final BigDecimal QUARTERLY_BASE = new BigDecimal("499.00");
    private static final BigDecimal YEARLY_BASE = new BigDecimal("1499.00");
    
    private static final BigDecimal STANDARD_MULTIPLIER = new BigDecimal("1.0");
    private static final BigDecimal LOYAL_MULTIPLIER = new BigDecimal("1.2");
    private static final BigDecimal VIP_MULTIPLIER = new BigDecimal("1.5");
    
    private static final BigDecimal SILVER_MULTIPLIER = new BigDecimal("1.0");
    private static final BigDecimal GOLD_MULTIPLIER = new BigDecimal("1.1");
    private static final BigDecimal PLATINUM_MULTIPLIER = new BigDecimal("1.25");

    public BigDecimal calculate(PlanType planType, MembershipType membershipType, MembershipTier tier) {
        BigDecimal basePrice = getBasePrice(planType);
        BigDecimal membershipMultiplier = getMembershipMultiplier(membershipType);
        BigDecimal tierMultiplier = getTierMultiplier(tier);
        
        return basePrice.multiply(membershipMultiplier).multiply(tierMultiplier);
    }

    private BigDecimal getBasePrice(PlanType planType) {
        switch (planType) {
            case MONTHLY: return MONTHLY_BASE;
            case QUARTERLY: return QUARTERLY_BASE;
            case YEARLY: return YEARLY_BASE;
            default: return BigDecimal.ZERO;
        }
    }

    private BigDecimal getMembershipMultiplier(MembershipType membershipType) {
        switch (membershipType) {
            case STANDARD: return STANDARD_MULTIPLIER;
            case LOYAL: return LOYAL_MULTIPLIER;
            case VIP: return VIP_MULTIPLIER;
            default: return STANDARD_MULTIPLIER;
        }
    }

    private BigDecimal getTierMultiplier(MembershipTier tier) {
        switch (tier) {
            case SILVER: return SILVER_MULTIPLIER;
            case GOLD: return GOLD_MULTIPLIER;
            case PLATINUM: return PLATINUM_MULTIPLIER;
            default: return SILVER_MULTIPLIER;
        }
    }
}

