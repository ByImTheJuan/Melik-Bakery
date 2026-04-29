package com.hyd.pipes_bakery_backend.mapper;

import org.springframework.stereotype.Component;

import com.hyd.pipes_bakery_backend.dto.address.AddressRequestDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressResponseDTO;
import com.hyd.pipes_bakery_backend.dto.address.AddressSnapshotDTO;
import com.hyd.pipes_bakery_backend.model.Address;
import com.hyd.pipes_bakery_backend.model.AddressSnapshot;

@Component
public class AddressMapper {

    public AddressResponseDTO toDto(Address address) {
        if (address == null) {
            return null;
        }

        return new AddressResponseDTO(address.getId(),
                                        address.getStreet(),
                                        address.getAdditionalInformation(),
                                        address.getCity(),
                                        address.getZipCode(),
                                        address.getCountry());
    }

    public Address toEntity(AddressRequestDTO dto){
        if (dto == null) {
            return null;
        }

        return new Address(dto.getStreet(),
        dto.getAdditionalInformation(),
        dto.getCity(),
        dto.getZipCode(),
        dto.getCountry());
    }

    public AddressSnapshotDTO toSnapshotDto(AddressSnapshot address) {
        return new AddressSnapshotDTO(address.getStreet(),
                                        address.getAdditionalInformation(),
                                        address.getCity(),
                                        address.getZipCode(),
                                        address.getCountry());
    }

    public AddressSnapshot toSnapshotEntity(AddressSnapshotDTO dto){
        return new AddressSnapshot(dto.getStreet(),
        dto.getAdditionalInformation(),
        dto.getCity(),
        dto.getZipCode(),
        dto.getCountry());
    }
}
