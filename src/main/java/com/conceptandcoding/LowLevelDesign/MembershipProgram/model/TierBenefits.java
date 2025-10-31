package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TierBenefits {
    private final MembershipTier tier;
    private final FreeDeliveryRule freeDeliveryRule;
    private final List<DiscountRule> discountRules;
    private final List<CouponRule> couponRules;

    public TierBenefits(MembershipTier tier, FreeDeliveryRule freeDeliveryRule, List<DiscountRule> discountRules, List<CouponRule> couponRules) {
        this.tier = tier;
        this.freeDeliveryRule = freeDeliveryRule;
        this.discountRules = discountRules == null ? new ArrayList<>() : new ArrayList<>(discountRules);
        this.couponRules = couponRules == null ? new ArrayList<>() : new ArrayList<>(couponRules);
    }

    public MembershipTier getTier() {
        return tier;
    }

    public FreeDeliveryRule getFreeDeliveryRule() {
        return freeDeliveryRule;
    }

    public List<DiscountRule> getDiscountRules() {
        return Collections.unmodifiableList(discountRules);
    }

    public List<CouponRule> getCouponRules() {
        return Collections.unmodifiableList(couponRules);
    }
}


