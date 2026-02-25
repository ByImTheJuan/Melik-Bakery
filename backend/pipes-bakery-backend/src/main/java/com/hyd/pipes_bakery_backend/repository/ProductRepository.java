package com.hyd.pipes_bakery_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hyd.pipes_bakery_backend.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
