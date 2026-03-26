-- =============================================
-- Analytics (Statistics & Analytics) — indexes
-- =============================================
-- Adds performance indexes for analytics MVP date-range aggregations.
--
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration.
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- ---------------------------------------------
-- sales_header: accelerate status + date filters
-- ---------------------------------------------
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_sh_date_status')
BEGIN
    CREATE INDEX idx_sh_date_status
        ON [dbo].[sales_header] ([sales_date], [status]);

    PRINT 'Created index idx_sh_date_status on sales_header';
END
ELSE
BEGIN
    PRINT 'Index idx_sh_date_status already exists. Skipping.';
END
GO

-- ---------------------------------------------
-- sales_line: accelerate product aggregations by item and sales header join
-- ---------------------------------------------
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_sl_item_header')
BEGIN
    CREATE INDEX idx_sl_item_header
        ON [dbo].[sales_line] ([item_id], [sales_header_id]);

    PRINT 'Created index idx_sl_item_header on sales_line';
END
ELSE
BEGIN
    PRINT 'Index idx_sl_item_header already exists. Skipping.';
END
GO

-- ---------------------------------------------
-- payment: accelerate payment-method breakdown joins
-- ---------------------------------------------
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_pay_method')
BEGIN
    CREATE INDEX idx_pay_method
        ON [dbo].[payment] ([payment_method_id]);

    PRINT 'Created index idx_pay_method on payment';
END
ELSE
BEGIN
    PRINT 'Index idx_pay_method already exists. Skipping.';
END
GO

COMMIT TRANSACTION;
GO

PRINT '=============================================';
PRINT 'Migration 014: analytics indexes completed.';
PRINT '=============================================';

