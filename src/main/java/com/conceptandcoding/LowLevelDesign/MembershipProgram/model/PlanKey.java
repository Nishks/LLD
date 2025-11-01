package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;

import java.util.Objects;

public class PlanKey {
    private final MembershipType membershipType;
    private final PlanType planType;
    private final MembershipTier tier;

    public PlanKey(MembershipType membershipType, PlanType planType, MembershipTier tier) {
        this.membershipType = membershipType;
        this.planType = planType;
        this.tier = tier;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public MembershipTier getTier() {
        return tier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanKey planKey = (PlanKey) o;
        return membershipType == planKey.membershipType &&
                planType == planKey.planType &&
                tier == planKey.tier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(membershipType, planType, tier);
    }

    @Override
    public String toString() {
        return membershipType + "_" + planType + "_" + tier;
    }
}

