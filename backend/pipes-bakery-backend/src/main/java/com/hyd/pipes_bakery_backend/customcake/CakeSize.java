package com.hyd.pipes_bakery_backend.customcake;

import java.math.BigDecimal;
import java.util.List;

/**
 * A real cake the bakery offers. {@code tierDiameters} lists its edible tiers bottom to top
 * (a single entry for one-tier cakes); {@code servings} may be null when not specified.
 */
public record CakeSize(
        String id,
        String label,
        String description,
        Integer servings,
        BigDecimal basePrice,
        List<Integer> tierDiameters
) {
}
