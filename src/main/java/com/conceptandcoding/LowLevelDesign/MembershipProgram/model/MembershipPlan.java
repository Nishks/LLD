package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;

import java.math.BigDecimal;

public class MembershipPlan {
    private final String id;
    private final MembershipType membershipType;
    private final PlanType planType;
    private final MembershipTier tier;
    private final BigDecimal price;
    private final TierBenefits tierBenefits; // Only store the specific benefits for this plan

    public MembershipPlan(String id, MembershipType membershipType, PlanType planType, MembershipTier tier, BigDecimal price, TierBenefits tierBenefits) {
        this.id = id;
        this.membershipType = membershipType;
        this.planType = planType;
        this.tier = tier;
        this.price = price;
        this.tierBenefits = tierBenefits;
    }

    public String getId() {
        return id;
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

    public BigDecimal getPrice() {
        return price;
    }

    public TierBenefits getTierBenefits() {
        return tierBenefits;
    }
}


