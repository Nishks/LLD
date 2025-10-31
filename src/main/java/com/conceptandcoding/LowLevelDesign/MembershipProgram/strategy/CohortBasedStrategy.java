package com.conceptandcoding.LowLevelDesign.MembershipProgram.strategy;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;

import java.util.Map;
import java.util.Set;

public class CohortBasedStrategy implements TierEvaluationStrategy {
    private final Map<String, Set<String>> userToCohorts; // userId -> set of cohort names
    private final Map<MembershipTier, String> requiredCohortByTier; // target tier -> required cohort name

    public CohortBasedStrategy(Map<String, Set<String>> userToCohorts, Map<MembershipTier, String> requiredCohortByTier) {
        this.userToCohorts = userToCohorts;
        this.requiredCohortByTier = requiredCohortByTier;
    }

    @Override
    public boolean qualifies(String userId, MembershipTier targetTier) {
        String requiredCohort = requiredCohortByTier.get(targetTier);
        if (requiredCohort == null) return false;
        Set<String> cohorts = userToCohorts.get(userId);
        return cohorts != null && cohorts.contains(requiredCohort);
    }
}


