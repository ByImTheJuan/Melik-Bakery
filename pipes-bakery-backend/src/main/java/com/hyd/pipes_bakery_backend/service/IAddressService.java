package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;
import com.hyd.pipes_bakery_backend.model.Address;

public interface IAddressService {

    List<AddressResponseDTO> getAllAddresses();

    AddressResponseDTO getAddressById(Long id);

    AddressResponseDTO createAddress(AddressRequestDTO Address);

    void deleteAddress(Long id);

    AddressResponseDTO updateAddress(Long id, AddressRequestDTO updatedAddress);

    AddressResponseDTO toDto(Address address);

    Address toEntity(AddressRequestDTO dto);
}
