package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.DiscountType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.PlanType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.*;

import java.math.BigDecimal;
import java.util.*;

public class PlanRepository {
    private final Map<PlanType, MembershipPlan> planTypeToPlan = new EnumMap<>(PlanType.class);

    public PlanRepository() {
        seedDefaultPlans();
    }

    private void seedDefaultPlans() {
        // Monthly plan benefits per tier
        BenefitConfig monthlyBenefits = new BenefitConfig()
                .addTierBenefits(new TierBenefits(
                        MembershipTier.SILVER,
                        new FreeDeliveryRule(new BigDecimal("999")),
                        Arrays.asList(
                                new DiscountRule(DiscountType.PERCENT, new BigDecimal("3"), Collections.emptySet(), new HashSet<>(Arrays.asList("CAT_EVERYDAY")))
                        ),
                        Arrays.asList(
                                new CouponRule("SILV5", new DiscountRule(DiscountType.FLAT, new BigDecimal("50"), Collections.emptySet(), Collections.emptySet()))
                        )
                ))
                .addTierBenefits(new TierBenefits(
                        MembershipTier.GOLD,
                        new FreeDeliveryRule(new BigDecimal("699")),
                        Arrays.asList(
                                new DiscountRule(DiscountType.PERCENT, new BigDecimal("5"), Collections.emptySet(), new HashSet<>(Arrays.asList("CAT_EVERYDAY", "CAT_FRESH")))
                        ),
                        Arrays.asList(
                                new CouponRule("GOLD10", new DiscountRule(DiscountType.PERCENT, new BigDecimal("10"), Collections.emptySet(), Collections.emptySet()))
                        )
                ))
                .addTierBenefits(new TierBenefits(
                        MembershipTier.PLATINUM,
                        new FreeDeliveryRule(new BigDecimal("0")),
                        Arrays.asList(
                                new DiscountRule(DiscountType.PERCENT, new BigDecimal("8"), Collections.emptySet(), Collections.emptySet())
                        ),
                        Arrays.asList(
                                new CouponRule("PLAT20", new DiscountRule(DiscountType.PERCENT, new BigDecimal("20"), Collections.emptySet(), Collections.emptySet()))
                        )
                ));

        planTypeToPlan.put(PlanType.MONTHLY, new MembershipPlan(
                "plan_monthly",
                PlanType.MONTHLY,
                new BigDecimal("199.00"),
                monthlyBenefits
        ));

        // Quarterly plan
        BenefitConfig quarterlyBenefits = new BenefitConfig()
                .addTierBenefits(new TierBenefits(
                        MembershipTier.SILVER,
                        new FreeDeliveryRule(new BigDecimal("799")),
                        Arrays.asList(
                                new DiscountRule(DiscountType.PERCENT, new BigDecimal("4"), Collections.emptySet(), new HashSet<>(Arrays.asList("CAT_EVERYDAY")))
                        ),
                        Collections.emptyList()
                ))
                .addTierBenefits(new TierBenefits(
                        MembershipTier.GOLD,
                        new FreeDeliveryRule(new BigDecimal("499")),
                        Arrays.asList(
                                new DiscountRule(DiscountType.PERCENT, new BigDecimal("7"), Collections.emptySet(), Collections.emptySet())
                        ),
                        Arrays.asList(
                                new CouponRule("QGOLD15", new DiscountRule(DiscountType.PERCENT, new BigDecimal("15"), Collections.emptySet(), Collections.emptySet()))
                        )
                ))
                .addTierBenefits(new TierBenefits(
                        MembershipTier.PLATINUM,
                        new FreeDeliveryRule(new BigDecimal("0")),
                        Arrays.asList(
                                new DiscountRule(DiscountType.PERCENT, new BigDecimal("10"), Collections.emptySet(), Collections.emptySet())
                        ),
                        Arrays.asList(
                                new CouponRule("QPLAT25", new DiscountRule(DiscountType.PERCENT, new BigDecimal("25"), Collections.emptySet(), Collections.emptySet()))
                        )
                ));

        planTypeToPlan.put(PlanType.QUARTERLY, new MembershipPlan(
                "plan_quarterly",
                PlanType.QUARTERLY,
                new BigDecimal("499.00"),
                quarterlyBenefits
        ));

        // Yearly plan
        BenefitConfig yearlyBenefits = new BenefitConfig()
                .addTierBenefits(new TierBenefits(
                        MembershipTier.SILVER,
                        new FreeDeliveryRule(new BigDecimal("599")),
                        Arrays.asList(
                                new DiscountRule(DiscountType.PERCENT, new BigDecimal("5"), Collections.emptySet(), Collections.emptySet())
                        ),
                        Collections.emptyList()
                ))
                .addTierBenefits(new TierBenefits(
                        MembershipTier.GOLD,
                        new FreeDeliveryRule(new BigDecimal("299")),
                        Arrays.asList(
                                new DiscountRule(DiscountType.PERCENT, new BigDecimal("9"), Collections.emptySet(), Collections.emptySet())
                        ),
                        Arrays.asList(
                                new CouponRule("YGOLD20", new DiscountRule(DiscountType.PERCENT, new BigDecimal("20"), Collections.emptySet(), Collections.emptySet()))
                        )
                ))
                .addTierBenefits(new TierBenefits(
                        MembershipTier.PLATINUM,
                        new FreeDeliveryRule(new BigDecimal("0")),
                        Arrays.asList(
                                new DiscountRule(DiscountType.PERCENT, new BigDecimal("12"), Collections.emptySet(), Collections.emptySet())
                        ),
                        Arrays.asList(
                                new CouponRule("YPLAT30", new DiscountRule(DiscountType.PERCENT, new BigDecimal("30"), Collections.emptySet(), Collections.emptySet()))
                        )
                ));

        planTypeToPlan.put(PlanType.YEARLY, new MembershipPlan(
                "plan_yearly",
                PlanType.YEARLY,
                new BigDecimal("1499.00"),
                yearlyBenefits
        ));
    }

    public List<MembershipPlan> getAllPlans() {
        return Collections.unmodifiableList(new ArrayList<>(planTypeToPlan.values()));
    }

    public MembershipPlan getByType(PlanType type) {
        return planTypeToPlan.get(type);
    }
}


