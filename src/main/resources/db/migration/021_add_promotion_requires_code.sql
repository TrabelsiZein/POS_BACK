-- Migration 021: Add requires_code column to promotion table
-- When true, the promotion is NOT auto-applied — cashier must enter the code manually at POS.

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'promotion' AND COLUMN_NAME = 'requires_code'
)
BEGIN
    ALTER TABLE promotion ADD requires_code BIT NOT NULL DEFAULT 0;
END
