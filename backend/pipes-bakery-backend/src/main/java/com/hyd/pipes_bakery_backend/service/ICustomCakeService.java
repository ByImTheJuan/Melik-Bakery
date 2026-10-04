package com.hyd.pipes_bakery_backend.service;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeConfigurationDTO;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeOptionsResponseDTO;

public interface ICustomCakeService {

    CustomCakeOptionsResponseDTO getOptions();

    /**
     * Validates a configuration against the catalog and resolves it into a snapshot with labels.
     *
     * @throws com.hyd.pipes_bakery_backend.exception.InvalidCustomCakeException if any choice is not valid
     */
    CustomCakeDetails resolve(CustomCakeConfigurationDTO configuration);

    /** Unit price of an already resolved cake, always computed from the server catalog. */
    BigDecimal price(CustomCakeDetails details);

    /** Stores the customer's photo for an edible print and returns its relative path. */
    String storeImage(MultipartFile file, String clientIp);
}
