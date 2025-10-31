package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

public class CouponRule {
    private final String code;
    private final DiscountRule discountRule;

    public CouponRule(String code, DiscountRule discountRule) {
        this.code = code;
        this.discountRule = discountRule;
    }

    public String getCode() {
        return code;
    }

    public DiscountRule getDiscountRule() {
        return discountRule;
    }
}


