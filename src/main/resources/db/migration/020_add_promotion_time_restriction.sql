-- Migration 020: Add time restriction columns to promotion table (Happy Hour / Flash Sale)
-- day_of_week : comma-separated day names (e.g. 'MONDAY,FRIDAY'). NULL = every day.
-- time_start  : time of day from which promotion is active. NULL = no restriction.
-- time_end    : time of day until which promotion is active. NULL = no restriction.

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'promotion' AND COLUMN_NAME = 'day_of_week'
)
BEGIN
    ALTER TABLE promotion ADD day_of_week NVARCHAR(100) NULL;
END

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'promotion' AND COLUMN_NAME = 'time_start'
)
BEGIN
    ALTER TABLE promotion ADD time_start TIME NULL;
END

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'promotion' AND COLUMN_NAME = 'time_end'
)
BEGIN
    ALTER TABLE promotion ADD time_end TIME NULL;
END
