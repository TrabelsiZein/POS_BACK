-- =============================================
-- Company Information — singleton table
-- =============================================
-- Creates the company_information table and inserts a default empty row (id=1).
-- This row is always present; the backend exposes only GET and PUT endpoints.
--
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration.
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

-- =============================================
-- Create company_information table
-- =============================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'company_information')
BEGIN
    CREATE TABLE [dbo].[company_information] (
        [id]                    BIGINT          NOT NULL PRIMARY KEY,
        [company_name]          NVARCHAR(200)   NULL,
        [logo_base64]           NVARCHAR(MAX)   NULL,
        [matricule_fiscal]      NVARCHAR(100)   NULL,
        [address]               NVARCHAR(500)   NULL,
        [city]                  NVARCHAR(100)   NULL,
        [postal_code]           NVARCHAR(20)    NULL,
        [country]               NVARCHAR(100)   NULL,
        [phone]                 NVARCHAR(50)    NULL,
        [fax]                   NVARCHAR(50)    NULL,
        [email]                 NVARCHAR(200)   NULL,
        [website]               NVARCHAR(200)   NULL,
        [bank_name]             NVARCHAR(200)   NULL,
        [bank_account]          NVARCHAR(100)   NULL,
        [rib]                   NVARCHAR(100)   NULL,
        [invoice_footer_note]   NVARCHAR(1000)  NULL,
        [active]                BIT             NOT NULL DEFAULT 1,
        [created_at]            DATETIME2       NOT NULL DEFAULT GETDATE(),
        [updated_at]            DATETIME2       NOT NULL DEFAULT GETDATE(),
        [created_by]            NVARCHAR(255)   NULL,
        [updated_by]            NVARCHAR(255)   NULL
    );

    PRINT 'Created company_information table';
END
ELSE
BEGIN
    PRINT 'company_information table already exists. Skipping table creation.';
END
GO

-- =============================================
-- Insert default singleton row (id=1)
-- =============================================

IF NOT EXISTS (SELECT * FROM [dbo].[company_information] WHERE [id] = 1)
BEGIN
    INSERT INTO [dbo].[company_information]
        ([id], [company_name], [active], [created_by], [updated_by], [created_at], [updated_at])
    VALUES
        (1, '', 1, 'System', 'System', GETDATE(), GETDATE());

    PRINT 'Inserted default company_information row (id=1)';
END
ELSE
BEGIN
    PRINT 'Default company_information row already exists. Skipping insert.';
END
GO

COMMIT TRANSACTION;
GO

PRINT '=============================================';
PRINT 'Migration 013: company_information completed.';
PRINT '  - Table: company_information (singleton, id=1)';
PRINT '=============================================';
