-- =============================================
-- Payment Method Title Number Length - TICKET_RESTAURANT, CHEQUE_CADEAU, CLIENT_TRAITE
-- =============================================
-- Adds GeneralSetup entries for title number length validation
-- for TICKET_RESTAURANT, CHEQUE_CADEAU, and CLIENT_TRAITE payment methods.
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
    -- PAYMENT_METHOD_TICKET_RESTAURANT_TITLE_NUMBER_LENGTH
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'PAYMENT_METHOD_TICKET_RESTAURANT_TITLE_NUMBER_LENGTH')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('PAYMENT_METHOD_TICKET_RESTAURANT_TITLE_NUMBER_LENGTH', '7', 'Required length for title number (N° Titre) for TICKET_RESTAURANT payment method. Must be exactly this number of characters.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        PRINT 'Added PAYMENT_METHOD_TICKET_RESTAURANT_TITLE_NUMBER_LENGTH to general_setup';
    END
    ELSE
        PRINT 'PAYMENT_METHOD_TICKET_RESTAURANT_TITLE_NUMBER_LENGTH already exists. Skipping.';

    -- PAYMENT_METHOD_CHEQUE_CADEAU_TITLE_NUMBER_LENGTH
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'PAYMENT_METHOD_CHEQUE_CADEAU_TITLE_NUMBER_LENGTH')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('PAYMENT_METHOD_CHEQUE_CADEAU_TITLE_NUMBER_LENGTH', '7', 'Required length for title number (N° Titre) for CHEQUE_CADEAU payment method. Must be exactly this number of characters.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        PRINT 'Added PAYMENT_METHOD_CHEQUE_CADEAU_TITLE_NUMBER_LENGTH to general_setup';
    END
    ELSE
        PRINT 'PAYMENT_METHOD_CHEQUE_CADEAU_TITLE_NUMBER_LENGTH already exists. Skipping.';

    -- PAYMENT_METHOD_CLIENT_TRAITE_TITLE_NUMBER_LENGTH
    IF NOT EXISTS (SELECT * FROM [dbo].[general_setup] WHERE [code] = 'PAYMENT_METHOD_CLIENT_TRAITE_TITLE_NUMBER_LENGTH')
    BEGIN
        INSERT INTO [dbo].[general_setup] ([code], [valeur], [description], [read_only], [active], [created_by], [updated_by], [created_at], [updated_at])
        VALUES ('PAYMENT_METHOD_CLIENT_TRAITE_TITLE_NUMBER_LENGTH', '7', 'Required length for title number (N° Titre) for CLIENT_TRAITE payment method. Must be exactly this number of characters.', 0, 1, 'System', 'System', GETDATE(), GETDATE());
        PRINT 'Added PAYMENT_METHOD_CLIENT_TRAITE_TITLE_NUMBER_LENGTH to general_setup';
    END
    ELSE
        PRINT 'PAYMENT_METHOD_CLIENT_TRAITE_TITLE_NUMBER_LENGTH already exists. Skipping.';
END
ELSE
    PRINT 'GeneralSetup table does not exist. Skipping.';
GO

COMMIT TRANSACTION;
GO

PRINT 'Migration 007: Payment method title number length (TICKET_RESTAURANT, CHEQUE_CADEAU, CLIENT_TRAITE) completed.';
