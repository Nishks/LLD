package com.conceptandcoding.LowLevelDesign.MembershipProgram.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SubscriptionExpiryScheduler {
    private final SubscriptionService subscriptionService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public SubscriptionExpiryScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public void start(long initialDelaySeconds, long intervalSeconds) {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                subscriptionService.expireDueSubscriptions();
            } catch (Exception ignored) {
            }
        }, initialDelaySeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}


