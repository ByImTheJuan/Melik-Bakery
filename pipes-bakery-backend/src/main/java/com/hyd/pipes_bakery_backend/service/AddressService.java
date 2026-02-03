package com.hyd.pipes_bakery_backend.service;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;
import com.hyd.pipes_bakery_backend.mapper.AddressMapper;
import com.hyd.pipes_bakery_backend.model.Address;
import com.hyd.pipes_bakery_backend.repository.AddressRepository;

@Service
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(addressMapper::toDto)
                .toList();
    }

    @Override
    public AddressResponseDTO getAddressById(@NonNull Long id) {

        return addressRepository.findById(id).map(addressMapper::toDto).orElseThrow(() -> new ResourceNotFoundException(
                "Address not found with id " + id
        ));
    }

    @Override
    public AddressResponseDTO createAddress(@NonNull AddressRequestDTO dto) {
        Address address = addressMapper.toEntity(dto);
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDto(savedAddress);
    }

    @Override
    public void deleteAddress(@NonNull Long id) {
        if(addressRepository.existsById(id))
            addressRepository.deleteById(id);

        else
            throw new ResourceNotFoundException("Address not found with id " + id);
    }

    @Override
    public AddressResponseDTO updateAddress(@NonNull Long id, AddressRequestDTO updatedAddress) {
            return addressRepository.findById(id)
                .map(address -> {
                    address.setStreet(updatedAddress.getStreet());
                    address.setAdditionalInformation(updatedAddress.getAdditionalInformation());
                    address.setCity(updatedAddress.getCity());
                    address.setCountry(updatedAddress.getCountry());
                    address.setZipCode(updatedAddress.getZipCode());
                    return addressRepository.save(address);
                })
                .map(addressMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Address not found with id " + id
            ));
    }
}
