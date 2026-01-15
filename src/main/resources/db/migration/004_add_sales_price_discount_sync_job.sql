-- =============================================
-- Add Sales Price and Discount Sync Job Migration
-- =============================================
-- This migration adds:
-- 1. The IMPORT_SALES_PRICES_AND_DISCOUNTS sync job
-- 2. GeneralSetup checkpoint entries for tracking last sync timestamps
-- 
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- =============================================
-- Add GeneralSetup Checkpoint Entries
-- =============================================

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'general_setup')
BEGIN
    -- ERP_SYNC_LAST_SALES_PRICE checkpoint
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'ERP_SYNC_LAST_SALES_PRICE')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('ERP_SYNC_LAST_SALES_PRICE', '', 'Timestamp (Modified_At) of last synchronized sales price', 1, 1, 'System', 'System', GETDATE(), GETDATE());
        
        PRINT 'Added ERP_SYNC_LAST_SALES_PRICE to general_setup';
    END
    ELSE
    BEGIN
        PRINT 'ERP_SYNC_LAST_SALES_PRICE already exists in general_setup. Skipping.';
    END

    -- ERP_SYNC_LAST_SALES_DISCOUNT checkpoint
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'ERP_SYNC_LAST_SALES_DISCOUNT')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('ERP_SYNC_LAST_SALES_DISCOUNT', '', 'Timestamp (Modified_At) of last synchronized sales discount', 1, 1, 'System', 'System', GETDATE(), GETDATE());
        
        PRINT 'Added ERP_SYNC_LAST_SALES_DISCOUNT to general_setup';
    END
    ELSE
    BEGIN
        PRINT 'ERP_SYNC_LAST_SALES_DISCOUNT already exists in general_setup. Skipping.';
    END

    -- Note: We don't create ERP_SYNC_LAST_SALES_PRICE_AND_DISCOUNT because
    -- the job uses separate checkpoints for each entity (ERP_SYNC_LAST_SALES_PRICE
    -- and ERP_SYNC_LAST_SALES_DISCOUNT) to allow independent incremental sync
END
ELSE
BEGIN
    PRINT 'GeneralSetup table does not exist. Skipping GeneralSetup entries.';
END
GO

-- =============================================
-- Add ERP Sync Job
-- =============================================

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'ERP_SYNC_JOB')
BEGIN
    -- Check if job already exists
    IF NOT EXISTS (SELECT * FROM [dbo].[ERP_SYNC_JOB] WHERE [job_type] = 'IMPORT_SALES_PRICES_AND_DISCOUNTS')
    BEGIN
        -- Insert the new sync job
        -- Cron: "0 40 * * * *" = Every hour at minute 40 (runs hourly)
        -- You can modify the cron expression as needed:
        -- - "0 40 * * * *" = Every hour at minute 40
        -- - "0 0 3 * * *" = Daily at 3:00 AM
        -- - "0 0 */2 * * *" = Every 2 hours
        INSERT INTO [dbo].[ERP_SYNC_JOB] 
            ([job_type], [cron_expression], [description], [enabled], [last_run_at], [next_run_at], [last_status], 
             [created_at], [updated_at], [created_by], [updated_by], [active])
        VALUES 
            ('IMPORT_SALES_PRICES_AND_DISCOUNTS', 
             '0 40 * * * *', 
             'Hourly import of sales prices and discounts from Dynamics NAV', 
             1, -- enabled = true
             NULL, 
             NULL, 
             NULL,
             GETDATE(), 
             GETDATE(), 
             'System', 
             'System', 
             1);
        
        PRINT 'Added IMPORT_SALES_PRICES_AND_DISCOUNTS job to ERP_SYNC_JOB';
    END
    ELSE
    BEGIN
        PRINT 'IMPORT_SALES_PRICES_AND_DISCOUNTS job already exists in ERP_SYNC_JOB. Skipping.';
    END
END
ELSE
BEGIN
    PRINT 'ERP_SYNC_JOB table does not exist. Skipping job insertion.';
END
GO

-- =============================================
-- Migration Complete
-- =============================================

COMMIT TRANSACTION;
GO

PRINT '=============================================';
PRINT 'Sales Price and Discount Sync Job Migration Completed Successfully';
PRINT '=============================================';
PRINT '';
PRINT 'Summary:';
PRINT '  - Added GeneralSetup checkpoint entries:';
PRINT '    * ERP_SYNC_LAST_SALES_PRICE (for SalesPrice entity)';
PRINT '    * ERP_SYNC_LAST_SALES_DISCOUNT (for SalesDiscount entity)';
PRINT '  - Added ERP sync job: IMPORT_SALES_PRICES_AND_DISCOUNTS';
PRINT '';
PRINT 'Job Configuration:';
PRINT '  - Job Type: IMPORT_SALES_PRICES_AND_DISCOUNTS';
PRINT '  - Cron Expression: 0 40 * * * * (runs every hour at minute 40)';
PRINT '  - Description: Hourly import of sales prices and discounts from Dynamics NAV';
PRINT '  - Enabled: Yes';
PRINT '';
PRINT 'Note:';
PRINT '  - The job will sync both SalesPrice and SalesDiscount entities';
PRINT '  - Each entity has its own separate checkpoint (ERP_SYNC_LAST_SALES_PRICE';
PRINT '    and ERP_SYNC_LAST_SALES_DISCOUNT) for independent incremental sync';
PRINT '  - This allows each entity to track its own last sync timestamp';
PRINT '  - You can modify the cron expression in the ERP Jobs admin page';
PRINT '  - You can enable/disable the job from the ERP Jobs admin page';
PRINT '=============================================';

