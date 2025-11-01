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

    // Compute effective TierBenefits for a given plan + membership type + tier
    public TierBenefits effectiveBenefits(MembershipPlan plan, MembershipType membershipType, MembershipTier tier) {
        TierBenefits base = plan.getBenefitConfig().getBenefitsForMembershipAndTier(membershipType, tier);
        if (base == null) return null;

        int tierExtraPercent = tierConfigRepository.getExtraDiscountForTier(tier);
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

    // Backward compatibility: tier-only lookup
    public TierBenefits effectiveBenefits(MembershipPlan plan, MembershipTier tier) {
        // Try STANDARD membership first for backward compatibility
        TierBenefits result = effectiveBenefits(plan, MembershipType.STANDARD, tier);
        if (result != null) return result;
        // Fallback to old method
        TierBenefits base = plan.getBenefitConfig().getBenefitsForTier(tier);
        if (base == null) return null;
        int tierExtraPercent = tierConfigRepository.getExtraDiscountForTier(tier);
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

    private java.util.List<DiscountRule> mergeDiscounts(TierBenefits base, DiscountRule extra) {
        java.util.List<DiscountRule> merged = new ArrayList<>(base.getDiscountRules());
        merged.add(extra);
        return merged;
    }
}


