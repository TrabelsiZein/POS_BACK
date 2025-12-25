-- =============================================
-- Payment Method Title Number Length Configuration Migration
-- =============================================
-- This migration adds GeneralSetup configuration entry for
-- payment method title number length validation.
-- 
-- This allows configurable validation of title number (N° Titre)
-- length per payment method type (e.g., CLIENT_CHEQUE must be exactly 7 characters).
-- 
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- =============================================
-- Add GeneralSetup Entry for Payment Method Title Number Length
-- =============================================

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'general_setup')
BEGIN
    -- PAYMENT_METHOD_CLIENT_CHEQUE_TITLE_NUMBER_LENGTH
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'PAYMENT_METHOD_CLIENT_CHEQUE_TITLE_NUMBER_LENGTH')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('PAYMENT_METHOD_CLIENT_CHEQUE_TITLE_NUMBER_LENGTH', '7', 'Required length for title number (N° Titre) for CLIENT_CHEQUE payment method. Must be exactly this number of characters.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        
        PRINT 'Added PAYMENT_METHOD_CLIENT_CHEQUE_TITLE_NUMBER_LENGTH to general_setup';
    END
    ELSE
    BEGIN
        PRINT 'PAYMENT_METHOD_CLIENT_CHEQUE_TITLE_NUMBER_LENGTH already exists in general_setup. Skipping.';
    END
END
ELSE
BEGIN
    PRINT 'GeneralSetup table does not exist. Skipping GeneralSetup entries.';
END
GO

-- =============================================
-- Migration Complete
-- =============================================

COMMIT TRANSACTION;
GO

PRINT '=============================================';
PRINT 'Payment Method Title Number Length Configuration Migration Completed Successfully';
PRINT '=============================================';
PRINT '';
PRINT 'Summary:';
PRINT '  - Added GeneralSetup entry for CLIENT_CHEQUE title number length validation';
PRINT '';
PRINT 'Configuration:';
PRINT '  - Code: PAYMENT_METHOD_CLIENT_CHEQUE_TITLE_NUMBER_LENGTH';
PRINT '  - Value: 7 (exact length required)';
PRINT '  - Description: Required length for title number for CLIENT_CHEQUE payment method';
PRINT '';
PRINT 'Note:';
PRINT '  - This configuration can be modified per client deployment';
PRINT '  - To add validation for other payment methods, create entries with pattern:';
PRINT '    PAYMENT_METHOD_{PAYMENT_METHOD_TYPE}_TITLE_NUMBER_LENGTH';
PRINT '  - If not configured, only "required" validation applies (backward compatible)';
PRINT '=============================================';

