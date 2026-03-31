-- Add POS_SHOW_IMAGES general setup parameter.
-- When set to 'false', the backend strips image fields from POS-facing responses
-- and the frontend suppresses all image requests, preventing slowdowns on busy networks.
INSERT INTO general_setup (code, valeur, description, read_only, active, created_at, updated_at)
VALUES (
    'POS_SHOW_IMAGES',
    'true',
    'Show product/family/subfamily images in POS cashier screen. Set to false to disable images if the system is slow.',
    false,
    true,
    GETDATE(),
    GETDATE()
);
