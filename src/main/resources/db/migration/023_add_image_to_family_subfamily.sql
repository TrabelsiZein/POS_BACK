-- Add image_filename column to item_family and item_sub_family tables
-- Stores only the filename (e.g. "42.jpg"). Full URL is constructed by the image serving endpoint.
ALTER TABLE item_family
    ADD image_filename VARCHAR(255) NULL;

ALTER TABLE item_sub_family
    ADD image_filename VARCHAR(255) NULL;
