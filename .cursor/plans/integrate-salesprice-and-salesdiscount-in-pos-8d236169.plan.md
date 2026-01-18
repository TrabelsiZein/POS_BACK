<!-- 8d236169-61e2-42ed-a01c-a3738b5eb2bd 2c30b34b-28f5-4b7b-a9b1-ae660050a6c2 -->
# Change to Best Price/Discount Selection Logic

## Overview

Replace priority-based matching with best-value selection:

- **SalesPrice**: Find all valid matches, select **lowest price** (best for customer)
- **SalesDiscount**: Find all valid matches, select **highest discount percentage** (best for customer)
- **No priority**: All matching records are considered equally, best value wins

## Current vs New Logic

### Current (Priority-Based)

1. Check Customer Price Group → if found, return (stop)
2. Check Customer → if found, return (stop)
3. Check All Customers → if found, return (stop)

### New (Best Value)

1. Find ALL matching records (Customer Price Group + Customer + All Customers)
2. Filter by date validity
3. Select best: lowest price OR highest discount

## Implementation Plan

### Phase 1: Update Repository Queries

**File**: `SalesPriceRepository.java`

Add new query method to find all matching SalesPrice records:

```java
@Query("SELECT sp FROM SalesPrice sp WHERE sp.itemNo = :itemNo "
 + "AND ("
 + "  (sp.salesType = :customerPriceGroupType AND sp.salesCode = :customerPriceGroup) OR "
 + "  (sp.salesType = :customerType AND sp.salesCode = :customerCode) OR "
 + "  (sp.salesType = :allCustomersType AND (sp.salesCode = '' OR sp.salesCode IS NULL)) "
 + ") "
 + "AND (sp.startingDate <= :currentDate OR sp.startingDate IS NULL) "
 + "AND (sp.endingDate >= :currentDate OR sp.endingDate IS NULL) "
 + "ORDER BY sp.unitPrice ASC")
List<SalesPrice> findAllMatchingSalesPrices(
    @Param("itemNo") String itemNo,
    @Param("customerPriceGroupType") SalesPriceType customerPriceGroupType,
    @Param("customerPriceGroup") String customerPriceGroup,
    @Param("customerType") SalesPriceType customerType,
    @Param("customerCode") String customerCode,
    @Param("allCustomersType") SalesPriceType allCustomersType,
    @Param("currentDate") LocalDate currentDate);
```

**File**: `SalesDiscountRepository.java`

Add new query methods to find all matching SalesDiscount records:

For ITEM_DISC_GROUP:

```java
@Query("SELECT sd FROM SalesDiscount sd WHERE sd.type = :type AND sd.code = :code "
 + "AND ("
 + "  (sd.salesType = :customerDiscGroupType AND sd.salesCode = :customerDiscGroup) OR "
 + "  (sd.salesType = :customerType AND sd.salesCode = :customerCode) OR "
 + "  (sd.salesType = :allCustomersType AND (sd.salesCode = '' OR sd.salesCode IS NULL)) "
 + ") "
 + "AND (sd.startingDate <= :currentDate OR sd.startingDate IS NULL) "
 + "AND (sd.endingDate >= :currentDate OR sd.endingDate IS NULL) "
 + "ORDER BY sd.lineDiscount DESC")
List<SalesDiscount> findAllMatchingDiscounts(
    @Param("type") SalesDiscountType type,
    @Param("code") String code,
    @Param("customerDiscGroupType") SalesDiscountSalesType customerDiscGroupType,
    @Param("customerDiscGroup") String customerDiscGroup,
    @Param("customerType") SalesDiscountSalesType customerType,
    @Param("customerCode") String customerCode,
    @Param("allCustomersType") SalesDiscountSalesType allCustomersType,
    @Param("currentDate") LocalDate currentDate);
```

### Phase 2: Update PricingService Logic

**File**: `PricingService.java`

#### 2.1 Update `findSalesPrice()` method

**Current**: Sequential priority checks, returns first match

**New**: Single query to find all matches, return lowest price

```java
private SalesPrice findSalesPrice(Item item, Customer customer, String responsibilityCenter) {
    // Build query parameters
    // Execute single query to get all matches
    // Return first result (lowest price due to ORDER BY unitPrice ASC)
}
```

**Logic**:

1. Build query with all three match conditions (OR)
2. Execute single query
3. Return first result (already sorted by price ASC = lowest first)
4. If empty, return null

#### 2.2 Update `findSalesDiscount()` method

**Current**: Priority checks for ITEM_DISC_GROUP then ITEM, then sales type priority

**New**: Find all matches for both types, select highest discount

**Logic**:

1. Try ITEM_DISC_GROUP: Find all matches, get highest discount
2. Try ITEM: Find all matches, get highest discount
3. Return the higher of the two (or the one that exists)

**Alternative simpler approach**:

- Find all matches for both ITEM_DISC_GROUP and ITEM in one query (if possible)
- Or: Two queries, compare results, return best

#### 2.3 Optimize for Minimal Database Access

**Strategy**:

- Use single query with OR conditions (one DB call)
- Database handles filtering and sorting
- Application just takes first result

**Performance**:

- Current: Up to 3 queries (stops early)
- New: 1 query (finds all, DB sorts, returns best)
- Trade-off: Slightly more data transferred, but single query is often faster

### Phase 3: Handle Edge Cases

#### 3.1 Null Customer Handling

- If customer is null: Only check ALL_CUSTOMERS type
- Query should handle null customer gracefully

#### 3.2 Empty Customer Groups

- If customerPriceGroup is null/empty: Skip that condition in query
- If customerDiscGroup is null/empty: Skip that condition in query

#### 3.3 Multiple Records with Same Best Value

- For SalesPrice: If multiple records have same lowest price, return first (or most recent by startingDate)
- For SalesDiscount: If multiple records have same highest discount, return first (or most recent)
- Update ORDER BY to include startingDate as secondary sort

### Phase 4: Update Query ORDER BY

**SalesPrice**:

- Primary: `unitPrice ASC` (lowest first)
- Secondary: `startingDate DESC` (most recent first, for tie-breaking)

**SalesDiscount**:

- Primary: `lineDiscount DESC` (highest first)
- Secondary: `startingDate DESC` (most recent first, for tie-breaking)

## Files to Modify

1. **SalesPriceRepository.java**

            - Add `findAllMatchingSalesPrices()` method
            - Remove or deprecate old priority-based methods (keep for backward compatibility if needed)

2. **SalesDiscountRepository.java**

            - Add `findAllMatchingDiscounts()` method
            - Remove or deprecate old priority-based methods

3. **PricingService.java**

            - Rewrite `findSalesPrice()` to use new query
            - Rewrite `findSalesDiscount()` to use new query
            - Update logic to select best value instead of first match

## Benefits

- **Simpler logic**: No priority levels to maintain
- **Best for customer**: Always gets lowest price / highest discount
- **Minimal DB access**: Single query per lookup (or two for discount types)
- **Database optimization**: DB handles sorting, indexes help
- **Always true**: Matches business requirement of "always choose the best"

## Testing Considerations

- Test with multiple matching records across different types
- Verify lowest price is selected when multiple prices exist
- Verify highest discount is selected when multiple discounts exist
- Test with null customer (should only match ALL_CUSTOMERS)
- Test with customer but no groups (should match CUSTOMER and ALL_CUSTOMERS)
- Performance test: Compare single query vs multiple queries

### To-dos

- [ ] Create enums: SalesPriceType, SalesDiscountType, SalesDiscountSalesType
- [ ] Update sync process: Handle new fields and LocalDate conversion in ErpItemBootstrapService
- [ ] Add ENABLE_SALES_PRICE_GROUP configuration in GeneralSetup with default false
- [ ] Create PricingService with calculateItemPrice, findSalesPrice, and findSalesDiscount methods implementing priority logic
- [ ] Add optimized query methods to SalesPriceRepository and SalesDiscountRepository with date filtering
- [ ] Integrate PricingService into ItemBarcodeService.getItemByBarcode()
- [ ] Integrate PricingService into ItemService item retrieval methods
- [ ] Update SalesHeaderService to use PricingService when creating sales lines