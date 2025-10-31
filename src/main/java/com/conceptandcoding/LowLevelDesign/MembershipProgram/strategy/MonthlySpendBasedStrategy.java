package com.conceptandcoding.LowLevelDesign.MembershipProgram.strategy;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MonthlySpendBasedStrategy implements TierEvaluationStrategy {
    private final Map<MembershipTier, BigDecimal> thresholdByTier = new ConcurrentHashMap<>();
    private final Map<String, BigDecimal> userMonthlySpend; // injected/mocked data source

    public MonthlySpendBasedStrategy(Map<String, BigDecimal> userMonthlySpend) {
        this.userMonthlySpend = userMonthlySpend;
        thresholdByTier.put(MembershipTier.SILVER, new BigDecimal("1000"));
        thresholdByTier.put(MembershipTier.GOLD, new BigDecimal("5000"));
        thresholdByTier.put(MembershipTier.PLATINUM, new BigDecimal("10000"));
    }

    @Override
    public boolean qualifies(String userId, MembershipTier targetTier) {
        BigDecimal spend = userMonthlySpend.getOrDefault(userId, BigDecimal.ZERO);
        BigDecimal threshold = thresholdByTier.getOrDefault(targetTier, new BigDecimal("999999999"));
        return spend.compareTo(threshold) >= 0;
    }
}


