package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;

import java.util.HashMap;
import java.util.Map;

public class BenefitConfig {
    // Key: "MembershipType_Tier" e.g., "LOYAL_GOLD", "VIP_PLATINUM"
    private final Map<String, TierBenefits> benefitsByMembershipAndTier = new HashMap<>();

    public BenefitConfig addTierBenefits(MembershipType membershipType, TierBenefits tierBenefits) {
        String key = membershipType.name() + "_" + tierBenefits.getTier().name();
        benefitsByMembershipAndTier.put(key, tierBenefits);
        return this;
    }

    public TierBenefits getBenefitsForMembershipAndTier(MembershipType membershipType, MembershipTier tier) {
        String key = membershipType.name() + "_" + tier.name();
        return benefitsByMembershipAndTier.get(key);
    }

    // Backward compatibility for tier-only lookups (for existing code)
    public TierBenefits getBenefitsForTier(MembershipTier tier) {
        // Return first match found (could be improved)
        for (TierBenefits benefits : benefitsByMembershipAndTier.values()) {
            if (benefits.getTier() == tier) {
                return benefits;
            }
        }
        return null;
    }
}
