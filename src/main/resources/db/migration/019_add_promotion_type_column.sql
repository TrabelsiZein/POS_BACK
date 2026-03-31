-- Migration 019: Add promotion_type column to promotion table
-- Adds the high-level PromotionType enum column and back-fills existing rows.

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'promotion' AND COLUMN_NAME = 'promotion_type'
)
BEGIN
    -- Add the column as nullable first so existing rows are not rejected
    ALTER TABLE promotion
        ADD promotion_type NVARCHAR(30) NULL;

    -- Back-fill existing rows based on the current field combinations:
    --   FREE_QUANTITY benefit  → QUANTITY_PROMOTION
    --   CART scope             → CART_DISCOUNT
    --   Everything else        → SIMPLE_DISCOUNT (safest default for legacy data)
    UPDATE promotion
    SET promotion_type = CASE
        WHEN benefit_type = 'FREE_QUANTITY'  THEN 'QUANTITY_PROMOTION'
        WHEN scope        = 'CART'           THEN 'CART_DISCOUNT'
        ELSE                                      'SIMPLE_DISCOUNT'
    END
    WHERE promotion_type IS NULL;

    -- Now enforce NOT NULL
    ALTER TABLE promotion
        ALTER COLUMN promotion_type NVARCHAR(30) NOT NULL;

    -- Index for fast filtering by promotion type in the POS engine
    IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_promotion_type' AND object_id = OBJECT_ID('promotion'))
    BEGIN
        CREATE INDEX idx_promotion_type ON promotion (promotion_type);
    END
END
