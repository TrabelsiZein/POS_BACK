-- =============================================
-- Add Cash Discrepancy Check Config Migration
-- =============================================
-- This migration adds:
-- 1. ENABLE_CASH_DISCREPANCY_CHECK config to GeneralSetup
-- 
-- This config controls whether to validate that the closing amount
-- matches the expected real cash amount when closing a cashier session.
-- If enabled, the system will block closure if amounts don't match,
-- requiring badge scan with CLOSE_SESSION_WITH_DISCREPANCY permission.
-- 
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- =============================================
-- Add GeneralSetup Entry for Cash Discrepancy Check
-- =============================================

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'general_setup')
BEGIN
    -- ENABLE_CASH_DISCREPANCY_CHECK config
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'ENABLE_CASH_DISCREPANCY_CHECK')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('ENABLE_CASH_DISCREPANCY_CHECK', 'true', 'Enable cash discrepancy check when closing session. If true, system validates closing amount matches expected real cash.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        
        PRINT 'Added ENABLE_CASH_DISCREPANCY_CHECK to general_setup';
    END
    ELSE
    BEGIN
        PRINT 'ENABLE_CASH_DISCREPANCY_CHECK already exists in general_setup. Skipping.';
    END
END
ELSE
BEGIN
    PRINT 'GeneralSetup table does not exist. Skipping GeneralSetup entries.';
END
GO

COMMIT TRANSACTION;
GO

PRINT '=============================================';
PRINT 'Migration completed successfully!';
PRINT '  - Added GeneralSetup config: ENABLE_CASH_DISCREPANCY_CHECK';
PRINT '=============================================';
