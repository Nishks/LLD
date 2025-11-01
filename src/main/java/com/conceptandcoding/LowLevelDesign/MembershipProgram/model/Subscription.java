package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.SubscriptionStatus;

import java.time.LocalDate;
import java.util.Objects;

public class Subscription {
    private final String id;
    private final String userId;
    private PlanType planType;
    private MembershipType membershipType;
    private MembershipTier tier;
    private LocalDate startDate;
    private LocalDate endDate;
    private SubscriptionStatus status;

    public Subscription(String id, String userId, PlanType planType, MembershipType membershipType, MembershipTier tier, LocalDate startDate, LocalDate endDate, SubscriptionStatus status) {
        this.id = id;
        this.userId = userId;
        this.planType = planType;
        this.membershipType = membershipType;
        this.tier = tier;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public MembershipTier getTier() {
        return tier;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }

    public void setTier(MembershipTier tier) {
        this.tier = tier;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


