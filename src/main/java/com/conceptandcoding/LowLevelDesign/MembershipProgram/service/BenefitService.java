package com.conceptandcoding.LowLevelDesign.MembershipProgram.service;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.DiscountType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.*;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.TierConfigRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

public class BenefitService {
    private final TierConfigRepository tierConfigRepository;

    public BenefitService(TierConfigRepository tierConfigRepository) {
        this.tierConfigRepository = tierConfigRepository;
    }

    // Compute effective TierBenefits for a given plan
    // Plan already contains its specific TierBenefits, so we just enhance them with tier-level extra discount
    public TierBenefits effectiveBenefits(MembershipPlan plan) {
        TierBenefits base = plan.getTierBenefits();
        if (base == null) return null;

        int tierExtraPercent = tierConfigRepository.getExtraDiscountForTier(plan.getTier());
        if (tierExtraPercent <= 0) return base;

        DiscountRule tierExtraRule = new DiscountRule(
                DiscountType.PERCENT,
                new BigDecimal(String.valueOf(tierExtraPercent)),
                Collections.emptySet(),
                Collections.emptySet()
        );

        return new TierBenefits(
                base.getTier(),
                base.getFreeDeliveryRule(),
                mergeDiscounts(base, tierExtraRule),
                base.getCouponRules()
        );
    }

    // Overloaded method for backward compatibility (if needed)
    public TierBenefits effectiveBenefits(MembershipPlan plan, MembershipType membershipType, MembershipTier tier) {
        // Plan already has the correct benefits for its membership type and tier
        // Just validate they match and return effective benefits
        if (plan.getMembershipType() != membershipType || plan.getTier() != tier) {
            return null; // Requested combination doesn't match this plan
        }
        return effectiveBenefits(plan);
    }

    private java.util.List<DiscountRule> mergeDiscounts(TierBenefits base, DiscountRule extra) {
        java.util.List<DiscountRule> merged = new ArrayList<>(base.getDiscountRules());
        merged.add(extra);
        return merged;
    }
}


