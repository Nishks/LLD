package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

import java.math.BigDecimal;

public class FreeDeliveryRule {
    private final BigDecimal minOrderAmount;

    public FreeDeliveryRule(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }
}


