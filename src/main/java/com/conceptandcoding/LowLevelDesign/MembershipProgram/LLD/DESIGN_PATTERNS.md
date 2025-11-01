# Design Patterns Used - Membership Program

## 📚 Overview

This document details all design patterns implemented in the Membership Program, their purpose, implementation, and benefits.

---

## 1. Builder Pattern

### **Purpose**
Construct complex objects step-by-step with a fluent, readable API.

### **Implementation**

#### **TierBenefitsBuilder**
```java
TierBenefits benefits = new TierBenefitsBuilder()
    .withTier(MembershipTier.GOLD)
    .withFreeDeliveryThreshold(new BigDecimal("499"))
    .addPercentDiscount(new BigDecimal("8"), Set.of("CAT_EVERYDAY"))
    .addCoupon("GOLD15", DiscountType.PERCENT, new BigDecimal("15"))
    .build();
```

**Key Features:**
- Fluent API (method chaining)
- Optional parameters with sensible defaults
- Validation in `build()` method
- Immutable result

#### **BenefitConfigBuilder**
```java
BenefitConfig config = new BenefitConfigBuilder()
    .addTierBenefits(MembershipType.STANDARD, standardBenefits)
    .addTierBenefits(MembershipType.LOYAL, loyalBenefits)
    .addTierBenefits(MembershipType.VIP, vipBenefits)
    .build();
```

### **Benefits**
✅ Readable code  
✅ Flexible construction (optional fields)  
✅ Encapsulates complex object creation  
✅ Validation at build time  

### **When to Use**
- Objects with many optional parameters
- Complex nested object construction
- Need for immutable objects

---

## 2. Factory Pattern

### **Purpose**
Encapsulate object creation logic and provide a centralized creation point.

### **Implementation**

#### **PlanFactory**
```java
public class PlanFactory {
    private final BenefitConfigurationFactory benefitFactory;
    private final PriceCalculator priceCalculator;
    
    public List<MembershipPlan> createAllPlans() {
        // Creates all 27 plans (3 membership types × 3 plan types × 3 tiers)
        // Encapsulates all creation logic
    }
}
```

**Usage:**
```java
PlanFactory factory = new PlanFactory();
List<MembershipPlan> plans = factory.createAllPlans();
```

#### **BenefitConfigurationFactory**
```java
public class BenefitConfigurationFactory {
    public BenefitConfig createForPlanType(PlanType planType) {
        // Creates benefit config for a specific plan type
        // Hides complexity of benefit rule creation
    }
}
```

### **Benefits**
✅ Centralized creation logic  
✅ Easy to modify creation process  
✅ Hides complexity from clients  
✅ Can swap implementations easily  

### **Variations Used**
- **Simple Factory**: `PlanFactory`, `BenefitConfigurationFactory`
- **Factory Method**: Creation methods in factory classes

---

## 3. Strategy Pattern

### **Purpose**
Encapsulate different algorithms as interchangeable strategies, making them runtime-selectable.

### **Implementation**

#### **Interface**
```java
public interface TierEvaluationStrategy {
    boolean qualifies(String userId, MembershipTier targetTier);
}
```

#### **Concrete Strategies**

**OrderCountBasedStrategy**
```java
public class OrderCountBasedStrategy implements TierEvaluationStrategy {
    private final Map<MembershipTier, Integer> thresholdByTier;
    
    @Override
    public boolean qualifies(String userId, MembershipTier targetTier) {
        int orders = userMonthlyOrderCount.getOrDefault(userId, 0);
        int threshold = thresholdByTier.getOrDefault(targetTier, Integer.MAX_VALUE);
        return orders >= threshold;
    }
}
```

**MonthlySpendBasedStrategy**
```java
public class MonthlySpendBasedStrategy implements TierEvaluationStrategy {
    // Evaluates based on total monthly spending
}
```

**CohortBasedStrategy**
```java
public class CohortBasedStrategy implements TierEvaluationStrategy {
    // Evaluates based on user cohort membership
}
```

### **Usage**
```java
TierEvaluationStrategy strategy = new CompositeTierEvaluationStrategy()
    .add(new OrderCountBasedStrategy(orderCounts))
    .add(new MonthlySpendBasedStrategy(spends))
    .add(new CohortBasedStrategy(cohorts, mappings));

SubscriptionService service = new SubscriptionService(
    subscriptionRepository, 
    planService, 
    strategy // Strategy injected
);
```

### **Benefits**
✅ Open/Closed Principle: Add new strategies without modifying existing code  
✅ Runtime strategy selection  
✅ Encapsulates algorithms  
✅ Testable in isolation  

### **Extension Points**
- Add new qualification rules by implementing `TierEvaluationStrategy`
- Change strategy composition without affecting `SubscriptionService`

---

## 4. Composite Pattern

### **Purpose**
Compose multiple `TierEvaluationStrategy` objects into a tree structure to represent part-whole hierarchies.

### **Implementation**

#### **CompositeTierEvaluationStrategy**
```java
public class CompositeTierEvaluationStrategy implements TierEvaluationStrategy {
    private final List<TierEvaluationStrategy> strategies = new ArrayList<>();
    
    public CompositeTierEvaluationStrategy add(TierEvaluationStrategy strategy) {
        strategies.add(strategy);
        return this;
    }
    
    @Override
    public boolean qualifies(String userId, MembershipTier targetTier) {
        // OR logic: if ANY strategy qualifies, return true
        for (TierEvaluationStrategy strategy : strategies) {
            if (strategy.qualifies(userId, targetTier)) {
                return true;
            }
        }
        return false;
    }
}
```

### **Usage**
```java
TierEvaluationStrategy composite = new CompositeTierEvaluationStrategy()
    .add(new OrderCountBasedStrategy(...))
    .add(new MonthlySpendBasedStrategy(...))
    .add(new CohortBasedStrategy(...));

// Client code treats composite same as individual strategy
boolean qualifies = composite.qualifies(userId, MembershipTier.GOLD);
```

### **Benefits**
✅ Treats individual objects and compositions uniformly  
✅ Flexible combination of strategies  
✅ Easy to add AND logic variant (AllMatchComposite)  
✅ Maintains strategy interface  

### **Variations**
- **OR Composite**: Current implementation (any strategy qualifies)
- **AND Composite**: Could add `AllMatchCompositeTierEvaluationStrategy`

---

## 5. Repository Pattern

### **Purpose**
Abstract data access and provide a clean interface independent of data storage implementation.

### **Implementation**

#### **PlanRepository**
```java
public class PlanRepository {
    private final Map<PlanKey, MembershipPlan> planKeyToPlan;
    
    public MembershipPlan getByKey(MembershipType, PlanType, MembershipTier);
    public List<MembershipPlan> getAllPlans();
}
```

#### **SubscriptionRepository**
```java
public class SubscriptionRepository {
    private final ConcurrentMap<String, Subscription> userIdToSubscription;
    
    public Optional<Subscription> findByUserId(String userId);
    public void save(Subscription subscription);
    public Collection<Subscription> findAll();
}
```

### **Benefits**
✅ Decouples business logic from data access  
✅ Easy to swap implementations (in-memory → database)  
✅ Centralized data access logic  
✅ Testable (can mock repositories)  

### **Future Extension**
Replace with database repositories:
```java
public class JdbcPlanRepository implements PlanRepository {
    // Database implementation
}
```

---

## 6. Service Layer Pattern

### **Purpose**
Encapsulate business logic and coordinate between repositories and models.

### **Implementation**

#### **SubscriptionService**
- Coordinates subscription operations
- Manages per-user locks for thread safety
- Delegates tier evaluation to strategies

#### **BenefitService**
- Computes effective benefits
- Merges tier-level discounts with plan benefits
- Encapsulates benefit calculation logic

#### **PlanService**
- Provides plan lookup operations
- Abstracts plan retrieval logic

### **Benefits**
✅ Separation of concerns  
✅ Reusable business logic  
✅ Testable services  
✅ Single entry point for operations  

---

## 7. Template Method Pattern (Implicit)

### **Purpose**
Define skeleton of algorithm in base class, letting subclasses override specific steps.

### **Implementation**

While not explicitly using inheritance, the pattern appears in:
- `BenefitConfigurationFactory` methods:
  - `createStandardBenefits()` 
  - `createLoyalBenefits()`
  - `createVipBenefits()`
  
All follow same structure:
1. Create builder
2. Set tier
3. Configure free delivery
4. Add discounts
5. Add coupons
6. Build

### **Benefits**
✅ Consistent benefit creation structure  
✅ Easy to add new membership types  
✅ Reduces code duplication  

---

## 8. Observer Pattern (Implicit in Scheduler)

### **Purpose**
Define one-to-many dependency between objects so when one changes, all dependents are notified.

### **Implementation**

**SubscriptionExpiryScheduler** acts as observer pattern:
- Observes time (via ScheduledExecutorService)
- Notifies (calls) `expireDueSubscriptions()` periodically

```java
scheduler.scheduleWithFixedDelay(() -> {
    subscriptionService.expireDueSubscriptions();
}, initialDelay, interval, TimeUnit.SECONDS);
```

### **Benefits**
✅ Decouples expiry logic from subscription operations  
✅ Automatic periodic execution  
✅ Easy to modify schedule  

---

## 🎯 Pattern Summary Table

| Pattern | Location | Purpose | Key Benefit |
|---------|----------|---------|-------------|
| **Builder** | `TierBenefitsBuilder`, `BenefitConfigBuilder` | Construct complex objects | Fluent API, readable code |
| **Factory** | `PlanFactory`, `BenefitConfigurationFactory` | Encapsulate creation | Centralized, modifiable |
| **Strategy** | `TierEvaluationStrategy` implementations | Algorithm selection | Extensible, testable |
| **Composite** | `CompositeTierEvaluationStrategy` | Combine strategies | Flexible combinations |
| **Repository** | `*Repository` classes | Data access abstraction | Swappable, testable |
| **Service Layer** | `*Service` classes | Business logic encapsulation | Reusable, organized |
| **Template Method** | `BenefitConfigurationFactory` methods | Algorithm skeleton | Consistent structure |

---

## 🔄 Pattern Interactions

### **Factory + Builder**
```java
// Factory uses Builder to create objects
BenefitConfigurationFactory {
    TierBenefitsBuilder builder = new TierBenefitsBuilder()
        .withTier(...)
        .addPercentDiscount(...)
        .build();
}
```

### **Strategy + Composite**
```java
// Composite combines multiple Strategies
CompositeTierEvaluationStrategy {
    List<TierEvaluationStrategy> strategies; // Composition
}
```

### **Service + Repository + Strategy**
```java
// Service orchestrates Repository and Strategy
SubscriptionService {
    SubscriptionRepository repository;
    TierEvaluationStrategy strategy; // Injected strategy
}
```

---

## 📈 Design Principles Applied

1. **SOLID Principles**:
   - **S**: Single Responsibility (each class has one job)
   - **O**: Open/Closed (strategies are extensible)
   - **L**: Liskov Substitution (strategies are interchangeable)
   - **I**: Interface Segregation (focused interfaces)
   - **D**: Dependency Inversion (depend on abstractions)

2. **DRY (Don't Repeat Yourself)**: Builders eliminate repetitive object creation

3. **Separation of Concerns**: Clear boundaries between layers

4. **Encapsulation**: Complex logic hidden behind simple interfaces

---

## 🚀 Extension Examples

### Adding New Strategy
```java
public class ReferralBasedStrategy implements TierEvaluationStrategy {
    @Override
    public boolean qualifies(String userId, MembershipTier targetTier) {
        // New qualification logic
    }
}

// Usage:
compositeStrategy.add(new ReferralBasedStrategy(...));
```

### Adding New Builder Method
```java
public TierBenefitsBuilder addEarlyAccess(boolean enabled) {
    // Add early access benefit
    return this;
}
```

### Swapping Repository
```java
// Replace in-memory with database
PlanRepository repository = new JdbcPlanRepository(dataSource);
PlanService service = new PlanService(repository);
```

---

## 📚 References

- **Gang of Four (GoF) Design Patterns**: Builder, Factory, Strategy, Composite
- **Martin Fowler Patterns**: Repository, Service Layer
- **Enterprise Patterns**: Layered Architecture

