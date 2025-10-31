package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;

import java.util.EnumMap;
import java.util.Map;

public class TierConfigRepository {
    private final Map<MembershipTier, Integer> tierToExtraDiscount = new EnumMap<>(MembershipTier.class);

    public TierConfigRepository() {
        // Default extra discount by tier; plans add on top if needed
        tierToExtraDiscount.put(MembershipTier.SILVER, 2);
        tierToExtraDiscount.put(MembershipTier.GOLD, 5);
        tierToExtraDiscount.put(MembershipTier.PLATINUM, 10);
    }

    public int getExtraDiscountForTier(MembershipTier tier) {
        return tierToExtraDiscount.getOrDefault(tier, 0);
    }
}


