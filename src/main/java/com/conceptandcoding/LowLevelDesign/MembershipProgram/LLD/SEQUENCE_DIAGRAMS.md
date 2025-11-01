# Sequence Diagrams - Membership Program

## 📋 Table of Contents
1. [Subscribe Flow](#1-subscribe-flow)
2. [Tier Upgrade Flow](#2-tier-upgrade-flow)
3. [Auto-Upgrade Flow](#3-auto-upgrade-flow)
4. [Plan Creation Flow](#4-plan-creation-flow)
5. [Expiry Scheduler Flow](#5-expiry-scheduler-flow)
6. [Checkout with Benefits Flow](#6-checkout-with-benefits-flow)

---

## 1. Subscribe Flow

```mermaid
sequenceDiagram
    actor User
    participant Controller as MembershipController
    participant SubSvc as SubscriptionService
    participant SubRepo as SubscriptionRepository

    User->>Controller: subscribe(userId, PlanType, MembershipType, Tier)
    Controller->>SubSvc: subscribe(userId, PlanType, MembershipType, Tier)
    
    SubSvc->>SubSvc: lockForUser(userId)
    SubSvc->>SubSvc: lock.lock()
    
    SubSvc->>SubRepo: findByUserId(userId)
    SubRepo-->>SubSvc: Optional<Subscription>
    
    alt Existing Active Subscription
        SubSvc->>SubSvc: Update existing subscription
    else New Subscription
        SubSvc->>SubSvc: Create new Subscription
    end
    
    SubSvc->>SubRepo: save(subscription)
    SubSvc->>SubSvc: lock.unlock()
    
    SubSvc-->>Controller: Subscription
    Controller-->>User: Subscription
```

---

## 2. Tier Upgrade Flow

```mermaid
sequenceDiagram
    actor User
    participant Controller as MembershipController
    participant SubSvc as SubscriptionService
    participant Strategy as TierEvaluationStrategy
    participant SubRepo as SubscriptionRepository

    User->>Controller: upgradeTier(userId, MembershipTier.GOLD)
    Controller->>SubSvc: upgradeTier(userId, GOLD)
    
    SubSvc->>SubSvc: lockForUser(userId)
    SubSvc->>SubSvc: lock.lock()
    
    SubSvc->>SubRepo: findByUserId(userId)
    SubRepo-->>SubSvc: Optional<Subscription>
    
    SubSvc->>SubSvc: Validate subscription is ACTIVE
    SubSvc->>SubSvc: Get current MembershipType
    
    SubSvc->>Strategy: qualifies(userId, GOLD)
    
    Strategy->>Strategy: Check order count / spend / cohort
    Strategy-->>SubSvc: true/false
    
    alt User Qualifies
        SubSvc->>SubSvc: Check if GOLD > current tier
        SubSvc->>SubSvc: Update subscription.tier = GOLD
        SubSvc->>SubRepo: save(subscription)
    else User Doesn't Qualify
        SubSvc->>SubSvc: Return subscription unchanged
    end
    
    SubSvc->>SubSvc: lock.unlock()
    SubSvc-->>Controller: Optional<Subscription>
    Controller-->>User: Updated Subscription
```

---

## 3. Auto-Upgrade Flow

```mermaid
sequenceDiagram
    actor User
    participant Controller as MembershipController
    participant SubSvc as SubscriptionService
    participant Strategy as CompositeTierEvaluationStrategy
    participant SubRepo as SubscriptionRepository

    User->>Controller: autoUpgradeToHighest(userId)
    Controller->>SubSvc: upgradeTier(userId, null)
    
    SubSvc->>SubSvc: lockForUser(userId)
    SubSvc->>SubSvc: lock.lock()
    
    SubSvc->>SubRepo: findByUserId(userId)
    SubRepo-->>SubSvc: Subscription (current: LOYAL_SILVER)
    
    SubSvc->>SubSvc: Get current MembershipType (LOYAL)
    
    loop Check PLATINUM, GOLD, SILVER (in reverse order)
        SubSvc->>Strategy: qualifies(userId, PLATINUM)
        Strategy->>Strategy: OrderCountBasedStrategy
        Strategy->>Strategy: MonthlySpendBasedStrategy
        Strategy-->>SubSvc: false (doesn't qualify)
        
        SubSvc->>Strategy: qualifies(userId, GOLD)
        Strategy-->>SubSvc: true (qualifies!)
        SubSvc->>SubSvc: Break loop (found highest)
    end
    
    SubSvc->>SubSvc: tierToUpgrade = GOLD
    SubSvc->>SubSvc: Update subscription.tier = GOLD
    SubSvc->>SubRepo: save(subscription)
    
    SubSvc->>SubSvc: lock.unlock()
    SubSvc-->>Controller: Optional<Subscription>
    Controller-->>User: Upgraded Subscription (LOYAL_GOLD)
```

---

## 4. Plan Creation Flow

```mermaid
sequenceDiagram
    participant Main
    participant PlanRepo as PlanRepository
    participant PlanFactory
    participant BenefitFactory as BenefitConfigurationFactory
    participant PriceCalc as PriceCalculator
    participant TBBuilder as TierBenefitsBuilder
    participant BCBuilder as BenefitConfigBuilder

    Main->>PlanRepo: Constructor
    PlanRepo->>PlanFactory: createAllPlans()
    
    loop For each PlanType (MONTHLY, QUARTERLY, YEARLY)
        PlanFactory->>BenefitFactory: createForPlanType(MONTHLY)
        
        BenefitFactory->>BCBuilder: new BenefitConfigBuilder()
        
        loop For each Tier (SILVER, GOLD, PLATINUM)
            BenefitFactory->>TBBuilder: new TierBenefitsBuilder()
            BenefitFactory->>TBBuilder: withTier(SILVER)
            BenefitFactory->>TBBuilder: withFreeDeliveryThreshold(999)
            BenefitFactory->>TBBuilder: addPercentDiscount(3%, categories)
            BenefitFactory->>TBBuilder: addCoupon(...)
            TBBuilder-->>BenefitFactory: TierBenefits (STANDARD_SILVER)
            
            BenefitFactory->>TBBuilder: (repeat for LOYAL_SILVER)
            TBBuilder-->>BenefitFactory: TierBenefits (LOYAL_SILVER)
            
            BenefitFactory->>TBBuilder: (repeat for VIP_SILVER)
            TBBuilder-->>BenefitFactory: TierBenefits (VIP_SILVER)
            
            BenefitFactory->>BCBuilder: addAllMembershipTypes(SILVER, std, loyal, vip)
        end
        
        BCBuilder-->>BenefitFactory: BenefitConfig (all 9 combinations)
        BenefitFactory-->>PlanFactory: BenefitConfig
        
        loop For each (MembershipType, Tier) combination
            PlanFactory->>BenefitFactory: Extract TierBenefits (VIP, MONTHLY, GOLD)
            BenefitFactory-->>PlanFactory: TierBenefits
            
            PlanFactory->>PriceCalc: calculate(MONTHLY, VIP, GOLD)
            PriceCalc-->>PlanFactory: BigDecimal (price)
            
            PlanFactory->>PlanFactory: new MembershipPlan(..., tierBenefits, price)
        end
    end
    
    PlanFactory-->>PlanRepo: List<MembershipPlan> (27 plans)
    PlanRepo->>PlanRepo: Store in Map<PlanKey, MembershipPlan>
```

---

## 5. Expiry Scheduler Flow

```mermaid
sequenceDiagram
    participant Scheduler as SubscriptionExpiryScheduler
    participant SubSvc as SubscriptionService
    participant SubRepo as SubscriptionRepository

    Note over Scheduler: Runs every 60 seconds
    
    Scheduler->>SubSvc: expireDueSubscriptions()
    SubSvc->>SubRepo: findAll()
    SubRepo-->>SubSvc: Collection<Subscription>
    
    loop For each Subscription
        SubSvc->>SubSvc: Check if status == ACTIVE && endDate < today
        
        alt Subscription is Due
            SubSvc->>SubSvc: lockForUser(userId)
            SubSvc->>SubSvc: lock.lock()
            
            SubSvc->>SubRepo: findByUserId(userId) [re-check]
            SubRepo-->>SubSvc: Subscription
            
            alt Still Active and Due
                SubSvc->>SubSvc: subscription.setStatus(EXPIRED)
                SubSvc->>SubRepo: save(subscription)
                Note right of SubSvc: updated++
            end
            
            SubSvc->>SubSvc: lock.unlock()
        end
    end
    
    SubSvc-->>Scheduler: int (count of expired)
```

---

## 6. Checkout with Benefits Flow

```mermaid
sequenceDiagram
    actor User
    participant Controller as MembershipController
    participant PlanSvc as PlanService
    participant BenefitSvc as BenefitService
    participant TierConfig as TierConfigRepository

    User->>Controller: Checkout (has subscription)
    
    Controller->>PlanSvc: getPlan(MembershipType, PlanType, Tier)
    PlanSvc-->>Controller: MembershipPlan (with TierBenefits)
    
    Controller->>BenefitSvc: effectiveBenefits(plan)
    BenefitSvc->>BenefitSvc: plan.getTierBenefits()
    BenefitSvc->>BenefitSvc: Get base TierBenefits
    
    BenefitSvc->>TierConfig: getExtraDiscountForTier(tier)
    TierConfig-->>BenefitSvc: int (extra discount %)
    
    BenefitSvc->>BenefitSvc: Create DiscountRule (tier extra)
    BenefitSvc->>BenefitSvc: mergeDiscounts(base, tierExtra)
    
    BenefitSvc-->>Controller: TierBenefits (enhanced)
    
    Controller->>Controller: Calculate checkout:
    Note right of Controller: - Check freeDelivery threshold<br/>- Apply discount rules<br/>- Show available coupons<br/>- Calculate payable amount
    
    Controller-->>User: Checkout Summary
```

---

## 🔄 Concurrent User Flow

```mermaid
sequenceDiagram
    par User A Thread
        UserA->>Controller: subscribe(userA, MONTHLY, LOYAL, SILVER)
        Controller->>SubSvc: subscribe(...)
        Note over SubSvc: Lock userA
        SubSvc->>SubRepo: save(subscription)
        SubSvc-->>Controller: Subscription
    and User B Thread
        UserB->>Controller: subscribe(userB, YEARLY, VIP, GOLD)
        Controller->>SubSvc: subscribe(...)
        Note over SubSvc: Lock userB (different lock)
        SubSvc->>SubRepo: save(subscription)
        SubSvc-->>Controller: Subscription
    and User C Thread
        UserC->>Controller: subscribe(userC, QUARTERLY, STANDARD, SILVER)
        Controller->>SubSvc: subscribe(...)
        Note over SubSvc: Lock userC (different lock)
        SubSvc->>SubRepo: save(subscription)
        SubSvc-->>Controller: Subscription
    end
```

**Key Point**: Each user has a separate lock, allowing concurrent operations for different users while preventing race conditions for the same user.

---

## 📊 Tier Evaluation Strategy Flow

```mermaid
sequenceDiagram
    participant SubSvc as SubscriptionService
    participant Composite as CompositeTierEvaluationStrategy
    participant OrderStrat as OrderCountBasedStrategy
    participant SpendStrat as MonthlySpendBasedStrategy
    participant CohortStrat as CohortBasedStrategy

    SubSvc->>Composite: qualifies(userId, PLATINUM)
    
    Composite->>OrderStrat: qualifies(userId, PLATINUM)
    OrderStrat->>OrderStrat: Check userMonthlyOrderCount
    OrderStrat-->>Composite: false (orders < 10)
    
    Composite->>SpendStrat: qualifies(userId, PLATINUM)
    SpendStrat->>SpendStrat: Check userMonthlySpend
    SpendStrat-->>Composite: true (spend >= 10000)
    
    Note over Composite: Found qualifying strategy (OR logic)
    Composite-->>SubSvc: true
    
    Note over SubSvc: User qualifies for PLATINUM tier
```

---

## 🎯 Summary

These sequence diagrams show:
1. **Synchronous flows**: Subscribe, Upgrade, Checkout
2. **Background processes**: Expiry scheduler
3. **Complex creation**: Plan factory with builders
4. **Concurrent operations**: Multiple users simultaneously
5. **Strategy pattern**: Tier evaluation with multiple strategies

Each flow demonstrates proper locking, error handling, and clean separation of concerns.

