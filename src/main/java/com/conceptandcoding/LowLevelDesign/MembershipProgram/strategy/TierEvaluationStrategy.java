package com.conceptandcoding.LowLevelDesign.MembershipProgram.strategy;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;

public interface TierEvaluationStrategy {
    boolean qualifies(String userId, MembershipTier targetTier);
}


