-- Migration 016: Add stock_movement table for stock audit trail
-- Immutable log of every stock quantity change (sale, purchase, return, adjustment).
-- Only populated in standalone mode; ERP mode manages stock via the ERP system.

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='stock_movement' AND xtype='U')
BEGIN
    CREATE TABLE stock_movement (
        id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
        movement_type       NVARCHAR(40)    NOT NULL,
        direction           NVARCHAR(5)     NOT NULL,
        item_id             BIGINT          NOT NULL,
        quantity            INT             NOT NULL,
        unit_price_ht       FLOAT,
        vat_percent         INT,
        unit_price_ttc      FLOAT,
        reference_id        BIGINT,
        reference_type      NVARCHAR(20),
        cashier_session_id  BIGINT,
        notes               NVARCHAR(500),
        -- BaseEntity fields
        created_at          DATETIME2,
        updated_at          DATETIME2,
        created_by          NVARCHAR(100),
        updated_by          NVARCHAR(100),
        active              BIT DEFAULT 1,

        CONSTRAINT FK_stock_movement_item
            FOREIGN KEY (item_id) REFERENCES item(id),
        CONSTRAINT FK_stock_movement_session
            FOREIGN KEY (cashier_session_id) REFERENCES cashier_session(id)
    );

    -- Index for reporting queries: by item + date
    CREATE INDEX IDX_stock_movement_item_date
        ON stock_movement (item_id, created_at);

    -- Index for reporting queries: by type + date
    CREATE INDEX IDX_stock_movement_type_date
        ON stock_movement (movement_type, created_at);

    -- Index for looking up movements by source document
    CREATE INDEX IDX_stock_movement_reference
        ON stock_movement (reference_type, reference_id);
END
