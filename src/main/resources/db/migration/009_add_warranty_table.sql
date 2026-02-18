-- =============================================
-- Warranty table (manual warranty registration per sold item)
-- =============================================
-- When using ddl-auto=update, Hibernate creates this table automatically.
-- Use this script for environments that apply migrations manually.
--
-- IMPORTANT: Update the database name in the USE statement below
-- before running this migration
-- =============================================

USE [pos_db_dev] -- Change database name as needed
GO

BEGIN TRANSACTION;
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'warranty')
BEGIN
    CREATE TABLE [dbo].[warranty] (
        [id] BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        [created_at] DATETIME2 NOT NULL,
        [updated_at] DATETIME2 NOT NULL,
        [created_by] NVARCHAR(255) NULL,
        [updated_by] NVARCHAR(255) NULL,
        [active] BIT NOT NULL DEFAULT 1,
        [sales_header_id] BIGINT NOT NULL,
        [sales_line_id] BIGINT NOT NULL,
        [item_id] BIGINT NOT NULL,
        [start_date] DATE NOT NULL,
        [end_date] DATE NOT NULL,
        [quantity_covered] INT NOT NULL DEFAULT 1,
        [notes] NVARCHAR(MAX) NULL,
        CONSTRAINT [FK_warranty_sales_header] FOREIGN KEY ([sales_header_id]) REFERENCES [dbo].[sales_header] ([id]),
        CONSTRAINT [FK_warranty_sales_line] FOREIGN KEY ([sales_line_id]) REFERENCES [dbo].[sales_line] ([id]),
        CONSTRAINT [FK_warranty_item] FOREIGN KEY ([item_id]) REFERENCES [dbo].[item] ([id])
    );
    PRINT 'Created warranty table';
END
ELSE
    PRINT 'warranty table already exists. Skipping.';
GO

COMMIT TRANSACTION;
GO

PRINT 'Migration 009: Warranty table completed.';
