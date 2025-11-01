package com.conceptandcoding.LowLevelDesign.MembershipProgram.repository.builder;

import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipTier;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.Enums.MembershipType;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.BenefitConfig;
import com.conceptandcoding.LowLevelDesign.MembershipProgram.model.TierBenefits;

public class BenefitConfigBuilder {
    private final BenefitConfig config = new BenefitConfig();

    public BenefitConfigBuilder addTierBenefits(MembershipType membershipType, TierBenefits tierBenefits) {
        config.addTierBenefits(membershipType, tierBenefits);
        return this;
    }

    public BenefitConfigBuilder addAllMembershipTypes(MembershipTier tier, 
                                                      TierBenefits standardBenefits,
                                                      TierBenefits loyalBenefits,
                                                      TierBenefits vipBenefits) {
        config.addTierBenefits(MembershipType.STANDARD, standardBenefits);
        config.addTierBenefits(MembershipType.LOYAL, loyalBenefits);
        config.addTierBenefits(MembershipType.VIP, vipBenefits);
        return this;
    }

    public BenefitConfig build() {
        return config;
    }
}

