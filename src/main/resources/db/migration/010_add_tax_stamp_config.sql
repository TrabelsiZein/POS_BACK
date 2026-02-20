-- =============================================
-- Tax stamp (timbre fiscal) config and Item.show_in_pos
-- =============================================
-- 1. Adds column show_in_pos to item (default 1 = show in POS).
-- 2. Adds GeneralSetup entries: ENABLE_TAX_STAMP, TAX_STAMP_VALUE_MILLIMES, TAX_STAMP_ERP_ITEM_CODE.
--    Used for Tunisia: 100 millimes per receipt (e.g. since Feb 2022).
--
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- 1. Add show_in_pos to item if not exists
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'item')
BEGIN
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('item') AND name = 'show_in_pos')
    BEGIN
        ALTER TABLE [dbo].[item] ADD [show_in_pos] BIT NOT NULL DEFAULT 1;
        PRINT 'Added column show_in_pos to item';
    END
    ELSE
        PRINT 'Column item.show_in_pos already exists. Skipping.';
END
GO

-- 2. Add GeneralSetup entries for tax stamp
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'general_setup')
BEGIN
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'ENABLE_TAX_STAMP')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('ENABLE_TAX_STAMP', 'false', 'Enable tax stamp (timbre fiscal) per receipt. When true, adds configured amount (e.g. 100 millimes in Tunisia) as a line per sale.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        PRINT 'Added ENABLE_TAX_STAMP to general_setup';
    END

    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'TAX_STAMP_VALUE_MILLIMES')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('TAX_STAMP_VALUE_MILLIMES', '100', 'Tax stamp amount in millimes (e.g. 100 = 0.100 TND per receipt).', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        PRINT 'Added TAX_STAMP_VALUE_MILLIMES to general_setup';
    END

    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'TAX_STAMP_ERP_ITEM_CODE')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('TAX_STAMP_ERP_ITEM_CODE', '', 'ERP item code for tax stamp. Used when exporting ticket lines to ERP. Leave empty if not using ERP or not configured.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        PRINT 'Added TAX_STAMP_ERP_ITEM_CODE to general_setup';
    END
END
GO

COMMIT TRANSACTION;
GO

PRINT 'Migration 010: Tax stamp config and item.show_in_pos completed.';
