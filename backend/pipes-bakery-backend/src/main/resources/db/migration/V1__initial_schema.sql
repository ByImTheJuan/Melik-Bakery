##db/migration/V1__initial_schema.sql

CREATE TABLE `addresses` (
  `zip_code` int NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `additional_information` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `clients` (
  `address_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKsrv16ica2c1csub334bxjjb59` (`email`),
  KEY `FK21gyuophuha3vq8t1os4x2jtl` (`address_id`),
  CONSTRAINT `FK21gyuophuha3vq8t1os4x2jtl`
    FOREIGN KEY (`address_id`) REFERENCES `addresses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `products` (
  `display_order` int NOT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `orders` (
  `total_amount` decimal(38,2) NOT NULL,
  `zip_code` int DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `additional_information` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `client_email` varchar(255) NOT NULL,
  `client_first_name` varchar(255) NOT NULL,
  `client_last_name` varchar(255) NOT NULL,
  `client_phone_number` varchar(255) NOT NULL,
  `country` varchar(255) DEFAULT NULL,
  `public_id` varchar(6) NOT NULL,
  `receiver_name` varchar(255) DEFAULT NULL,
  `street` varchar(255) DEFAULT NULL,
  `status` enum('CANCELLED','CREATED','DELIVERED','PAID','SHIPPED') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_orders_public_id` (`public_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `clients_orders` (
  `client_id` bigint NOT NULL,
  `orders_id` bigint NOT NULL,
  UNIQUE KEY `UKs7ptuuoy037sn28df5qdnwqrg` (`orders_id`),
  KEY `FK5mx58tcchygjrtmb8x5bgoe72` (`client_id`),
  CONSTRAINT `FK5mx58tcchygjrtmb8x5bgoe72`
    FOREIGN KEY (`client_id`) REFERENCES `clients` (`id`),
  CONSTRAINT `FKl7dbc9eyo2u08ltlrgaws60il`
    FOREIGN KEY (`orders_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `order_items` (
  `quantity` int NOT NULL,
  `unit_price_at_purchase` decimal(38,2) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w`
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi`
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `product_ingredients` (
  `product_id` bigint NOT NULL,
  `ingredient` varchar(255) DEFAULT NULL,
  KEY `FKa69i4fo6fys3gt2cbrxsrbn4` (`product_id`),
  CONSTRAINT `FKa69i4fo6fys3gt2cbrxsrbn4`
    FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;