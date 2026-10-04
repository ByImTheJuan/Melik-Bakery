-- Customers now choose when their order is delivered: a date (at least 3 days ahead)
-- and a time window. Older orders keep NULL ("as soon as possible").
ALTER TABLE `orders`
  ADD COLUMN `delivery_date` date NULL,
  ADD COLUMN `delivery_slot` varchar(20) NULL;
