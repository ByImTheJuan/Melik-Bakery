package com.hyd.pipes_bakery_backend.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Stores the details of a personalized cake as JSON text in the order_items table. */
@Converter
public class CustomCakeDetailsConverter implements AttributeConverter<CustomCakeDetails, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String convertToDatabaseColumn(CustomCakeDetails details) {
        if (details == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(details);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize custom cake details", ex);
        }
    }

    @Override
    public CustomCakeDetails convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, CustomCakeDetails.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not read custom cake details", ex);
        }
    }
}
