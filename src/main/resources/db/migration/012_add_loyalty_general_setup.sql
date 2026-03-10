-- =============================================
-- Loyalty (Fidélité) Program — GeneralSetup entry
-- =============================================
-- This migration adds:
-- 1. LOYALTY_ENABLED — master on/off switch for the loyalty program.
--    Detailed configuration (rates, limits, expiry) is managed in the
--    loyalty_program table; this flag only controls whether the feature
--    is visible to cashiers.
--
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- =============================================
-- Add LOYALTY_ENABLED to general_setup
-- =============================================

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'general_setup')
BEGIN
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'LOYALTY_ENABLED')
    BEGIN
        INSERT INTO [dbo].[general_setup]
            ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES
            ('LOYALTY_ENABLED',
             'false',
             'Enable the loyalty (fidélité) program. When true, cashiers can attach loyalty cards to sales and customers earn/redeem points. Configure rates in Loyalty Programs admin page.',
             0,
             1,
             'System',
             'System',
             GETDATE(),
             GETDATE());

        PRINT 'Added LOYALTY_ENABLED to general_setup';
    END
    ELSE
    BEGIN
        PRINT 'LOYALTY_ENABLED already exists in general_setup. Skipping.';
    END
END
ELSE
BEGIN
    PRINT 'general_setup table does not exist. Skipping.';
END
GO

COMMIT TRANSACTION;
GO

PRINT '=============================================';
PRINT 'Migration 012: Loyalty GeneralSetup entry completed.';
PRINT '  - general_setup: LOYALTY_ENABLED (default: false)';
PRINT '=============================================';
