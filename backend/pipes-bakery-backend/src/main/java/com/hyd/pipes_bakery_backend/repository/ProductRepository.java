package com.hyd.pipes_bakery_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hyd.pipes_bakery_backend.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderByDisplayOrderAscIdAsc();

    Optional<Product> findTopByOrderByDisplayOrderDesc();
}
