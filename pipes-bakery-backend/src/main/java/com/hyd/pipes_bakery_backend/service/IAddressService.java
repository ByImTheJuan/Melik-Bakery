package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;
import com.hyd.pipes_bakery_backend.model.Address;

public interface IAddressService {

    List<AddressResponseDTO> getAllAddresses();

    AddressResponseDTO getAddressById(@NonNull Long id);

    AddressResponseDTO createAddress(@NonNull AddressRequestDTO Address);

    void deleteAddress(@NonNull Long id);

    AddressResponseDTO updateAddress(@NonNull Long id, AddressRequestDTO updatedAddress);

    AddressResponseDTO toDto(@NonNull Address address);

    Address toEntity(@NonNull AddressRequestDTO dto);
}
