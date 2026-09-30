# Products now store only the image file name (e.g. "cinnamonRoll.jpg"),
# resolved against app.images.path on disk and the /images/** URL pattern.

ALTER TABLE products RENAME COLUMN image_url TO image_file;

UPDATE products
SET image_file = SUBSTRING_INDEX(image_file, '/', -1)
WHERE image_file LIKE '%/%';
