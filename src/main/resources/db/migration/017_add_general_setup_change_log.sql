-- Migration 017: add audit table for general setup changes

IF OBJECT_ID('general_setup_change_log', 'U') IS NULL
BEGIN
    CREATE TABLE general_setup_change_log (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        general_setup_id BIGINT NOT NULL,
        code NVARCHAR(128) NOT NULL,
        old_value NVARCHAR(MAX) NULL,
        new_value NVARCHAR(MAX) NULL,
        change_type NVARCHAR(20) NOT NULL,
        source NVARCHAR(20) NOT NULL,
        reason NVARCHAR(512) NULL,
        created_at DATETIME2 NULL,
        updated_at DATETIME2 NULL,
        created_by NVARCHAR(255) NULL,
        updated_by NVARCHAR(255) NULL,
        active BIT NULL,
        CONSTRAINT fk_general_setup_change_log_setup
            FOREIGN KEY (general_setup_id) REFERENCES general_setup(id)
    );
END

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_general_setup_change_log_setup_id'
      AND object_id = OBJECT_ID('general_setup_change_log')
)
BEGIN
    CREATE INDEX idx_general_setup_change_log_setup_id
        ON general_setup_change_log(general_setup_id);
END

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'idx_general_setup_change_log_code_created_at'
      AND object_id = OBJECT_ID('general_setup_change_log')
)
BEGIN
    CREATE INDEX idx_general_setup_change_log_code_created_at
        ON general_setup_change_log(code, created_at DESC);
END
