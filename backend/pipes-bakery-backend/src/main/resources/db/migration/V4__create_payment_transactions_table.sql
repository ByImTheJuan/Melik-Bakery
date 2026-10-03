CREATE TABLE `payment_transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `wompi_reference` varchar(64) NOT NULL,
  `wompi_transaction_id` varchar(64) DEFAULT NULL,
  `status` enum('APPROVED','DECLINED','ERROR','PENDING','VOIDED') NOT NULL,
  `amount_in_cents` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_payment_transactions_wompi_reference` (`wompi_reference`),
  UNIQUE KEY `UK_payment_transactions_wompi_transaction_id` (`wompi_transaction_id`),
  KEY `FK_payment_transactions_order` (`order_id`),
  CONSTRAINT `FK_payment_transactions_order`
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
