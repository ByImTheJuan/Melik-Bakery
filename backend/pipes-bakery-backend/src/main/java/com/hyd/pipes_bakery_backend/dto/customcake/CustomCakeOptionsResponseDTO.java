package com.hyd.pipes_bakery_backend.dto.customcake;

import java.math.BigDecimal;
import java.util.List;

import com.hyd.pipes_bakery_backend.customcake.CakeColor;
import com.hyd.pipes_bakery_backend.customcake.CakeExtra;
import com.hyd.pipes_bakery_backend.customcake.CakeFlavour;
import com.hyd.pipes_bakery_backend.customcake.CakeSize;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Opciones y reglas de precio del modulo de personalizacion de tortas.")
public record CustomCakeOptionsResponseDTO(
        List<CakeSize> sizes,
        List<CakeFlavour> flavours,
        List<CakeColor> colors,
        List<CakeExtra> extras,
        int maxDecorativeTiers,
        BigDecimal decorativeTierPrice,
        int maxTextLength,
        BigDecimal textPrice,
        BigDecimal imagePrice,
        int maxNotesLength,
        int maxDietaryLength
) {
}
