# Membership Program - Low Level Design (LLD)

## 📋 Table of Contents
1. [Overview](#overview)
2. [System Requirements](#system-requirements)
3. [Architecture](#architecture)
4. [Class Diagram](#class-diagram)
5. [Key Flows](#key-flows)
6. [Design Patterns](#design-patterns)
7. [Data Models](#data-models)
8. [API Endpoints](#api-endpoints)
9. [Concurrency Handling](#concurrency-handling)

---

## 🎯 Overview

The Membership Program is a subscription-based system that allows users to subscribe to different membership plans (Monthly, Quarterly, Yearly) with various membership types (STANDARD, LOYAL, VIP), each having three tiers (SILVER, GOLD, PLATINUM). The system supports tier upgrades/downgrades, benefit management, and automatic subscription expiry.

---

## 📝 System Requirements

### Core Features
1. **Membership Plans**: Users can choose from Monthly, Quarterly, and Yearly plans
2. **Membership Types**: STANDARD, LOYAL, and VIP memberships with distinct benefits
3. **Tier System**: Each membership type has SILVER, GOLD, and PLATINUM tiers
4. **Benefits Management**: 
   - Free delivery with configurable minimum order value
   - Discount rules (percentage or flat) on products/categories
   - Exclusive coupons per tier
5. **Subscription Management**:
   - Subscribe to a plan (membership type + tier)
   - Upgrade/downgrade tiers within same membership type
   - Auto-upgrade to highest qualifying tier
   - Cancel subscriptions
6. **Tier Evaluation**: 
   - Order count-based qualification
   - Monthly spend-based qualification
   - Cohort-based qualification
7. **Automatic Expiry**: Background scheduler expires overdue subscriptions

---

## 🏗️ Architecture

### Package Structure
```
MembershipProgram/
├── Enums/
│   ├── MembershipType (STANDARD, LOYAL, VIP)
│   ├── MembershipTier (SILVER, GOLD, PLATINUM)
│   ├── PlanType (MONTHLY, QUARTERLY, YEARLY)
│   ├── SubscriptionStatus (ACTIVE, CANCELED, EXPIRED)
│   └── DiscountType (PERCENT, FLAT)
├── model/
│   ├── MembershipPlan
│   ├── Subscription
│   ├── TierBenefits
│   ├── BenefitConfig
│   ├── DiscountRule
│   ├── FreeDeliveryRule
│   ├── CouponRule
│   └── PlanKey
├── repository/
│   ├── PlanRepository
│   ├── SubscriptionRepository
│   ├── TierConfigRepository
│   └── builder/
│       ├── TierBenefitsBuilder
│       └── BenefitConfigBuilder
│   └── factory/
│       ├── BenefitConfigurationFactory
│       ├── PlanFactory
│       └── PriceCalculator
├── service/
│   ├── PlanService
│   ├── SubscriptionService
│   ├── BenefitService
│   └── SubscriptionExpiryScheduler
├── controller/
│   └── MembershipController
└── strategy/
    ├── TierEvaluationStrategy (interface)
    ├── OrderCountBasedStrategy
    ├── MonthlySpendBasedStrategy
    ├── CohortBasedStrategy
    └── CompositeTierEvaluationStrategy
```

### Layered Architecture
```
┌─────────────────────────────────────┐
│     Controller Layer                │
│   (MembershipController)            │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Service Layer                   │
│  PlanService | SubscriptionService   │
│  BenefitService | ExpiryScheduler    │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│    Repository Layer                  │
│  PlanRepository | SubscriptionRepo   │
│  TierConfigRepository               │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Model Layer                     │
│  MembershipPlan | Subscription       │
│  TierBenefits | BenefitConfig        │
└─────────────────────────────────────┘
```

---

## 📊 Class Diagram

See [CLASS_DIAGRAM.md](./CLASS_DIAGRAM.md) for detailed class relationships.

---

## 🔄 Key Flows

### 1. Subscription Flow
```
User → Controller.subscribe()
  → SubscriptionService.subscribe()
    → Lock user (per-user lock)
    → Create/Update Subscription
    → Save to SubscriptionRepository
    → Return Subscription
```

### 2. Benefit Evaluation Flow
```
User → Controller.getPlan()
  → PlanService.getPlan()
    → PlanRepository.getByKey()
      → Returns MembershipPlan with TierBenefits
```

### 3. Tier Upgrade Flow
```
User → Controller.upgradeTier()
  → SubscriptionService.upgradeTier()
    → Lock user
    → TierEvaluationStrategy.qualifies()
      → CompositeTierEvaluationStrategy
        → OrderCountBasedStrategy | MonthlySpendBasedStrategy
    → Update Subscription.tier
    → Save to Repository
```

### 4. Plan Creation Flow
```
PlanRepository Constructor
  → PlanFactory.createAllPlans()
    → For each PlanType:
      → BenefitConfigurationFactory.createForPlanType()
        → For each Tier:
          → TierBenefitsBuilder (build benefits)
          → BenefitConfigBuilder (assemble)
      → For each (MembershipType, Tier):
        → Extract specific TierBenefits
        → PriceCalculator.calculate()
        → Create MembershipPlan
```

### 5. Expiry Flow
```
SubscriptionExpiryScheduler (runs every 60 seconds)
  → SubscriptionService.expireDueSubscriptions()
    → For each Subscription:
      → Check if endDate < today && status == ACTIVE
      → Lock user
      → Set status = EXPIRED
      → Save
```

---

## 🎨 Design Patterns

### 1. **Builder Pattern**
**Used in:** `TierBenefitsBuilder`, `BenefitConfigBuilder`

**Purpose:** Construct complex `TierBenefits` objects step-by-step with a fluent API.

**Example:**
```java
TierBenefits benefits = new TierBenefitsBuilder()
    .withTier(MembershipTier.GOLD)
    .withFreeDeliveryThreshold(new BigDecimal("499"))
    .addPercentDiscount(new BigDecimal("8"), categories)
    .addCoupon("GOLD15", DiscountType.PERCENT, new BigDecimal("15"))
    .build();
```

**Benefits:**
- Readable, fluent API
- Flexible construction
- Encapsulates complex object creation

### 2. **Factory Pattern**
**Used in:** `PlanFactory`, `BenefitConfigurationFactory`

**Purpose:** Encapsulate object creation logic and provide a single entry point.

**Example:**
```java
BenefitConfig config = benefitFactory.createForPlanType(PlanType.MONTHLY);
List<MembershipPlan> plans = planFactory.createAllPlans();
```

**Benefits:**
- Centralized creation logic
- Easy to modify plan creation
- Hides complexity from clients

### 3. **Strategy Pattern**
**Used in:** `TierEvaluationStrategy` and implementations

**Purpose:** Encapsulate different tier qualification algorithms as interchangeable strategies.

**Example:**
```java
TierEvaluationStrategy strategy = new CompositeTierEvaluationStrategy()
    .add(new OrderCountBasedStrategy(...))
    .add(new MonthlySpendBasedStrategy(...))
    .add(new CohortBasedStrategy(...));
```

**Benefits:**
- Easy to add new qualification rules
- Runtime strategy selection
- Open/Closed Principle compliance

### 4. **Composite Pattern**
**Used in:** `CompositeTierEvaluationStrategy`

**Purpose:** Combine multiple tier evaluation strategies with OR logic.

**Benefits:**
- Flexible strategy composition
- Can combine multiple rules
- Easy to extend with AND logic variant

### 5. **Repository Pattern**
**Used in:** `PlanRepository`, `SubscriptionRepository`, `TierConfigRepository`

**Purpose:** Abstract data access and provide a clean interface.

**Benefits:**
- Decouples business logic from data storage
- Easy to swap implementations
- Centralized data access

---

## 📦 Data Models

### MembershipPlan
- **Fields:** id, membershipType, planType, tier, price, tierBenefits
- **Key:** Each plan is unique by (MembershipType, PlanType, MembershipTier)

### Subscription
- **Fields:** id, userId, planType, membershipType, tier, startDate, endDate, status
- **Relationships:** One subscription per user (active)

### TierBenefits
- **Fields:** tier, freeDeliveryRule, discountRules[], couponRules[]
- **Purpose:** Encapsulates all benefits for a specific tier

### BenefitConfig
- **Purpose:** Temporary container during plan creation
- **Stores:** Map of (MembershipType_Tier) → TierBenefits

---

## 🔌 API Endpoints (Controller Methods)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `getPlans()` | GET /membership/plans | Get all available plans |
| `subscribe()` | POST /membership/subscribe | Subscribe with membership type and tier |
| `upgradeTier()` | POST /membership/upgrade | Upgrade to specific tier |
| `autoUpgradeToHighest()` | POST /membership/auto-upgrade | Auto-upgrade to highest qualifying tier |
| `downgradeTier()` | POST /membership/downgrade | Downgrade to specific tier |
| `cancel()` | POST /membership/cancel | Cancel subscription |
| `getCurrent()` | GET /membership/current | Get user's current subscription |

---

## 🔒 Concurrency Handling

### Per-User Locks
- Each user has a dedicated `ReentrantLock`
- Prevents race conditions in subscription operations
- Lock is stored in `ConcurrentMap<String, Lock>`

### Thread-Safe Collections
- `SubscriptionRepository`: Uses `ConcurrentHashMap`
- `PlanRepository`: Uses `HashMap` (read-only after initialization)

### Background Scheduler
- `SubscriptionExpiryScheduler` uses `ScheduledExecutorService`
- Runs expiry check every 60 seconds
- Thread-safe subscription updates

---

## 🚀 How to Run

1. **Compile:**
   ```bash
   javac -d . MembershipProgram/**/*.java
   ```

2. **Run:**
   ```bash
   java com.conceptandcoding.LowLevelDesign.MembershipProgram.Main
   ```

3. **Expected Output:**
   - Displays all available plans organized by membership type
   - Demonstrates 3 concurrent user flows
   - Shows subscription, checkout, and auto-upgrade operations

---

## 🔧 Extension Points

1. **New Membership Types**: Add to `MembershipType` enum and update `BenefitConfigurationFactory`
2. **New Qualification Rules**: Implement `TierEvaluationStrategy` and add to composite
3. **New Benefit Types**: Extend `TierBenefits` model and builder
4. **Different Pricing Models**: Modify `PriceCalculator` logic
5. **Persistence**: Replace in-memory repositories with database implementations

---

## 📈 Scalability Considerations

1. **Caching**: Plans can be cached (rarely change)
2. **Database**: Move from in-memory to database for production
3. **Distributed Locks**: Replace per-user locks with distributed locks (Redis/Zookeeper) for multi-instance
4. **Event-Driven**: Use message queues for subscription expiry in distributed systems
5. **Read Replicas**: Separate read/write for subscription queries

---

## ✅ Testing Recommendations

1. **Unit Tests**: 
   - TierBenefitsBuilder
   - PriceCalculator
   - TierEvaluationStrategy implementations

2. **Integration Tests**:
   - SubscriptionService (with concurrent scenarios)
   - PlanFactory
   - BenefitService

3. **Concurrency Tests**:
   - Multiple users subscribing simultaneously
   - Upgrade race conditions
   - Expiry scheduler correctness

---

## 📚 Additional Resources

- See [CLASS_DIAGRAM.md](./CLASS_DIAGRAM.md) for detailed class relationships
- See [SEQUENCE_DIAGRAMS.md](./SEQUENCE_DIAGRAMS.md) for flow diagrams
- See [DESIGN_PATTERNS.md](./DESIGN_PATTERNS.md) for pattern details

