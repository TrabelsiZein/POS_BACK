-- =============================================
-- Add Auto Add Cash Payment Config Migration
-- =============================================
-- This migration adds:
-- 1. AUTO_ADD_CASH_PAYMENT_ON_PAYMENT_PAGE config to GeneralSetup
-- 
-- This config controls whether to automatically add an empty "Client Espèce"
-- payment method when opening the payment page.
-- 
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- =============================================
-- Add GeneralSetup Entry for Auto Add Cash Payment
-- =============================================

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'general_setup')
BEGIN
    -- AUTO_ADD_CASH_PAYMENT_ON_PAYMENT_PAGE config
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'AUTO_ADD_CASH_PAYMENT_ON_PAYMENT_PAGE')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('AUTO_ADD_CASH_PAYMENT_ON_PAYMENT_PAGE', 'true', 'If true, automatically add empty "Client Espèce" payment method when opening payment page. If false, keep payment page empty with no selected payment method.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        
        PRINT 'Added AUTO_ADD_CASH_PAYMENT_ON_PAYMENT_PAGE to general_setup';
    END
    ELSE
    BEGIN
        PRINT 'AUTO_ADD_CASH_PAYMENT_ON_PAYMENT_PAGE already exists in general_setup. Skipping.';
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
PRINT '  - Added GeneralSetup config: AUTO_ADD_CASH_PAYMENT_ON_PAYMENT_PAGE';
PRINT '=============================================';

