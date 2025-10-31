package com.conceptandcoding.LowLevelDesign.MembershipProgram.model;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.DiscountType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DiscountRule {
    private final DiscountType type;
    private final BigDecimal value;
    private final Set<String> applicableProductIds;
    private final Set<String> applicableCategoryIds;

    public DiscountRule(DiscountType type, BigDecimal value, Set<String> applicableProductIds, Set<String> applicableCategoryIds) {
        this.type = type;
        this.value = value;
        this.applicableProductIds = applicableProductIds == null ? new HashSet<>() : new HashSet<>(applicableProductIds);
        this.applicableCategoryIds = applicableCategoryIds == null ? new HashSet<>() : new HashSet<>(applicableCategoryIds);
    }

    public DiscountType getType() {
        return type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Set<String> getApplicableProductIds() {
        return Collections.unmodifiableSet(applicableProductIds);
    }

    public Set<String> getApplicableCategoryIds() {
        return Collections.unmodifiableSet(applicableCategoryIds);
    }
}


