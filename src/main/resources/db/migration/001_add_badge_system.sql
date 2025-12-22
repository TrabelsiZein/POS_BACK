-- =============================================
-- Badge-Based Permission System Migration
-- =============================================
-- This migration adds badge-related columns to existing tables
-- and initializes GeneralSetup configuration entries.
-- 
-- NOTE: Table badge_scan_log will be 
-- created automatically by Hibernate on application startup.
-- 
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- =============================================
-- 1. Add Badge Columns to user_account Table
-- =============================================

-- Add badge_code column (unique, nullable)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[user_account]') AND name = 'badge_code')
BEGIN
    ALTER TABLE [dbo].[user_account]
    ADD [badge_code] NVARCHAR(50) NULL;
    
    -- Create unique index on badge_code
    CREATE UNIQUE NONCLUSTERED INDEX [IX_user_account_badge_code]
    ON [dbo].[user_account] ([badge_code])
    WHERE [badge_code] IS NOT NULL;
    
    PRINT 'Added badge_code column to user_account table';
END
GO

-- Add badge_permissions column (comma-separated enum values)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[user_account]') AND name = 'badge_permissions')
BEGIN
    ALTER TABLE [dbo].[user_account]
    ADD [badge_permissions] NVARCHAR(500) NULL;
    
    PRINT 'Added badge_permissions column to user_account table';
END
GO

-- Add badge_expiration_date column
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[user_account]') AND name = 'badge_expiration_date')
BEGIN
    ALTER TABLE [dbo].[user_account]
    ADD [badge_expiration_date] DATETIME2 NULL;
    
    PRINT 'Added badge_expiration_date column to user_account table';
END
GO

-- Add badge_revoked column (NOT NULL, default false)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[user_account]') AND name = 'badge_revoked')
BEGIN
    ALTER TABLE [dbo].[user_account]
    ADD [badge_revoked] BIT NOT NULL DEFAULT 0;
    
    PRINT 'Added badge_revoked column to user_account table';
END
GO

-- Add badge_revoked_at column
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[user_account]') AND name = 'badge_revoked_at')
BEGIN
    ALTER TABLE [dbo].[user_account]
    ADD [badge_revoked_at] DATETIME2 NULL;
    
    PRINT 'Added badge_revoked_at column to user_account table';
END
GO

-- Add badge_revoked_by_id column (foreign key to user_account)
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[user_account]') AND name = 'badge_revoked_by_id')
BEGIN
    ALTER TABLE [dbo].[user_account]
    ADD [badge_revoked_by_id] BIGINT NULL;
    
    -- Add foreign key constraint
    ALTER TABLE [dbo].[user_account]
    ADD CONSTRAINT [FK_user_account_badge_revoked_by]
    FOREIGN KEY ([badge_revoked_by_id])
    REFERENCES [dbo].[user_account] ([id]);
    
    PRINT 'Added badge_revoked_by_id column to user_account table';
END
GO

-- Add badge_revoke_reason column
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[user_account]') AND name = 'badge_revoke_reason')
BEGIN
    ALTER TABLE [dbo].[user_account]
    ADD [badge_revoke_reason] NVARCHAR(500) NULL;
    
    PRINT 'Added badge_revoke_reason column to user_account table';
END
GO

-- =============================================
-- 2. Add GeneralSetup Entries
-- =============================================
-- NOTE: Table badge_scan_log will be 
-- created automatically by Hibernate on application startup
-- =============================================

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'general_setup')
BEGIN
    -- ALWAYS_SHOW_BADGE_SCAN_POPUP
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'ALWAYS_SHOW_BADGE_SCAN_POPUP')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('ALWAYS_SHOW_BADGE_SCAN_POPUP', 'false', 'If true, always show badge scan popup for restricted functionalities, even if the current user has permission. If false, only show if current user lacks permission.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        
        PRINT 'Added ALWAYS_SHOW_BADGE_SCAN_POPUP to general_setup';
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
PRINT 'Badge System Migration Completed Successfully';
PRINT '=============================================';
PRINT '';
PRINT 'Summary:';
PRINT '  - Added badge columns to user_account table';
PRINT '  - Added GeneralSetup entries for badge configuration';
PRINT '  - NOTE: Table badge_scan_log';
PRINT '    will be created automatically by Hibernate on startup';
PRINT '';
PRINT 'Next Steps:';
PRINT '  1. Start the application - Hibernate will create new tables';
PRINT '  2. Verify all tables and columns were created correctly';
PRINT '  3. Test badge assignment in User Management';
PRINT '  4. Test badge scanning in POS features';
PRINT '  5. Verify audit logs are being created';
PRINT '=============================================';
