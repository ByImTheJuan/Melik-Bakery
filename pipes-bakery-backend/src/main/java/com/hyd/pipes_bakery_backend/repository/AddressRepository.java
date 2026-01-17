package com.hyd.pipes_bakery_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyd.pipes_bakery_backend.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
