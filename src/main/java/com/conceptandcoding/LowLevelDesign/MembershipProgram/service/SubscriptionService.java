package com.conceptandcoding.LowLevelDesign.MembershipProgram.service;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.SubscriptionStatus;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.MembershipPlan;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.Subscription;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.SubscriptionRepository;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.strategy.TierEvaluationStrategy;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanService planService;
    private final TierEvaluationStrategy tierEvaluationStrategy;
    private final ConcurrentMap<String, Lock> userLocks = new ConcurrentHashMap<>();

    public SubscriptionService(SubscriptionRepository subscriptionRepository, PlanService planService, TierEvaluationStrategy tierEvaluationStrategy) {
        this.subscriptionRepository = subscriptionRepository;
        this.planService = planService;
        this.tierEvaluationStrategy = tierEvaluationStrategy;
    }

    private Lock lockForUser(String userId) {
        return userLocks.computeIfAbsent(userId, id -> new ReentrantLock());
    }

    public Subscription subscribe(String userId, PlanType planType, MembershipTier tier) {
        Lock lock = lockForUser(userId);
        lock.lock();
        try {
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusDays(planType.getDurationDays());

            Optional<Subscription> existing = subscriptionRepository.findByUserId(userId);
            Subscription subscription;
            if (existing.isPresent() && existing.get().getStatus() == SubscriptionStatus.ACTIVE) {
                subscription = existing.get();
                subscription.setPlanType(planType);
                subscription.setTier(tier);
                subscription.setStartDate(start);
                subscription.setEndDate(end);
                subscription.setStatus(SubscriptionStatus.ACTIVE);
            } else {
                subscription = new Subscription(UUID.randomUUID().toString(), userId, planType, tier, start, end, SubscriptionStatus.ACTIVE);
            }
            subscriptionRepository.save(subscription);
            return subscription;
        } finally {
            lock.unlock();
        }
    }

    public Optional<Subscription> getCurrent(String userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    /**
     * Upgrade user tier. If targetTier is null, automatically upgrades to the highest qualifying tier.
     * @param userId user id
     * @param targetTier target tier (null for auto-upgrade to highest qualifying tier)
     * @return updated subscription
     */
    public Optional<Subscription> upgradeTier(String userId, MembershipTier targetTier) {
        Lock lock = lockForUser(userId);
        lock.lock();
        try {
            Optional<Subscription> existing = subscriptionRepository.findByUserId(userId);
            if (existing.isEmpty()) return Optional.empty();

            Subscription sub = existing.get();
            if (sub.getStatus() != SubscriptionStatus.ACTIVE) return Optional.of(sub);

            MembershipTier tierToUpgrade;
            if (targetTier == null) {
                // Auto-upgrade: find highest qualifying tier
                tierToUpgrade = getHighestQualifyingTier(userId);
                if (tierToUpgrade == null) {
                    return Optional.of(sub); // No upgrade available
                }
            } else {
                // Manual upgrade: check if user qualifies for requested tier
                if (!tierEvaluationStrategy.qualifies(userId, targetTier)) {
                    return Optional.of(sub); // Doesn't qualify
                }
                tierToUpgrade = targetTier;
            }

            // Only upgrade if new tier is higher than current
            if (isTierHigher(tierToUpgrade, sub.getTier())) {
                sub.setTier(tierToUpgrade);
                subscriptionRepository.save(sub);
            }
            return Optional.of(sub);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Find the highest tier the user qualifies for
     */
    private MembershipTier getHighestQualifyingTier(String userId) {
        MembershipTier[] tiers = MembershipTier.values();
        // Check tiers from highest to lowest (reverse order)
        for (int i = tiers.length - 1; i >= 0; i--) {
            MembershipTier tier = tiers[i];
            if (tierEvaluationStrategy.qualifies(userId, tier)) {
                return tier; // First match in reverse order is the highest qualifying tier
            }
        }
        return null; // No tier qualifies
    }

    /**
     * Check if tier1 is higher than tier2
     */
    private boolean isTierHigher(MembershipTier tier1, MembershipTier tier2) {
        MembershipTier[] tiers = MembershipTier.values();
        int index1 = -1, index2 = -1;
        for (int i = 0; i < tiers.length; i++) {
            if (tiers[i] == tier1) index1 = i;
            if (tiers[i] == tier2) index2 = i;
        }
        return index1 > index2; // Higher index = higher tier (PLATINUM > GOLD > SILVER)
    }

    /*
    * Don't understand this use case why would we allow anyone to downgrade ?
    *
    *
    * */

    public Optional<Subscription> downgradeTier(String userId, MembershipTier targetTier) {
        Lock lock = lockForUser(userId);
        lock.lock();
        try {
            Optional<Subscription> existing = subscriptionRepository.findByUserId(userId);
            if (existing.isEmpty()) return Optional.empty();
            Subscription sub = existing.get();
            if (sub.getStatus() != SubscriptionStatus.ACTIVE) return Optional.of(sub);
            sub.setTier(targetTier);
            subscriptionRepository.save(sub);
            return Optional.of(sub);
        } finally {
            lock.unlock();
        }
    }

    public Optional<Subscription> cancel(String userId) {
        Lock lock = lockForUser(userId);
        lock.lock();
        try {
            Optional<Subscription> existing = subscriptionRepository.findByUserId(userId);
            if (existing.isEmpty()) return Optional.empty();
            Subscription sub = existing.get();
            sub.setStatus(SubscriptionStatus.CANCELED);
            subscriptionRepository.save(sub);
            return Optional.of(sub);
        } finally {
            lock.unlock();
        }
    }

    // Sweep task: expire all overdue ACTIVE subscriptions
    public int expireDueSubscriptions() {
        int updated = 0;
        LocalDate today = LocalDate.now();
        for (Subscription s : subscriptionRepository.findAll()) {
            if (s.getStatus() == SubscriptionStatus.ACTIVE && today.isAfter(s.getEndDate())) {
                Lock lock = lockForUser(s.getUserId());
                lock.lock();
                try {
                    // re-check inside lock
                    Optional<Subscription> current = subscriptionRepository.findByUserId(s.getUserId());
                    if (current.isPresent()) {
                        Subscription sub = current.get();
                        if (sub.getStatus() == SubscriptionStatus.ACTIVE && today.isAfter(sub.getEndDate())) {
                            sub.setStatus(SubscriptionStatus.EXPIRED);
                            subscriptionRepository.save(sub);
                            updated++;
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        return updated;
    }
}


