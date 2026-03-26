-- Migration 016: add installation_id on license_record for machine-bound licensing

IF COL_LENGTH('license_record', 'installation_id') IS NULL
BEGIN
    ALTER TABLE license_record
    ADD installation_id NVARCHAR(128) NULL;
END
