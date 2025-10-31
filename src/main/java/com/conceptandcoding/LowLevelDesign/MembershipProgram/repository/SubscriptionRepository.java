package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.Subscription;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SubscriptionRepository {
    private final ConcurrentMap<String, Subscription> userIdToSubscription = new ConcurrentHashMap<>();

    public Optional<Subscription> findByUserId(String userId) {
        return Optional.ofNullable(userIdToSubscription.get(userId));
    }

    public void save(Subscription subscription) {
        userIdToSubscription.put(subscription.getUserId(), subscription);
    }
}


