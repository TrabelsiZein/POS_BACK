-- =============================================
-- Add Plafond Espèce (Cash Ceiling) Config
-- =============================================
-- Adds GeneralSetup entry PLAFOND_ESPECE.
-- When empty or null: allow payment with espèce (cash) without limit (default).
-- When set (e.g. 1000): maximum allowed cash amount per sale in TND.
--
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

IF EXISTS (SELECT * FROM sys.tables WHERE name = 'general_setup')
BEGIN
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'PLAFOND_ESPECE')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('PLAFOND_ESPECE', '', 'Plafond espèce (TND). Empty = no limit. When set (e.g. 1000), maximum cash amount allowed per sale in TND.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        PRINT 'Added PLAFOND_ESPECE to general_setup';
    END
    ELSE
        PRINT 'PLAFOND_ESPECE already exists in general_setup. Skipping.';
END
ELSE
    PRINT 'GeneralSetup table does not exist. Skipping.';
GO

COMMIT TRANSACTION;
GO

PRINT 'Migration 008: Plafond espèce config completed.';
