# Orders are now created only once a payment is approved, so a payment attempt
# exists before (and possibly without) its order. The attempt keeps the cart it
# came from and a JSON snapshot of the checkout data used to build the order.
ALTER TABLE `payment_transactions`
  MODIFY COLUMN `order_id` bigint NULL,
  ADD COLUMN `cart_id` varchar(36) NULL AFTER `order_id`,
  ADD COLUMN `checkout_data` text NULL AFTER `cart_id`;
