# POS System - Entity Relationships Design

Complete documentation of all entity relationships, their types, and design decisions.

---

## 📊 Overview

This POS system uses **JPA/Hibernate** for entity relationships. All relationships are designed to be **simple, efficient, and maintainable**.

---

## 🔗 Relationship Types Used

### 1. **@ManyToOne** (All Relationships)
**Used:** Every relationship in the system  
**Fetch Type:** LAZY (default in JPA)  
**Why:** Performance - only loads related entities when needed

**Benefits:**
- ✅ Avoids N+1 queries
- ✅ Faster initial loading
- ✅ Lower memory usage
- ✅ Better for REST APIs (only load what you need)

---

## 🎯 Relationship Mapping Strategy

### **Unidirectional Only** (From Child to Parent)

**Why unidirectional?**
- ✅ **Simpler code** - no circular dependencies
- ✅ **Better performance** - avoids bidirectional overhead
- ✅ **Clear ownership** - child owns the relationship
- ✅ **Less boilerplate** - no `mappedBy` annotations
- ✅ **Easier to maintain** - no cascading issues
- ✅ **RESTful design** - matches API patterns

---

## 📋 Complete Relationship Map

### **Session Management Hierarchy**

```
UserAccount (Cashier)
    ↓ @ManyToOne (LAZY)
CashierSession
    ↓ @OneToMany (implied)
SalesHeader
    ↓ @OneToMany (implied)
SalesLine
    ↓ @ManyToOne (LAZY)
Item

UserAccount (Responsible)
    ↓ @ManyToOne (LAZY)
CashierSession (verifiedBy)
```

---

## 🔍 Detailed Relationships

### 1. **SalesLine → SalesHeader**

**Relationship:** Many-to-One (Many Lines → One Header)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional (from SalesLine)  
**Foreign Key:** `sales_header_id`  
**Nullable:** NO

```java
// SalesLine.java
@ManyToOne
@JoinColumn(name = "sales_header_id", nullable = false)
private SalesHeader salesHeader;
```

**Why this design?**
- ✅ **Typical parent-child pattern** - lines belong to one header
- ✅ **Bidirectional unnecessary** - rarely query lines from header in bulk
- ✅ **Lazy loading** - don't load all lines when loading header
- ✅ **Clear ownership** - lines own the FK

**Usage:**
```java
// Load line with header
SalesLine line = repository.findById(1L);
SalesHeader header = line.getSalesHeader(); // Lazy loaded

// Don't need: header.getLines() // Not implemented
```

---

### 2. **SalesLine → Item**

**Relationship:** Many-to-One (Many Lines → One Item)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `item_id`  
**Nullable:** NO

```java
// SalesLine.java
@ManyToOne
@JoinColumn(name = "item_id", nullable = false)
private Item item;
```

**Why this design?**
- ✅ **Items are master data** - referenced but not owned by lines
- ✅ **Lazy loading** - don't load item details unless needed
- ✅ **No cascade delete** - deleting a line doesn't delete the item
- ✅ **Performance** - avoid loading heavy item data with every line

---

### 3. **SalesHeader → Customer**

**Relationship:** Many-to-One (Many Sales → One Customer)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `customer_id`  
**Nullable:** YES (walk-in customers)

```java
// SalesHeader.java
@ManyToOne
@JoinColumn(name = "customer_id")
private Customer customer;
```

**Why this design?**
- ✅ **Optional relationship** - some sales to walk-in customers
- ✅ **Lazy loading** - don't load customer info unless needed
- ✅ **No cascade** - customer data independent of sales
- ✅ **Unidirectional** - don't need to list all sales per customer often

---

### 4. **SalesHeader → UserAccount (Creator)**

**Relationship:** Many-to-One (Many Sales → One User)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `created_by_user`  
**Nullable:** YES

```java
// SalesHeader.java
@ManyToOne
@JoinColumn(name = "created_by_user")
private UserAccount createdByUser;
```

**Why this design?**
- ✅ **Audit trail** - track who created each sale
- ✅ **Lazy loading** - user details not needed on every load
- ✅ **No cascade** - users independent of sales
- ✅ **Audit pattern** - common in enterprise apps

---

### 5. **SalesHeader → CashierSession**

**Relationship:** Many-to-One (Many Sales → One Session)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `cashier_session_id`  
**Nullable:** YES

```java
// SalesHeader.java
@ManyToOne
@JoinColumn(name = "cashier_session_id")
private CashierSession cashierSession;
```

**Why this design?**
- ✅ **Session tracking** - group sales by shift
- ✅ **Lazy loading** - don't load session details with every sale
- ✅ **Optional** - some sales may be test/internal
- ✅ **Reporting** - easy to query sales per session

---

### 6. **PaymentHeader → SalesHeader**

**Relationship:** Many-to-One (Many Payments → One Sale)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `sales_header_id`  
**Nullable:** NO

```java
// PaymentHeader.java
@ManyToOne
@JoinColumn(name = "sales_header_id", nullable = false)
private PaymentHeader salesHeader;
```

**Why this design?**
- ✅ **Multi-payment support** - one sale can have multiple payments
- ✅ **Lazy loading** - don't load all payments with sale
- ✅ **Mandatory** - every payment must belong to a sale
- ✅ **Flexible** - cash + cheque in same sale

---

### 7. **PaymentHeader → PaymentMethod**

**Relationship:** Many-to-One (Many Payments → One Method)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `payment_method_id`  
**Nullable:** NO

```java
// PaymentHeader.java
@ManyToOne
@JoinColumn(name = "payment_method_id", nullable = false)
private PaymentMethod paymentMethod;
```

**Why this design?**
- ✅ **Master data reference** - methods are predefined
- ✅ **Lazy loading** - method details not always needed
- ✅ **Mandatory** - every payment must have a method
- ✅ **Type safety** - ensures valid payment methods

---

### 8. **PaymentHeader → UserAccount (Creator)**

**Relationship:** Many-to-One (Many Payments → One User)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `created_by_user`  
**Nullable:** YES

```java
// PaymentHeader.java
@ManyToOne
@JoinColumn(name = "created_by_user")
private UserAccount createdByUser;
```

**Why this design?**
- ✅ **Audit trail** - track who processed payment
- ✅ **Security** - identify users for payment disputes
- ✅ **Lazy loading** - user details not always needed
- ✅ **Consistency** - same pattern as SalesHeader

---

### 9. **CashierSession → UserAccount (Cashier)**

**Relationship:** Many-to-One (Many Sessions → One User)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `cashier_id`  
**Nullable:** NO

```java
// CashierSession.java
@ManyToOne
@JoinColumn(name = "cashier_id", nullable = false)
private CashierSession cashier;
```

**Why this design?**
- ✅ **Mandatory** - every session needs a cashier
- ✅ **Lazy loading** - user details not needed initially
- ✅ **Performance** - faster session listing
- ✅ **Security** - track sessions per user

---

### 10. **CashierSession → UserAccount (Verifier)**

**Relationship:** Many-to-One (Many Sessions → One User)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `verified_by_user`  
**Nullable:** YES

```java
// CashierSession.java
@ManyToOne
@JoinColumn(name = "verified_by_user")
private UserAccount verifiedBy;
```

**Why this design?**
- ✅ **Optional** - only set when verified
- ✅ **Audit trail** - track who verified each session
- ✅ **Role checking** - must be RESPONSIBLE role
- ✅ **Lazy loading** - verifier details not always needed

---

### 11. **CashCountDetail → CashierSession**

**Relationship:** Many-to-One (Many Details → One Session)  
**Annotation:** `@ManyToOne`  
**Fetch Type:** LAZY  
**Direction:** Unidirectional  
**Foreign Key:** `cashier_session_id`  
**Nullable:** NO

```java
// CashCountDetail.java
@ManyToOne
@JoinColumn(name = "cashier_session_id", nullable = false)
private CashCountDetail cashierSession;
```

**Why this design?**
- ✅ **Mandatory** - every detail belongs to a session
- ✅ **Lazy loading** - don't load session with every detail
- ✅ **Grouping** - easy to query all details for a session
- ✅ **Parent-child pattern** - standard design

---

## 🎨 Why Unidirectional Only?

### **Problems with Bidirectional**

```java
// ❌ Bad: Bidirectional
// SalesHeader.java
@OneToMany(mappedBy = "salesHeader", cascade = CascadeType.ALL)
private List<SalesLine> lines;

// SalesLine.java
@ManyToOne
@JoinColumn(name = "sales_header_id")
private SalesHeader salesHeader;
```

**Issues:**
- 🔴 **Cascading** - delete header might delete all lines
- 🔴 **Performance** - loads all lines with header
- 🔴 **Circular references** - JSON serialization issues
- 🔴 **Boilerplate** - must manage both sides
- 🔴 **Complex queries** - harder to optimize

### **Benefits of Unidirectional**

```java
// ✅ Good: Unidirectional
// SalesLine.java
@ManyToOne
@JoinColumn(name = "sales_header_id")
private SalesHeader salesHeader;

// SalesHeader doesn't reference lines
```

**Benefits:**
- ✅ **Simple** - one annotation per relationship
- ✅ **Performance** - explicit loading control
- ✅ **No cascading** - safer deletions
- ✅ **RESTful** - matches API patterns
- ✅ **Clean JSON** - no circular references
- ✅ **Better queries** - explicit fetching

---

## 🚀 Fetch Type Strategy

### **Why LAZY Loading?**

```java
@ManyToOne(fetch = FetchType.LAZY)  // Explicit
// OR
@ManyToOne  // Default is LAZY
```

**Benefits:**
- ✅ **Performance** - only loads when accessed
- ✅ **Memory** - smaller initial objects
- ✅ **Scalability** - better for large datasets
- ✅ **Control** - explicit loading via JPA queries

**Example:**
```java
// Load header without lines
SalesHeader header = repository.findById(1L);  // Fast!

// Only load lines when needed
SalesLine line = lineRepository.findBySalesHeader(header);  // Lazy!
```

---

## 📈 Query Patterns

### **When you need parent from child:**
```java
SalesLine line = lineRepository.findById(1L);
SalesHeader header = line.getSalesHeader(); // LAZY loaded
```

### **When you need children from parent:**
```java
// Don't have bidirectional, so use repository
List<SalesLine> lines = lineRepository.findBySalesHeader(header);

// OR use generic search
lines = lineRepository.findByField("salesHeader.id", "=", header.getId());
```

### **When you need with relations:**
```java
// Use JPA joins in custom queries
@Query("SELECT sl FROM SalesLine sl JOIN FETCH sl.item WHERE sl.salesHeader.id = :headerId")
List<SalesLine> findBySalesHeaderWithItem(@Param("headerId") Long headerId);
```

---

## 🎯 Design Decisions Summary

| Aspect | Choice | Reason |
|--------|--------|--------|
| **Relationship Type** | @ManyToOne | Standard parent-child pattern |
| **Fetch Type** | LAZY | Performance and memory efficiency |
| **Direction** | Unidirectional | Simplicity and REST compliance |
| **Nullable** | Case by case | Business rules determine |
| **Cascade** | None | Explicit control of deletions |
| **Ownership** | Child owns FK | Normal pattern |

---

## 🔥 Key Takeaways

1. ✅ **All relationships are @ManyToOne** - standard parent-child
2. ✅ **All fetch types are LAZY** - performance first
3. ✅ **All relationships are unidirectional** - simpler code
4. ✅ **No cascading** - explicit deletion control
5. ✅ **FK on child** - normal database design
6. ✅ **RESTful compatible** - works with JSON APIs

---

## 📊 Visual Relationship Map

```
┌─────────────────────────────────────────────────────────────────┐
│                      USER ACCOUNT                               │
│                   (Cashier, Responsible)                        │
└─────┬────────────────────────────────────┬──────────────────────┘
      │                                    │
      │ @ManyToOne LAZY                    │ @ManyToOne LAZY
      │                                    │
      ▼                                    ▼
┌─────────────────────┐          ┌──────────────────────┐
│  CASHIER SESSION    │          │   SALES HEADER       │
│  (verifiedBy)       │◄─────────┤  (createdByUser)     │
└─────────────────────┘          └──────────────────────┘
      ▲                                    ▲
      │                                    │
      │ @ManyToOne LAZY                    │ @ManyToOne LAZY
      │                                    │
┌─────────────────────┐          ┌──────────────────────┐
│  CASH COUNT DETAIL  │          │    SALES LINE        │
└─────────────────────┘          └──────────────────────┘
                                         ▲
                                         │
                                         │ @ManyToOne LAZY
                                         │
                            ┌────────────┴────────────┐
                            │                         │
                    ┌───────────────┐         ┌───────────────┐
                    │  CUSTOMER     │         │     ITEM      │
                    └───────────────┘         └───────────────┘

                            PAYMENT HEADER
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
            ┌───────────────┐           ┌───────────────┐
            │ SALES HEADER  │           │ PAYMENT METHOD│
            └───────────────┘           └───────────────┘
```

---

## ✅ Conclusion

**Simple, efficient, maintainable relationships** that:
- Follow JPA best practices
- Optimize for REST APIs
- Avoid circular dependencies
- Use lazy loading for performance
- Keep code DRY and clean

