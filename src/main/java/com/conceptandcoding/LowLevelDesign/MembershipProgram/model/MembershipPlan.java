package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;

import java.math.BigDecimal;

public class MembershipPlan {
    private final String id;
    private final PlanType planType;
    private final BigDecimal price;
    private final BenefitConfig benefitConfig;

    public MembershipPlan(String id, PlanType planType, BigDecimal price, BenefitConfig benefitConfig) {
        this.id = id;
        this.planType = planType;
        this.price = price;
        this.benefitConfig = benefitConfig;
    }

    public String getId() {
        return id;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BenefitConfig getBenefitConfig() {
        return benefitConfig;
    }
}


