package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.factory;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.DiscountType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.BenefitConfig;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.TierBenefits;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.builder.BenefitConfigBuilder;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.builder.TierBenefitsBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class BenefitConfigurationFactory {
    
    public BenefitConfig createForPlanType(PlanType planType) {
        BenefitConfigBuilder builder = new BenefitConfigBuilder();
        
        // Create benefits for all tiers and membership types
        for (MembershipTier tier : MembershipTier.values()) {
            TierBenefits standardBenefits = createStandardBenefits(tier, planType);
            TierBenefits loyalBenefits = createLoyalBenefits(tier, planType);
            TierBenefits vipBenefits = createVipBenefits(tier, planType);
            
            builder.addAllMembershipTypes(tier, standardBenefits, loyalBenefits, vipBenefits);
        }
        
        return builder.build();
    }
    
    private TierBenefits createStandardBenefits(MembershipTier tier, PlanType planType) {
        TierBenefitsBuilder builder = new TierBenefitsBuilder().withTier(tier);
        
        switch (tier) {
            case SILVER:
                builder.withFreeDeliveryThreshold(new BigDecimal("999"))
                       .addPercentDiscount(new BigDecimal("3"), Set.of("CAT_EVERYDAY"))
                       .addCoupon("STD_SILV5", DiscountType.FLAT, new BigDecimal("50")); // Special flat discount for monthly
                break;
            case GOLD:
                builder.withFreeDeliveryThreshold(new BigDecimal("699"))
                       .addPercentDiscount(new BigDecimal("5"), Set.of("CAT_EVERYDAY", "CAT_FRESH"))
                       .addCoupon(getCouponCode(planType, "STD_GOLD"), DiscountType.PERCENT, new BigDecimal("10"));
                break;
            case PLATINUM:
                BigDecimal platThreshold = planType == PlanType.QUARTERLY ? new BigDecimal("0") : new BigDecimal("499");
                builder.withFreeDeliveryThreshold(platThreshold)
                       .addPercentDiscount(getDiscountForTier(planType, 8), null)
                       .addCoupon(getCouponCode(planType, "STD_PLAT"), DiscountType.PERCENT, new BigDecimal("20"));
                break;
        }
        
        return builder.build();
    }
    
    private TierBenefits createLoyalBenefits(MembershipTier tier, PlanType planType) {
        TierBenefitsBuilder builder = new TierBenefitsBuilder().withTier(tier);
        
        switch (tier) {
            case SILVER:
                builder.withFreeDeliveryThreshold(new BigDecimal("799"))
                       .addPercentDiscount(new BigDecimal("5"), null)
                       .addCoupon(getCouponCode(planType, "LOYAL_SILV"), DiscountType.PERCENT, new BigDecimal("10"));
                break;
            case GOLD:
                builder.withFreeDeliveryThreshold(new BigDecimal("499"))
                       .addPercentDiscount(getDiscountForTier(planType, 8), null)
                       .addCoupon(getCouponCode(planType, "LOYAL_GOLD"), DiscountType.PERCENT, new BigDecimal("15"));
                break;
            case PLATINUM:
                builder.withFreeDeliveryAlways()
                       .addPercentDiscount(getDiscountForTier(planType, 12), null)
                       .addCoupon(getCouponCode(planType, "LOYAL_PLAT"), DiscountType.PERCENT, new BigDecimal("25"));
                break;
        }
        
        return builder.build();
    }
    
    private TierBenefits createVipBenefits(MembershipTier tier, PlanType planType) {
        TierBenefitsBuilder builder = new TierBenefitsBuilder().withTier(tier);
        
        switch (tier) {
            case SILVER:
                builder.withFreeDeliveryThreshold(new BigDecimal("599"))
                       .addPercentDiscount(new BigDecimal("8"), null)
                       .addCoupon(getCouponCode(planType, "VIP_SILV"), DiscountType.PERCENT, new BigDecimal("15"));
                break;
            case GOLD:
                builder.withFreeDeliveryThreshold(new BigDecimal("299"))
                       .addPercentDiscount(getDiscountForTier(planType, 12), null)
                       .addCoupon(getCouponCode(planType, "VIP_GOLD"), DiscountType.PERCENT, new BigDecimal("20"));
                break;
            case PLATINUM:
                builder.withFreeDeliveryAlways()
                       .addPercentDiscount(getDiscountForTier(planType, 15), null)
                       .addCoupon(getCouponCode(planType, "VIP_PLAT"), DiscountType.PERCENT, new BigDecimal("30"));
                break;
        }
        
        return builder.build();
    }
    
    private BigDecimal getDiscountForTier(PlanType planType, int baseDiscount) {
        // Quarterly and Yearly get slightly better discounts
        int adjustment = planType == PlanType.QUARTERLY ? 2 : (planType == PlanType.YEARLY ? 4 : 0);
        return new BigDecimal(String.valueOf(baseDiscount + adjustment));
    }
    
    private String getCouponCode(PlanType planType, String prefix) {
        // Generate coupon codes: STD_GOLD10, STD_QGOLD15, STD_YGOLD20, LOYAL_GOLD15, VIP_PLAT30, etc.
        String suffix;
        if (planType == PlanType.MONTHLY) {
            suffix = "10";
        } else if (planType == PlanType.QUARTERLY) {
            suffix = "Q15";
        } else { // YEARLY
            suffix = "Y20";
        }
        return prefix + suffix;
    }
}

