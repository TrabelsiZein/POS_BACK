-- =============================================
-- Add ERP Deletion Sync Job Migration
-- =============================================
-- This migration adds:
-- 1. The SYNC_ERP_DELETIONS sync job (removes from POS records deleted in ERP via Log API)
-- 2. GeneralSetup checkpoint entry for tracking last processed deletion log timestamp
--
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- =============================================
-- Add GeneralSetup Checkpoint Entry
-- =============================================

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'general_setup')
BEGIN
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'ERP_SYNC_LAST_DELETION_LOG')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('ERP_SYNC_LAST_DELETION_LOG', '', 'Timestamp (Modified_At) of last processed deletion log entry', 1, 1, 'System', 'System', GETDATE(), GETDATE());

        PRINT 'Added ERP_SYNC_LAST_DELETION_LOG to general_setup';
    END
    ELSE
    BEGIN
        PRINT 'ERP_SYNC_LAST_DELETION_LOG already exists in general_setup. Skipping.';
    END
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
    IF NOT EXISTS (SELECT * FROM [dbo].[ERP_SYNC_JOB] WHERE [job_type] = 'SYNC_ERP_DELETIONS')
    BEGIN
        INSERT INTO [dbo].[ERP_SYNC_JOB]
            ([job_type], [cron_expression], [description], [enabled], [last_run_at], [next_run_at], [last_status],
             [created_at], [updated_at], [created_by], [updated_by], [active])
        VALUES
            ('SYNC_ERP_DELETIONS',
             '0 50 * * * *',
             'Sync deletions from ERP Log (Sales Price, Sales Discount)',
             0,
             NULL,
             NULL,
             NULL,
             GETDATE(),
             GETDATE(),
             'System',
             'System',
             1);

        PRINT 'Added SYNC_ERP_DELETIONS job to ERP_SYNC_JOB';
    END
    ELSE
    BEGIN
        PRINT 'SYNC_ERP_DELETIONS job already exists in ERP_SYNC_JOB. Skipping.';
    END
END
ELSE
BEGIN
    PRINT 'ERP_SYNC_JOB table does not exist. Skipping job insertion.';
END
GO

COMMIT TRANSACTION;
GO

PRINT '=============================================';
PRINT 'ERP Deletion Sync Job Migration Completed Successfully';
PRINT '=============================================';
PRINT '  - GeneralSetup: ERP_SYNC_LAST_DELETION_LOG';
PRINT '  - ERP sync job: SYNC_ERP_DELETIONS (cron: 0 50 * * * *, disabled by default)';
PRINT '=============================================';
