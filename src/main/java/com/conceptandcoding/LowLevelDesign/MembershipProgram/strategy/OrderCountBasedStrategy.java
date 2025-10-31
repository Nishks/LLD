package com.conceptandcoding.LowLevelDesign.MembershipProgram.strategy;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderCountBasedStrategy implements TierEvaluationStrategy {
    private final Map<MembershipTier, Integer> thresholdByTier = new ConcurrentHashMap<>();
    private final Map<String, Integer> userMonthlyOrderCount; // injected/mocked data source

    public OrderCountBasedStrategy(Map<String, Integer> userMonthlyOrderCount) {
        this.userMonthlyOrderCount = userMonthlyOrderCount;
        thresholdByTier.put(MembershipTier.SILVER, 2);
        thresholdByTier.put(MembershipTier.GOLD, 5);
        thresholdByTier.put(MembershipTier.PLATINUM, 10);
    }

    @Override
    public boolean qualifies(String userId, MembershipTier targetTier) {
        int orders = userMonthlyOrderCount.getOrDefault(userId, 0);
        int threshold = thresholdByTier.getOrDefault(targetTier, Integer.MAX_VALUE);
        return orders >= threshold;
    }
}


