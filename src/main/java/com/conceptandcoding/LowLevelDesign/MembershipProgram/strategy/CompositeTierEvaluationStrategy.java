package com.conceptandcoding.LowLevelDesign.MembershipProgram.strategy;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;

import java.util.ArrayList;
import java.util.List;

public class CompositeTierEvaluationStrategy implements TierEvaluationStrategy {
    private final List<TierEvaluationStrategy> strategies = new ArrayList<>();

    public CompositeTierEvaluationStrategy add(TierEvaluationStrategy strategy) {
        strategies.add(strategy);
        return this;
    }

    @Override
    public boolean qualifies(String userId, MembershipTier targetTier) {
        for (TierEvaluationStrategy strategy : strategies) {
            if (strategy.qualifies(userId, targetTier)) {
                return true; // OR logic by default
            }
        }
        return false;
    }
}


