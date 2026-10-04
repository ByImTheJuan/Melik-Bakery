-- Personalized cakes are order lines without a catalog product: they keep their own
-- name and a JSON snapshot of the chosen size, flavour, tiers and decoration.
ALTER TABLE `order_items`
  ADD COLUMN `item_name` varchar(120) NULL,
  ADD COLUMN `custom_cake_details` text NULL;
