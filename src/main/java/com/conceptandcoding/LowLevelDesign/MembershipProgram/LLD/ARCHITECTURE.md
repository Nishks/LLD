# Architecture Overview - Membership Program

## 🏗️ System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Layer                            │
│                   (Main.java - Demo)                        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   Controller Layer                          │
│              MembershipController                           │
│  • getPlans()                                               │
│  • subscribe()                                              │
│  • upgradeTier() / autoUpgradeToHighest()                  │
│  • cancel() / getCurrent()                                  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Service Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ PlanService  │  │Subscription  │  │ BenefitService│     │
│  │              │  │   Service     │  │              │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│                            │                                 │
│                   ┌────────┴────────┐                       │
│                   │ Subscription    │                       │
│                   │ ExpiryScheduler │                       │
│                   └─────────────────┘                       │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                  Repository Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Plan        │  │ Subscription │  │ TierConfig   │     │
│  │ Repository  │  │  Repository   │  │  Repository  │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    Model Layer                               │
│  MembershipPlan | Subscription | TierBenefits               │
│  BenefitConfig | DiscountRule | CouponRule                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔀 Data Flow

### Read Flow (Get Plans)
```
Client → Controller → PlanService → PlanRepository → MembershipPlan
                                                          ↓
                                                     TierBenefits
```

### Write Flow (Subscribe)
```
Client → Controller → SubscriptionService → SubscriptionRepository
                                                ↓
                                           Subscription (saved)
```

### Complex Flow (Tier Upgrade)
```
Client → Controller → SubscriptionService
                          ↓
                   TierEvaluationStrategy (Strategy Pattern)
                          ↓
                   CompositeTierEvaluationStrategy
                          ↓
            [OrderCount | MonthlySpend | Cohort] Strategies
                          ↓
                   SubscriptionService
                          ↓
                   SubscriptionRepository
```

---

## 📦 Component Responsibilities

### **Controller Layer**
- **Responsibility**: API boundary, request/response handling
- **Classes**: `MembershipController`
- **Dependencies**: Services
- **No business logic** - delegates to services

### **Service Layer**
- **Responsibility**: Business logic, orchestration
- **Classes**: 
  - `PlanService`: Plan retrieval
  - `SubscriptionService`: Subscription lifecycle
  - `BenefitService`: Benefit calculation
  - `SubscriptionExpiryScheduler`: Background expiry
- **Dependencies**: Repositories, Strategies

### **Repository Layer**
- **Responsibility**: Data access abstraction
- **Classes**: 
  - `PlanRepository`: Plan storage
  - `SubscriptionRepository`: Subscription storage
  - `TierConfigRepository`: Tier configuration
- **Currently**: In-memory (HashMap/ConcurrentHashMap)
- **Future**: Database implementations

### **Model Layer**
- **Responsibility**: Domain entities and value objects
- **Classes**: `MembershipPlan`, `Subscription`, `TierBenefits`, etc.
- **Immutable or mutable**: Based on use case

### **Strategy Layer**
- **Responsibility**: Algorithm implementation (tier qualification)
- **Classes**: `TierEvaluationStrategy` implementations
- **Pattern**: Strategy + Composite

### **Factory/Builder Layer**
- **Responsibility**: Object creation
- **Classes**: `PlanFactory`, `BenefitConfigurationFactory`, Builders
- **Pattern**: Factory + Builder

---

## 🔒 Concurrency Architecture

### Thread Safety Strategy

#### **1. Per-User Locks**
```java
ConcurrentMap<String, Lock> userLocks;

Lock lock = userLocks.computeIfAbsent(userId, id -> new ReentrantLock());
```

**Benefits:**
- Fine-grained locking (only locks specific user)
- Allows concurrent operations for different users
- Prevents race conditions for same user

#### **2. Thread-Safe Collections**
```java
ConcurrentMap<String, Subscription> userIdToSubscription;
```

**Benefits:**
- Thread-safe for concurrent reads/writes
- No explicit synchronization needed

#### **3. Scheduled Tasks**
```java
ScheduledExecutorService scheduler;
scheduler.scheduleWithFixedDelay(() -> {
    // Expiry logic
}, delay, interval, TimeUnit.SECONDS);
```

**Benefits:**
- Background processing
- Non-blocking
- Automatic periodic execution

---

## 🎯 Design Decisions

### 1. **Why Store TierBenefits in MembershipPlan?**
**Decision**: Store only specific `TierBenefits`, not entire `BenefitConfig`

**Reasoning:**
- Memory efficiency (1 benefit vs 9)
- Direct access (no key lookup)
- Better encapsulation
- Type safety

### 2. **Why Per-User Locks?**
**Decision**: `ConcurrentMap<String, Lock>` per user

**Reasoning:**
- Fine-grained locking
- Allows concurrency for different users
- Prevents deadlocks
- Scalable

### 3. **Why Strategy Pattern for Tier Evaluation?**
**Decision**: `TierEvaluationStrategy` interface with implementations

**Reasoning:**
- Easy to add new qualification rules
- Testable in isolation
- Open/Closed Principle
- Flexible composition

### 4. **Why Factory Pattern for Plans?**
**Decision**: `PlanFactory` creates all plans

**Reasoning:**
- Centralized creation logic
- Encapsulates complexity
- Easy to modify plan structure
- Single source of truth

### 5. **Why Builder Pattern for Benefits?**
**Decision**: `TierBenefitsBuilder` for benefit construction

**Reasoning:**
- Readable fluent API
- Optional parameters
- Validation at build time
- Immutable result

---

## 📊 State Management

### Subscription States
```
[No Subscription]
    ↓ subscribe()
[ACTIVE]
    ↓ cancel()
[CANCELED]
    
[ACTIVE]
    ↓ expireDueSubscriptions() (endDate < today)
[EXPIRED]
```

### Tier Transitions (within same MembershipType)
```
LOYAL_SILVER → upgrade → LOYAL_GOLD → upgrade → LOYAL_PLATINUM
               ↓ downgrade              ↓ downgrade
```

**Constraint**: Upgrades/downgrades only allowed within same MembershipType

---

## 🔄 Dependency Graph

```
MembershipController
    ├─→ PlanService
    │   └─→ PlanRepository
    │       └─→ PlanFactory
    │           ├─→ BenefitConfigurationFactory
    │           │   ├─→ TierBenefitsBuilder
    │           │   └─→ BenefitConfigBuilder
    │           └─→ PriceCalculator
    │
    └─→ SubscriptionService
        ├─→ SubscriptionRepository
        ├─→ PlanService (for backward compat)
        └─→ TierEvaluationStrategy
            ├─→ OrderCountBasedStrategy
            ├─→ MonthlySpendBasedStrategy
            ├─→ CohortBasedStrategy
            └─→ CompositeTierEvaluationStrategy
                └─→ (uses above strategies)

BenefitService
    └─→ TierConfigRepository

SubscriptionExpiryScheduler
    └─→ SubscriptionService
```

---

## 🚀 Scalability Considerations

### Current (In-Memory)
- **Pros**: Fast, simple
- **Cons**: Not persistent, single JVM
- **Use Case**: Demo, testing

### Production (Distributed)
- **Database**: PostgreSQL/MySQL for persistence
- **Cache**: Redis for plan caching
- **Distributed Locks**: Redis/Zookeeper for multi-instance
- **Message Queue**: Kafka/RabbitMQ for expiry events
- **Read Replicas**: Separate read/write for queries

---

## 🔐 Security Considerations

### Current Implementation
- No authentication/authorization (assumed handled by API gateway)
- Input validation needed (userId format, etc.)

### Production Requirements
- Authentication: JWT/OAuth tokens
- Authorization: Role-based access
- Input validation: Sanitize userId, validate enums
- Audit logging: Track subscription changes
- Rate limiting: Prevent abuse

---

## 📈 Performance Characteristics

### Time Complexity
- **Get Plan**: O(1) - HashMap lookup
- **Subscribe**: O(1) - HashMap put with lock
- **Upgrade Tier**: O(1) - HashMap update with lock + strategy check
- **Expire Subscriptions**: O(n) - Iterate all subscriptions

### Space Complexity
- **Plans**: O(27) = O(1) - Fixed number of plans
- **Subscriptions**: O(n) - n users
- **Locks**: O(n) - One lock per user

### Optimization Opportunities
1. **Expiry**: Index by endDate for O(log n) lookup
2. **Caching**: Cache plans (rarely change)
3. **Batch Operations**: Batch expiry updates

---

## 🧪 Testing Strategy

### Unit Tests
- Builders (test fluent API)
- Factories (test plan creation)
- Strategies (test qualification logic)
- Services (mock repositories)

### Integration Tests
- End-to-end subscription flow
- Concurrent subscription operations
- Tier upgrade scenarios

### Load Tests
- Multiple concurrent subscriptions
- Expiry scheduler under load
- Plan retrieval performance

---

## 📝 Code Organization Principles

1. **Package by Feature**: Related classes grouped together
2. **Layer Separation**: Clear boundaries between layers
3. **Dependency Direction**: Outer layers depend on inner layers
4. **Interface Segregation**: Small, focused interfaces
5. **Single Responsibility**: Each class has one reason to change

---

## 🎓 Learning Points

1. **Design Patterns**: Practical application of Builder, Factory, Strategy, Composite
2. **Concurrency**: Per-user locks, thread-safe collections
3. **Architecture**: Layered architecture with clear separation
4. **Extensibility**: Easy to add new features without breaking existing code
5. **Trade-offs**: Memory vs simplicity, performance vs correctness

