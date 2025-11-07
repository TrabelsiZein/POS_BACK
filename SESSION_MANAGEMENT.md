# Cashier Session Management Guide

## Overview

The POS system implements a complete cashier session management workflow, similar to traditional cash register systems. This ensures accountability, security, and accurate cash tracking.

---

## Workflow

### 1. Opening Session (Font de Caisse)

**When:** POS user starts his shift (morning or evening)

**What happens:**
- POS user opens a new session
- Declares the starting cash fund (font de caisse)
- Session status: `OPENED`

**API Call:**
```bash
POST /pos/api/cashier-session
{
  "sessionNumber": "SESS-2024-001",
  "cashierId": 2,
  "openingCash": 100.00,
  "status": "OPENED"
}
```

---

### 2. During Shift

**POS user creates sales transactions:**
- Each sale is automatically linked to the cashier session
- All sales during the shift are tracked
- Expected cash = opening cash + total sales

---

### 3. Closing Session (Cloture de Session)

**When:** POS user finishes his shift

**What happens:**
- Count actual cash in register
- Record detailed breakdown
- Calculate difference
- Close session
- Generate Ticket Z (summary report)

**Step 1: Record Cash Count Details**
```bash
# Count banknotes
POST /pos/api/cash-count-detail
{
  "cashierSessionId": 1,
  "denomination": "10 dinar",
  "quantity": 5,
  "unitValue": 10.00,
  "totalAmount": 50.00
}

POST /pos/api/cash-count-detail
{
  "cashierSessionId": 1,
  "denomination": "50 dinar",
  "quantity": 6,
  "unitValue": 50.00,
  "totalAmount": 300.00
}

# Count checks
POST /pos/api/cash-count-detail
{
  "cashierSessionId": 1,
  "denomination": "check",
  "quantity": 2,
  "unitValue": 1000.00,
  "totalAmount": 2000.00
}
```

**Step 2: Close Session**
```bash
PUT /pos/api/cashier-session/1
{
  "actualCash": 207.99,
  "cashCountingDetails": "5 pieces of 10 dinar, 6 pieces of 50 dinar, 2 checks",
  "status": "CLOSED",
  "closedAt": "2024-01-15T18:00:00"
}
```

**Result:**
- Status changes from `OPENED` to `CLOSED`
- If difference found: `CLOSED_WITH_FAULT`

---

### 4. Responsible Verification

**When:** Manager or responsible person verifies

**What happens:**
- Responsible counts cash again
- Compares with POS user's count
- Confirms or notes discrepancies

**API Call:**
```bash
PUT /pos/api/cashier-session/1
{
  "verifiedByUserId": 1,
  "verifiedAt": "2024-01-15T18:30:00",
  "verificationNotes": "Count verified, no discrepancies found"
}
```

---

## Entities

### CashierSession

**Key Fields:**
- `sessionNumber` - Unique session ID
- `cashier` - POS user who opened
- `openedAt` - Opening timestamp
- `closedAt` - Closing timestamp
- `status` - OPENED, CLOSED, CLOSED_WITH_FAULT
- `openingCash` - Starting money
- `expectedCash` - Calculated total
- `actualCash` - Counted cash
- `cashDifference` - Deviation
- `verifiedBy` - Who verified
- `cashCountingDetails` - Text summary

### CashCountDetail

**Key Fields:**
- `cashierSession` - Session reference
- `denomination` - Type (e.g., "10 dinar", "check")
- `quantity` - Number of pieces
- `unitValue` - Value per piece
- `totalAmount` - Total = quantity × unitValue

---

## Use Cases

### Use Case 1: Perfect Day (No Discrepancies)

```
Opening Cash:        100.00 TND
Total Sales:         107.99 TND
Expected Cash:       207.99 TND
Actual Cash:         207.99 TND
Difference:           0.00 TND
Status:              CLOSED
```

### Use Case 2: Discrepancy Found

```
Opening Cash:        100.00 TND
Total Sales:         107.99 TND
Expected Cash:       207.99 TND
Actual Cash:         205.00 TND
Difference:          -2.99 TND (shortage)
Status:              CLOSED_WITH_FAULT
Notes:               Missing 2.99 TND, to be investigated
```

### Use Case 3: Extra Cash

```
Opening Cash:        100.00 TND
Total Sales:         107.99 TND
Expected Cash:       207.99 TND
Actual Cash:         210.00 TND
Difference:          +2.01 TND (surplus)
Status:              CLOSED_WITH_FAULT
Notes:               Extra 2.01 TND found
```

---

## API Endpoints

### Cashier Session
```
GET    /cashier-session           # List all sessions
GET    /cashier-session/{id}      # Get session details
GET    /cashier-session/findByField  # Search
POST   /cashier-session           # Open session
PUT    /cashier-session/{id}      # Update/close session
DELETE /cashier-session/{id}      # Delete session
GET    /cashier-session/{id}/exists
GET    /cashier-session/count
```

### Cash Count Detail
```
GET    /cash-count-detail         # List all details
GET    /cash-count-detail/{id}    # Get detail
GET    /cash-count-detail/findByField  # Search
POST   /cash-count-detail         # Add count entry
PUT    /cash-count-detail/{id}    # Update entry
DELETE /cash-count-detail/{id}    # Delete entry
GET    /cash-count-detail/{id}/exists
GET    /cash-count-detail/count
```

---

## Reports

### Ticket Z - Session Summary

**Generated when closing session**

**Contains:**
- Session number
- Cashier name
- Opening time
- Closing time
- Opening cash
- Total sales count
- Total sales amount
- Expected cash
- Actual cash
- Difference
- Cash breakdown details
- Verification status

**Fields in CashierSession:**
```json
{
  "sessionNumber": "SESS-2024-001",
  "cashier": { "fullName": "John Cashier" },
  "openedAt": "2024-01-15T08:00:00",
  "closedAt": "2024-01-15T18:00:00",
  "status": "CLOSED",
  "openingCash": 100.00,
  "expectedCash": 207.99,
  "actualCash": 207.99,
  "cashDifference": 0.00,
  "cashCountingDetails": "5 pieces of 10 dinar, 6 pieces of 50 dinar, 2 checks",
  "verifiedBy": { "fullName": "Manager" },
  "verifiedAt": "2024-01-15T18:30:00"
}
```

---

## Business Rules

1. **One Active Session Per Cashier**
   - A cashier can only have one OPENED session at a time
   - Must close current session before opening new one

2. **Sales Linked to Session**
   - All sales during shift must reference the session
   - Cannot create sale without active session

3. **Verification Required**
   - Only RESPONSIBLE role can verify sessions
   - Verification must happen after closure

4. **Audit Trail**
   - All actions logged with timestamps
   - Track who opened, who closed, who verified

---

## Security

- Only POS_USER can open/close their own sessions
- Only RESPONSIBLE can verify sessions
- Only ADMIN can view all sessions
- All changes are audited

---

## Future Enhancements

- Automatic expected cash calculation from sales
- Real-time session balance tracking
- Integration with sales reports
- Email/notification on faults
- Printable Ticket Z format
- Multiple cash register support
- Shift transfer between cashiers

