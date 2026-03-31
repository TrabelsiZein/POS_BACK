-- Migration 018: add promotion table

IF OBJECT_ID('promotion', 'U') IS NULL
BEGIN
    CREATE TABLE promotion (
        id                   BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        -- Identity
        code                 NVARCHAR(100)  NOT NULL,
        name                 NVARCHAR(255)  NOT NULL,
        description          NVARCHAR(MAX)  NULL,
        -- Scope & Target
        scope                NVARCHAR(30)   NOT NULL,
        item_id              BIGINT         NULL,
        item_family_id       BIGINT         NULL,
        item_sub_family_id   BIGINT         NULL,
        -- Thresholds
        minimum_quantity     INT            NULL,
        minimum_amount       DECIMAL(18,3)  NULL,
        -- Benefit
        benefit_type         NVARCHAR(30)   NOT NULL,
        discount_percentage  DECIMAL(7,4)   NULL,
        discount_amount      DECIMAL(18,3)  NULL,
        free_quantity        INT            NULL,
        -- Validity
        start_date           DATE           NULL,
        end_date             DATE           NULL,
        priority             INT            NOT NULL DEFAULT 0,
        -- _BaseEntity audit fields
        created_at           DATETIME2      NULL,
        updated_at           DATETIME2      NULL,
        created_by           NVARCHAR(255)  NULL,
        updated_by           NVARCHAR(255)  NULL,
        active               BIT            NULL DEFAULT 1,
        CONSTRAINT uq_promotion_code
            UNIQUE (code),
        CONSTRAINT fk_promotion_item
            FOREIGN KEY (item_id) REFERENCES item(id),
        CONSTRAINT fk_promotion_item_family
            FOREIGN KEY (item_family_id) REFERENCES item_family(id),
        CONSTRAINT fk_promotion_item_sub_family
            FOREIGN KEY (item_sub_family_id) REFERENCES item_sub_family(id)
    );
END

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_promotion_scope'
      AND object_id = OBJECT_ID('promotion')
)
BEGIN
    CREATE INDEX idx_promotion_scope
        ON promotion(scope);
END

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_promotion_active_dates'
      AND object_id = OBJECT_ID('promotion')
)
BEGIN
    CREATE INDEX idx_promotion_active_dates
        ON promotion(active, start_date, end_date);
END

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_promotion_item_id'
      AND object_id = OBJECT_ID('promotion')
)
BEGIN
    CREATE INDEX idx_promotion_item_id
        ON promotion(item_id);
END

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_promotion_item_family_id'
      AND object_id = OBJECT_ID('promotion')
)
BEGIN
    CREATE INDEX idx_promotion_item_family_id
        ON promotion(item_family_id);
END

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_promotion_item_sub_family_id'
      AND object_id = OBJECT_ID('promotion')
)
BEGIN
    CREATE INDEX idx_promotion_item_sub_family_id
        ON promotion(item_sub_family_id);
END
