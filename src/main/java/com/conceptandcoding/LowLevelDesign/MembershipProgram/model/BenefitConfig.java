package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;

import java.util.EnumMap;
import java.util.Map;

public class BenefitConfig {
    private final Map<MembershipTier, TierBenefits> benefitsByTier = new EnumMap<>(MembershipTier.class);

    public BenefitConfig addTierBenefits(TierBenefits tierBenefits) {
        benefitsByTier.put(tierBenefits.getTier(), tierBenefits);
        return this;
    }

    public TierBenefits getBenefitsForTier(MembershipTier tier) {
        return benefitsByTier.get(tier);
    }
}
