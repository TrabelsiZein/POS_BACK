# Analytics Module — Calculations Guide

> **Purpose**: Explains exactly how every value shown on the Statistics page is calculated,
> where the data comes from, and how the ▲/▼ percentage is determined.
>
> **Audience**: Developer or business analyst who needs to understand, debug, or extend the module.

---

## Table of Contents

1. [Period Selection — How dates are computed](#1-period-selection--how-dates-are-computed)
2. [The "Previous Period" — How ▲/▼ delta is computed](#2-the-previous-period--how--delta-is-computed)
3. [KPI 1 — Revenue (CA TTC)](#3-kpi-1--revenue-ca-ttc)
4. [KPI 2 — Transaction Count (Nombre de Ventes)](#4-kpi-2--transaction-count-nombre-de-ventes)
5. [KPI 3 — Average Basket (Panier Moyen)](#5-kpi-3--average-basket-panier-moyen)
6. [KPI 4 — Total Returns (Montant Retours)](#6-kpi-4--total-returns-montant-retours)
7. [KPI 5 — Net Revenue (CA Net)](#7-kpi-5--net-revenue-ca-net)
8. [Chart 1 — Sales Trend (Évolution des Ventes)](#8-chart-1--sales-trend-évolution-des-ventes)
9. [Chart 2 — Top 5 Products](#9-chart-2--top-5-products)
10. [Chart 3 — Payment Breakdown (Répartition des Paiements)](#10-chart-3--payment-breakdown-répartition-des-paiements)
11. [Edge Cases & Zero-Safety Rules](#11-edge-cases--zero-safety-rules)
12. [Full Data Flow Diagram](#12-full-data-flow-diagram)

---

## 1. Period Selection — How dates are computed

When you click a period preset button, the frontend computes `from` and `to` based on today's date.

| Button | `from` | `to` |
|---|---|---|
| **Today** | today | today |
| **Last 7 Days** | today − 6 days | today |
| **Last 30 Days** | today − 29 days | today |
| **This Month** | 1st day of current month | today |

**Example** — if today is `2026-03-25`:

| Button | from | to | Days in period |
|---|---|---|---|
| Today | 2026-03-25 | 2026-03-25 | 1 |
| Last 7 Days | 2026-03-19 | 2026-03-25 | 7 |
| Last 30 Days | 2026-02-24 | 2026-03-25 | 30 |
| This Month | 2026-03-01 | 2026-03-25 | 25 |

Both dates are **inclusive** (`BETWEEN :from AND :to` in SQL).

---

## 2. The "Previous Period" — How ▲/▼ delta is computed

### Concept

Every KPI is compared to the **immediately preceding period of the same duration**.
This ensures a fair comparison (30 days vs 30 days, not 30 days vs 31 days).

### Formula

```
duration  = (to - from) + 1 days
prevTo    = from - 1 day
prevFrom  = from - duration days
```

**Example** — Last 30 Days (from=2026-02-24, to=2026-03-25):

```
duration  = 30 days
prevTo    = 2026-02-23
prevFrom  = 2026-02-24 − 30 days = 2026-01-25
Previous period = [2026-01-25 .. 2026-02-23]  (also 30 days)
```

**Example** — Today (from=2026-03-25, to=2026-03-25):

```
duration  = 1 day
prevTo    = 2026-03-24
prevFrom  = 2026-03-24
Previous period = [2026-03-24 .. 2026-03-24]  = yesterday
```

### Delta % formula

```
delta % = ((current - previous) / previous) × 100
```

Rounded to **2 decimal places** using `HALF_UP` rounding.

**Examples:**

| current | previous | delta % | Badge |
|---|---|---|---|
| 12,450 | 11,500 | +8.26% | ▲ green |
| 8,300 | 9,000 | −7.78% | ▼ red |
| 500 | 0 | 0.00% | ▲ green (special case) |
| 0 | 0 | 0.00% | ▲ green (special case) |

**Special case — previous = 0**: delta is returned as `0.00%` (not infinity).
This avoids division-by-zero. The badge shows ▲ green because `0 >= 0`.

---

## 3. KPI 1 — Revenue (CA TTC)

### What it shows
Total revenue of all **completed** sales in the selected period.

### SQL source table
`sales_header`

### SQL query
```sql
SELECT SUM(sh.total_amount) AS revenue,
       COUNT(sh.id)         AS transaction_count
FROM   sales_header sh
WHERE  sh.status = 'COMPLETED'
  AND  CAST(sh.sales_date AS DATE) BETWEEN :from AND :to
```

### Business rules
- Only `status = 'COMPLETED'` sales are counted. Cancelled, void, or in-progress sales are excluded.
- `total_amount` is the **TTC (tax-included)** total of the sales ticket.
- Result is **3 decimal places** (e.g. `5 348.600 TND`).

### Delta
Compared against the same query run on the previous period.

```
revenueDelta = ((revenueNow - revenuePrev) / revenuePrev) × 100
```

---

## 4. KPI 2 — Transaction Count (Nombre de Ventes)

### What it shows
Number of completed sales tickets in the period.

### SQL source table
`sales_header`

### SQL query
Same query as Revenue — the `COUNT(sh.id)` column from the same result row:
```sql
SELECT SUM(sh.total_amount) AS revenue,
       COUNT(sh.id)         AS transaction_count
FROM   sales_header sh
WHERE  sh.status = 'COMPLETED'
  AND  CAST(sh.sales_date AS DATE) BETWEEN :from AND :to
```

### Business rules
- Each row in `sales_header` with `status='COMPLETED'` counts as 1 transaction.
- Result is a whole integer (e.g. `3`).

### Delta
```
transactionCountDelta = ((countNow - countPrev) / countPrev) × 100
```

---

## 5. KPI 3 — Average Basket (Panier Moyen)

### What it shows
Average revenue per transaction — how much a customer spends per visit on average.

### No direct SQL query
This is a **derived KPI**, computed in Java from KPI 1 and KPI 2:

```
avgBasket = revenue / transactionCount
```

### Java implementation
```java
if (transactionCount <= 0) return 0.000;
return revenue.divide(BigDecimal(transactionCount), 3, HALF_UP);
```

### Business rules
- If `transactionCount = 0`, result is `0.000` (no division by zero).
- Result is **3 decimal places** (e.g. `1 782.867 TND`).

### Delta
Computed from previous period's `avgBasket`:
```
avgBasketPrev = revenuePrev / transactionCountPrev
avgBasketDelta = ((avgBasketNow - avgBasketPrev) / avgBasketPrev) × 100
```

---

## 6. KPI 4 — Total Returns (Montant Retours)

### What it shows
Total value of goods returned by customers in the period.

### SQL source table
`return_header`

### SQL query
```sql
SELECT SUM(rh.total_return_amount) AS total_returns
FROM   return_header rh
WHERE  rh.status = 'COMPLETED'
  AND  CAST(rh.return_date AS DATE) BETWEEN :from AND :to
```

### Business rules
- Only `status = 'COMPLETED'` returns are counted.
- `total_return_amount` is the full refunded amount TTC.
- If there are **no returns** in the period, the SQL `SUM` returns `NULL`, which is safely converted to `0.000`.
- Result is **3 decimal places** (e.g. `0.000 TND`).

### Delta
```
totalReturnsDelta = ((returnsNow - returnsPrev) / returnsPrev) × 100
```

> **Note**: A ▼ (down) delta on Returns is **good news** — it means fewer refunds.
> A ▲ (up) delta on Returns means more money was refunded to customers.
> The badge color is purely mathematical (▲ = green, ▼ = red) — interpret in business context.

---

## 7. KPI 5 — Net Revenue (CA Net)

### What it shows
Actual revenue after deducting all returns. This is the "real" money the store kept.

### No direct SQL query
Derived KPI, computed in Java:

```
netRevenue = revenue − totalReturns
```

### Java implementation
```java
netRevenue = revenueNow.subtract(totalReturnsNow);
// then normalized to 3 decimal places
```

### Business rules
- If returns exceed revenue (edge case), result can be negative.
- Result is **3 decimal places**.

### Delta
```
netRevenueDelta = ((netRevenueNow - netRevenuePrev) / netRevenuePrev) × 100
```

---

## 8. Chart 1 — Sales Trend (Évolution des Ventes)

### What it shows
A **line chart** of daily revenue over the selected period. X-axis = date, Y-axis = TND.

### SQL source table
`sales_header`

### SQL query
```sql
SELECT CAST(sh.sales_date AS DATE) AS day,
       SUM(sh.total_amount)        AS revenue,
       COUNT(sh.id)                AS transaction_count
FROM   sales_header sh
WHERE  sh.status = 'COMPLETED'
  AND  CAST(sh.sales_date AS DATE) BETWEEN :from AND :to
GROUP  BY CAST(sh.sales_date AS DATE)
ORDER  BY day ASC
```

### Business rules
- One data point per **calendar day** that had at least one completed sale.
- Days with **zero sales are not shown** (no row is generated for them — they are gaps in the line).
- Result is sorted ascending by date so the line reads left-to-right chronologically.
- Each point tooltip shows: `1 240.500 TND`.

### No delta
The trend chart has no delta badge — it is visual/exploratory.

---

## 9. Chart 2 — Top 5 Products

### What it shows
A **horizontal bar chart** of the 5 products that generated the most revenue in the period.

### SQL source tables
`sales_line` + `item` + `item_family` + `sales_header`

### SQL query
```sql
SELECT TOP (5)
       i.item_code         AS item_code,
       i.name              AS item_name,
       f.name              AS family_name,
       SUM(sl.quantity)    AS quantity_sold,
       SUM(sl.line_total_including_vat) AS revenue
FROM   sales_line sl
JOIN   item i           ON i.id = sl.item_id
JOIN   item_family f    ON f.id = i.item_family_id
JOIN   sales_header sh  ON sh.id = sl.sales_header_id
WHERE  sh.status = 'COMPLETED'
  AND  CAST(sh.sales_date AS DATE) BETWEEN :from AND :to
GROUP  BY i.item_code, i.name, f.name
ORDER  BY revenue DESC
```

### Business rules
- Revenue per product = `SUM(line_total_including_vat)` — this is the **TTC line total** of every sold line.
- `quantity_sold` = total units sold across all transactions in the period.
- Ranked by **revenue descending**, so the highest-earning product is at the top.
- `TOP (5)` is SQL Server syntax — returns at most 5 rows.
- The bar chart X-axis = revenue TND, Y-axis = product name.

### No delta
No delta for products — this is a ranking chart.

---

## 10. Chart 3 — Payment Breakdown (Répartition des Paiements)

### What it shows
A **doughnut chart** showing what percentage of total payments came from each payment method (cash, card, cheque, etc.).

### SQL source tables
`payment` + `payment_method` + `sales_header`

### SQL query
```sql
SELECT pm.code            AS method_code,
       pm.name            AS method_name,
       SUM(p.total_amount) AS total_amount
FROM   payment p
JOIN   payment_method pm ON pm.id = p.payment_method_id
JOIN   sales_header sh   ON sh.id = p.sales_header_id
WHERE  sh.status = 'COMPLETED'
  AND  CAST(sh.sales_date AS DATE) BETWEEN :from AND :to
GROUP  BY pm.code, pm.name
ORDER  BY total_amount DESC
```

### Percentage calculation
The SQL returns the **raw amount** per method. The **percentage is computed in Java**:

```
total_all_methods = SUM of all payment rows' totalAmount
percentage of method X = (method_X_amount / total_all_methods) × 100
```

Rounded to **2 decimal places** using `HALF_UP`.

**Example:**

| Method | Amount | % |
|---|---|---|
| Espèces (Cash) | 7 200.000 | 57.80% |
| TPE (Card) | 4 530.000 | 36.37% |
| Chèque | 750.000 | 6.02% |
| Avoir | 20.000 | 0.16% |
| **TOTAL** | **12 450.000** | **~99.35%** ← rounding artefact |

> **Note on rounding**: percentages are each rounded independently to 2dp,
> so the total may be 99.99% or 100.01% instead of exactly 100%.
> This is expected behaviour — it does not affect the chart rendering.

### No delta
No delta for payment breakdown — this is a distribution chart.

---

## 11. Edge Cases & Zero-Safety Rules

| Situation | Behaviour |
|---|---|
| No sales in selected period | Revenue = 0.000, TransactionCount = 0, all deltas = 0.00% |
| No sales in previous period | All deltas = 0.00% (previous = 0 → special case, no division by zero) |
| No returns ever | Total Returns = 0.000 (SQL SUM returns NULL → converted to 0) |
| Single sale in period | Avg Basket = that sale's total amount |
| `from` date = `to` date (Today) | Previous period = yesterday only |
| Period > 366 days | Rejected by backend: HTTP 400 "Period cannot exceed 366 days" |
| `from` > `to` | Rejected by backend: HTTP 400 "from must be <= to" |
| Unauthorized role | Rejected by backend: HTTP 403 |

---

## 12. Full Data Flow Diagram

```
User clicks [Last 30 Days]
        │
        ▼
Frontend computes:
  from = today − 29 days
  to   = today
        │
        ▼ 4 parallel API calls
┌───────────────────────────────────────────────────────────┐
│  GET /admin/analytics/summary?from=...&to=...             │
│  GET /admin/analytics/sales/trend?from=...&to=...         │
│  GET /admin/analytics/products/top?from=...&to=...&limit=5│
│  GET /admin/analytics/payments/breakdown?from=...&to=...  │
└───────────────────────────────────────────────────────────┘
        │
        ▼ (for /summary only)
AnalyticsService computes previous period:
  prevFrom = from − 30 days
  prevTo   = from − 1 day
        │
        ▼ 4 SQL queries run in parallel (current + previous for summary)
┌─────────────────────────────────────────────────────────────┐
│  getSalesSummary(from, to)      → revenue, txCount (current)│
│  getReturnsSummary(from, to)    → returns (current)         │
│  getSalesSummary(prevFrom,prevTo) → revenue, txCount (prev) │
│  getReturnsSummary(prevFrom,prevTo) → returns (prev)        │
└─────────────────────────────────────────────────────────────┘
        │
        ▼ Java service derives
  avgBasket     = revenue / txCount
  netRevenue    = revenue − totalReturns
  revenueDelta  = ((now − prev) / prev) × 100
  ... (same for all 5 KPIs)
        │
        ▼
JSON response → Frontend renders KPI cards + charts
```

---

## Quick Reference — What tables feed what

| KPI / Chart | Primary Table | Key Column |
|---|---|---|
| Revenue, Tx Count | `sales_header` | `total_amount`, `status`, `sales_date` |
| Avg Basket | Derived | — |
| Total Returns | `return_header` | `total_return_amount`, `status`, `return_date` |
| Net Revenue | Derived | — |
| Sales Trend | `sales_header` | `total_amount`, `sales_date` |
| Top Products | `sales_line` + `item` + `item_family` | `line_total_including_vat`, `quantity` |
| Payment Breakdown | `payment` + `payment_method` | `total_amount`, `payment_method_id` |

---

*Last updated: 2026-03-25 — Analytics MVP v1.0*
