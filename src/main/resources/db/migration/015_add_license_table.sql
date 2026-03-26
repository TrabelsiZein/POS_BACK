-- Migration 015: Add license_record table for on-prem licensing system
-- Each row = one uploaded license. current_license=1 marks the active license.
-- Rows are never deleted; they form the full upload history.

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='license_record' AND xtype='U')
BEGIN
    CREATE TABLE license_record (
        id              BIGINT IDENTITY(1,1) PRIMARY KEY,
        company_name    NVARCHAR(255),
        app_id          NVARCHAR(100),
        issued_at       DATE,
        expires_at      DATE,
        raw_json        NVARCHAR(MAX),
        uploaded_at     DATETIME2,
        uploaded_by     NVARCHAR(100),
        current_license BIT NOT NULL DEFAULT 0,
        -- BaseEntity fields
        created_at      DATETIME2,
        updated_at      DATETIME2,
        created_by      NVARCHAR(100),
        updated_by      NVARCHAR(100),
        active          BIT DEFAULT 1
    );
END
