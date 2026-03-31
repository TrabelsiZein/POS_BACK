-- Migration 022: Add discount source tracking to sales_line and sales_header
-- Allows auditing whether a discount came from a manual entry, SalesPrice, SalesDiscount, or a Promotion.
-- All columns are nullable so existing rows remain valid without any data backfill.

-- ── sales_line ────────────────────────────────────────────────────────────────

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'sales_line' AND COLUMN_NAME = 'discount_source'
)
BEGIN
    ALTER TABLE sales_line ADD discount_source NVARCHAR(20) NULL;
END

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'sales_line' AND COLUMN_NAME = 'promotion_id'
)
BEGIN
    ALTER TABLE sales_line ADD promotion_id BIGINT NULL
        CONSTRAINT FK_sales_line_promotion FOREIGN KEY REFERENCES promotion(id);
END

-- ── sales_header ──────────────────────────────────────────────────────────────

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'sales_header' AND COLUMN_NAME = 'discount_source'
)
BEGIN
    ALTER TABLE sales_header ADD discount_source NVARCHAR(20) NULL;
END

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'sales_header' AND COLUMN_NAME = 'promotion_id'
)
BEGIN
    ALTER TABLE sales_header ADD promotion_id BIGINT NULL
        CONSTRAINT FK_sales_header_promotion FOREIGN KEY REFERENCES promotion(id);
END
