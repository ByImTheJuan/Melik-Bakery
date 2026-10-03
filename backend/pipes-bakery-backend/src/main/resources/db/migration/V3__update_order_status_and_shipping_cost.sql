# Widen the enum first so both the old and new labels are valid simultaneously.
ALTER TABLE `orders`
  MODIFY COLUMN `status` enum('CANCELLED','CREATED','DELIVERED','PAID','PAYMENT_PENDING','PREPARING','SHIPPED') NOT NULL;

UPDATE `orders` SET `status` = 'PAYMENT_PENDING' WHERE `status` = 'CREATED';

# Narrow to the final enum set.
ALTER TABLE `orders`
  MODIFY COLUMN `status` enum('CANCELLED','DELIVERED','PAID','PAYMENT_PENDING','PREPARING','SHIPPED') NOT NULL;

# Historic orders never charged shipping; default existing rows to 0 so total_amount stays unchanged for them.
# New orders always set this explicitly at insert time (see Order.java / OrderService#checkout).
ALTER TABLE `orders`
  ADD COLUMN `shipping_cost` decimal(38,2) NOT NULL DEFAULT '0.00' AFTER `total_amount`;
