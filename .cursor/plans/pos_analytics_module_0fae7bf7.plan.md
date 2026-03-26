---
name: POS Analytics MVP
overview: A strict 3–5 day MVP for the Statistics & Analytics module. 5 KPIs, 3 charts, 4 backend endpoints, one frontend page. No new tables, no snapshots, no AI, no exports.
todos:
  - id: db-indexes
    content: "Add 3 SQL Server indexes via a Flyway migration: sales_header(sales_date, status), sales_line(item_id, sales_header_id), payment(payment_method_id)"
    status: completed
  - id: backend-dtos
    content: "Create DTOs: AnalyticsSummaryDTO, SalesTrendPointDTO, TopProductDTO, PaymentBreakdownDTO"
    status: completed
  - id: backend-repo
    content: Create AnalyticsRepository with 4 native SQL Server queries + getReturnsSummary; fixed NonUniqueAlias + ClassCastException
    status: completed
  - id: backend-service
    content: Create AnalyticsService wrapping repository calls with date validation, delta computation, null safety
    status: completed
  - id: backend-controller
    content: Create AnalyticsAPI controller under /admin/analytics/* with ADMIN+RESPONSIBLE manual role check
    status: completed
  - id: frontend-route
    content: Add Statistics.vue route (/admin/statistics), menu entry (BarChart2Icon), Login.vue abilities for ADMIN+RESPONSIBLE
    status: completed
  - id: frontend-page
    content: "Build Statistics.vue: period selector, 5 KPI cards with delta arrows, sales trend line chart, top 5 products bar chart, payment methods donut chart. Fixed chart-wrapper height (Chart.js responsive sizing bug)."
    status: completed
  - id: frontend-i18n
    content: Add admin.statistics.* keys to en.json, fr.json, ar.json
    status: completed
isProject: false
---

# POS Analytics — Strict MVP

---

## 1. MVP KPIs (exactly 5)

These 5 KPIs fit on a single card row and answer the most critical business question: *"How is the store doing right now?"*

| # | KPI | Label | Formula | Data Source |

|---|-----|-------|---------|-------------|

| 1 | **Revenue (CA)** | Chiffre d'Affaires | `SUM(total_amount) WHERE status='COMPLETED'` | `sales_header` |

| 2 | **Transaction Count** | Nombre de Ventes | `COUNT(*) WHERE status='COMPLETED'` | `sales_header` |

| 3 | **Average Basket** | Panier Moyen | `CA / Transaction Count` | Derived |

| 4 | **Total Returns** | Montant Retours | `SUM(total_return_amount)` | `return_header` |

| 5 | **Net Revenue** | CA Net | `CA - Total Returns` | Derived |

Each KPI card shows the **current period value** + a **delta arrow** (▲/▼ vs previous equivalent period).

Delta calculation: if period = "Today", compare vs yesterday same metric. If period = "Last 7 days", compare vs previous 7 days. Keep it simple — compute in Java, not SQL.

---

## 2. MVP Endpoints (exactly 4)

Base path: `/admin/analytics` — security: `hasAnyRole('ADMIN', 'RESPONSIBLE')`

### `GET /admin/analytics/summary`

Returns all 5 KPIs for the selected period + their delta vs previous period.

Query params: `from` (ISO date), `to` (ISO date)

Response:

```json
{
  "revenue": 12450.500,
  "revenueDelta": 8.3,
  "transactionCount": 87,
  "transactionCountDelta": -2.1,
  "avgBasket": 143.110,
  "avgBasketDelta": 10.6,
  "totalReturns": 320.000,
  "totalReturnsDelta": -5.0,
  "netRevenue": 12130.500,
  "netRevenueDelta": 9.1
}
```

### `GET /admin/analytics/sales/trend`

Returns daily revenue buckets for the line chart.

Query params: `from`, `to`

Response:

```json
[
  { "date": "2026-03-01", "revenue": 1240.500, "transactionCount": 12 },
  { "date": "2026-03-02", "revenue": 980.000, "transactionCount": 9 }
]
```

### `GET /admin/analytics/products/top`

Returns top 5 products by revenue in the period.

Query params: `from`, `to`, `limit` (default 5, max 10)

Response:

```json
[
  { "itemCode": "ART001", "itemName": "Produit A", "familyName": "Famille X", "quantitySold": 34, "revenue": 2890.000 },
  ...
]
```

### `GET /admin/analytics/payments/breakdown`

Returns revenue split by payment method.

Query params: `from`, `to`

Response:

```json
[
  { "methodCode": "CLIENT_ESPECES", "methodName": "Espèces", "totalAmount": 7200.000, "percentage": 57.8 },
  { "methodCode": "CLIENT_TPE",    "methodName": "TPE",     "totalAmount": 4530.000, "percentage": 36.4 },
  ...
]
```

---

## 3. MVP Database Needs

**No new tables.** Queries run directly on existing tables.

One new Flyway migration file (`014_add_analytics_indexes.sql`):

```sql
-- Speeds up date-range + status filtering on sales_header
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_sh_date_status')
    CREATE INDEX idx_sh_date_status ON sales_header(sales_date, status);

-- Speeds up product aggregation (GROUP BY item_id)
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_sl_item_header')
    CREATE INDEX idx_sl_item_header ON sales_line(item_id, sales_header_id);

-- Speeds up payment method breakdown
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_pay_method')
    CREATE INDEX idx_pay_method ON payment(payment_method_id);
```

These are safe to add with zero downtime (SQL Server online index build). No existing queries are changed.

---

## 4. MVP Frontend

**One new page**: [`src/views/admin/Statistics.vue`](../../../Apps/POS_Front/src/views/admin/Statistics.vue)

### Layout

```
┌─────────────────────────────────────────────────────────────────┐
│ Page Header: "Statistiques" + Period Selector                   │
│  [Today] [Last 7 Days] [Last 30 Days] [This Month]  ← presets  │
├───────────┬───────────┬───────────┬───────────┬─────────────────┤
│  CA TTC   │ Ventes    │  Panier   │  Retours  │   CA Net        │
│ 12,450 TND│    87     │  143 TND  │  320 TND  │  12,130 TND     │
│  ▲ 8.3%  │  ▼ 2.1%  │  ▲ 10.6% │  ▼ 5.0%  │   ▲ 9.1%       │
├───────────────────────┬─────────────────────────────────────────┤
│  Sales Trend          │  Top 5 Products                         │
│  (line chart)         │  (horizontal bar chart)                 │
│  Revenue over days    │  Product name → revenue bar             │
├───────────────────────┴─────────────────────────────────────────┤
│  Payment Methods (donut chart, centered, with legend)           │
└─────────────────────────────────────────────────────────────────┘
```

### Period Selector behavior

- 4 preset buttons (Today, Last 7 Days, Last 30 Days, This Month)
- Selecting any preset fires all 4 API calls in parallel
- Loading spinners per card/chart while fetching
- Default on page load: **Last 30 Days**

### Charts library

Use **[vue-chartjs](https://vue-chartjs.org/)** (Chart.js wrapper) — already available or easy to add via `npm install vue-chartjs chart.js`. No heavy dependencies.

- Line chart: `revenue` per day, X=date, Y=TND amount
- Horizontal bar chart: `itemName` vs `revenue`, sorted descending
- Donut chart: payment methods with color-coded slices

### i18n keys to add (under `admin.statistics.*`)

```json
{
  "admin": {
    "statisticsMenu": "Statistiques",
    "statistics": {
      "title": "Tableau de Bord Analytics",
      "subtitle": "Indicateurs clés de performance",
      "periods": {
        "today": "Aujourd'hui",
        "last7days": "7 Derniers Jours",
        "last30days": "30 Derniers Jours",
        "thisMonth": "Ce Mois"
      },
      "kpis": {
        "revenue": "Chiffre d'Affaires",
        "transactions": "Nombre de Ventes",
        "avgBasket": "Panier Moyen",
        "returns": "Montant Retours",
        "netRevenue": "CA Net"
      },
      "charts": {
        "salesTrend": "Évolution des Ventes",
        "topProducts": "Top 5 Produits",
        "paymentMethods": "Répartition des Paiements"
      }
    }
  }
}
```

---

## 5. Step-by-Step Implementation Plan

Ordered tasks, one developer, 3–5 days total.

### Day 1 — Backend Foundation (~6h)

**Task 1: Flyway migration** (30 min)

- Create `src/main/resources/db/migration/014_add_analytics_indexes.sql`
- Add the 3 indexes with SQL Server `IF NOT EXISTS` guards

**Task 2: DTOs** (45 min)

- `AnalyticsSummaryDTO` — 5 KPI fields + 5 delta fields (all `Double`)
- `SalesTrendPointDTO` — `date` (String), `revenue` (Double), `transactionCount` (int)
- `TopProductDTO` — `itemCode`, `itemName`, `familyName`, `quantitySold`, `revenue`
- `PaymentBreakdownDTO` — `methodCode`, `methodName`, `totalAmount`, `percentage`

**Task 3: AnalyticsRepository** (2.5h)

- `@Repository` class using `EntityManager` or `@PersistenceContext` with `createNativeQuery`
- 4 methods, each returning `List<Object[]>` mapped to DTOs in the service:
  - `getSalesSummary(LocalDate from, LocalDate to)` → `[revenue, count]`
  - `getReturnsSummary(LocalDate from, LocalDate to)` → `[returns]`
  - `getSalesTrend(LocalDate from, LocalDate to)` → `[date, revenue, count]` per day
  - `getTopProducts(LocalDate from, LocalDate to, int limit)` → `[itemCode, name, family, qty, revenue]`
  - `getPaymentBreakdown(LocalDate from, LocalDate to)` → `[methodCode, methodName, amount]`

**Task 4: AnalyticsService** (1.5h)

- Calls repository methods
- Computes derived fields (`avgBasket`, `netRevenue`, `percentage`)
- Computes delta % by calling the same queries for the "previous" period
- Max range guard: reject if `to - from > 366 days`

**Task 5: AnalyticsAPI** (45 min)

- `@RestController @RequestMapping("admin/analytics")`
- `@PreAuthorize("hasAnyRole('ADMIN', 'RESPONSIBLE')")`
- 4 `@GetMapping` methods — thin, no logic, just call service and return DTO

---

### Day 2 — Backend Queries + Wiring (~5h)

**Task 6: Write and test the 4 SQL queries** (3h)

- Test directly in SQL Server Management Studio first, then move to Java
- Summary query:
```sql
SELECT SUM(sh.total_amount), COUNT(sh.id)
FROM sales_header sh
WHERE sh.status = 'COMPLETED'
  AND CAST(sh.sales_date AS DATE) BETWEEN :from AND :to
```

- Trend query:
```sql
SELECT CAST(sh.sales_date AS DATE) as day,
       SUM(sh.total_amount) as revenue,
       COUNT(sh.id) as cnt
FROM sales_header sh
WHERE sh.status = 'COMPLETED'
  AND CAST(sh.sales_date AS DATE) BETWEEN :from AND :to
GROUP BY CAST(sh.sales_date AS DATE)
ORDER BY day ASC
```

- Top products query:
```sql
SELECT TOP(:limit)
       i.item_code, i.name, f.name,
       SUM(sl.quantity) as qty,
       SUM(sl.line_total_including_vat) as revenue
FROM sales_line sl
JOIN item i ON i.id = sl.item_id
LEFT JOIN item_family f ON f.id = i.family_id
JOIN sales_header sh ON sh.id = sl.sales_header_id
WHERE sh.status = 'COMPLETED'
  AND CAST(sh.sales_date AS DATE) BETWEEN :from AND :to
GROUP BY i.item_code, i.name, f.name
ORDER BY revenue DESC
```

- Payment breakdown query:
```sql
SELECT pm.code, pm.name, SUM(p.total_amount) as total
FROM payment p
JOIN payment_method pm ON pm.id = p.payment_method_id
JOIN sales_header sh ON sh.id = p.sales_header_id
WHERE sh.status = 'COMPLETED'
  AND CAST(sh.sales_date AS DATE) BETWEEN :from AND :to
GROUP BY pm.code, pm.name
ORDER BY total DESC
```


**Task 7: SecurityConfig + role wiring** (30 min)

- Add `/admin/analytics/**` to the JWT-secured block (it likely already inherits this)
- Confirm `RESPONSIBLE` role can access analytics but not other admin-only endpoints

**Task 8: Smoke test all 4 endpoints with Postman/curl** (1.5h)

- Verify correct totals, correct date filtering, null safety when no data

---

### Day 3 — Frontend Skeleton (~6h)

**Task 9: Install chart library** (15 min)

```bash
npm install vue-chartjs chart.js
```

**Task 10: Add route, menu entry, abilities** (45 min)

- [`src/router/index.js`](../../../Apps/POS_Front/src/router/index.js): add `/admin/statistics` route, `requiredRole: 'ADMIN'` (RESPONSIBLE can access too — adjust guard)
- [`src/navigation/vertical/index.js`](../../../Apps/POS_Front/src/navigation/vertical/index.js): add entry with `BarChart2Icon` under Sales or Administration group
- [`src/views/Login.vue`](../../../Apps/POS_Front/src/views/Login.vue): add `admin-statistics` read ability to ADMIN + RESPONSIBLE roles

**Task 11: Statistics.vue skeleton + KPI cards** (3h)

- Period selector: 4 preset `<b-button>` buttons, active state
- Compute `from`/`to` from selected preset
- On mount + on preset change: call `GET /admin/analytics/summary`
- 5 KPI cards using `<b-card>`: big number + delta badge (green ▲ / red ▼)
- Loading state with `<b-spinner>` while fetching
- Error state with toast notification on failure

**Task 12: Sales Trend chart** (1h)

- `<LineChart>` from vue-chartjs
- X labels = dates from trend response, Y = revenue
- Call `GET /admin/analytics/sales/trend` on period change
- Clean tooltip: "1,240.500 TND — 12 ventes"

---

### Day 4 — Frontend Charts + i18n (~5h)

**Task 13: Top Products chart** (1.5h)

- `<HorizontalBarChart>` (or `<BarChart>` with `indexAxis: 'y'`)
- X = revenue TND, Y = product name
- Call `GET /admin/analytics/products/top`
- Color: single solid color, no gradient complexity

**Task 14: Payment Methods donut chart** (1.5h)

- `<DoughnutChart>` from vue-chartjs
- Call `GET /admin/analytics/payments/breakdown`
- Color map per method code (Espèces = green, TPE = blue, Chèque = orange, etc.)
- Legend below chart with % values

**Task 15: i18n keys** (1h)

- Add `admin.statistics.*` keys to `en.json`, `fr.json`, `ar.json`
- All 3 languages: KPI labels, period buttons, chart titles, loading/error messages

**Task 16: Layout polish** (1h)

- Responsive: cards wrap on smaller screens
- Match existing admin page style (same `<b-card>` + header pattern as `TicketsHistory.vue`)
- Consistent TND formatting (3 decimal places, e.g. `1,240.500 TND`)

---

### Day 5 — QA + Cleanup (~4h)

**Task 17: End-to-end test** (2h)

- Test all 4 period presets
- Test with no data (empty state: "Aucune donnée pour cette période")
- Test with large date ranges
- Check both ADMIN and RESPONSIBLE roles see the page
- Check POS_USER does NOT see the menu entry

**Task 18: Code cleanup** (1h)

- Remove any console.log
- Check for null pointer exceptions in backend (empty result sets)
- Verify SQL Server compatibility of all queries (no MySQL-specific functions)

**Task 19: Update AI_CONTEXT_POS.md** (30 min)

- Document the new analytics module, endpoints, and page

---

## What is explicitly NOT in this MVP

These are deferred to Phase 2 with no code scaffolding needed now:

- Heatmap (hour × day)
- Cashier performance chart
- Customer analytics
- Stock alerts table
- Period comparison overlay
- Reports page with filters and export
- Aggregation/snapshot tables and scheduler
- Loyalty analytics
- Franchise multi-location filter
- AI forecasting

The backend is designed so all of these can be added as new endpoints on the same `AnalyticsAPI` controller without touching existing code.