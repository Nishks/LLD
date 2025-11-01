package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.builder;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.DiscountType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.CouponRule;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.DiscountRule;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.FreeDeliveryRule;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.TierBenefits;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TierBenefitsBuilder {
    private MembershipTier tier;
    private BigDecimal minOrderForFreeDelivery;
    private List<DiscountRule> discountRules = new ArrayList<>();
    private List<CouponRule> couponRules = new ArrayList<>();

    public TierBenefitsBuilder withTier(MembershipTier tier) {
        this.tier = tier;
        return this;
    }

    public TierBenefitsBuilder withFreeDeliveryThreshold(BigDecimal minOrderAmount) {
        this.minOrderForFreeDelivery = minOrderAmount;
        return this;
    }

    public TierBenefitsBuilder withFreeDeliveryAlways() {
        this.minOrderForFreeDelivery = BigDecimal.ZERO;
        return this;
    }

    public TierBenefitsBuilder addPercentDiscount(BigDecimal percent, Set<String> categories) {
        DiscountRule rule = new DiscountRule(
                DiscountType.PERCENT,
                percent,
                Collections.emptySet(),
                categories != null ? categories : Collections.emptySet()
        );
        discountRules.add(rule);
        return this;
    }

    public TierBenefitsBuilder addFlatDiscount(BigDecimal amount, Set<String> categories) {
        DiscountRule rule = new DiscountRule(
                DiscountType.FLAT,
                amount,
                Collections.emptySet(),
                categories != null ? categories : Collections.emptySet()
        );
        discountRules.add(rule);
        return this;
    }

    public TierBenefitsBuilder addCoupon(String code, DiscountType type, BigDecimal value) {
        DiscountRule couponDiscount = new DiscountRule(type, value, Collections.emptySet(), Collections.emptySet());
        couponRules.add(new CouponRule(code, couponDiscount));
        return this;
    }

    public TierBenefits build() {
        if (tier == null) {
            throw new IllegalStateException("Tier must be set");
        }
        FreeDeliveryRule freeDeliveryRule = new FreeDeliveryRule(
                minOrderForFreeDelivery != null ? minOrderForFreeDelivery : new BigDecimal("999999")
        );
        return new TierBenefits(tier, freeDeliveryRule, discountRules, couponRules);
    }
}

