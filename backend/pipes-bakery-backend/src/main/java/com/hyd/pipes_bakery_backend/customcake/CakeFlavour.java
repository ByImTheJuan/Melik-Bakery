package com.hyd.pipes_bakery_backend.customcake;

import java.math.BigDecimal;

/** A sponge flavour; the colours are used by the frontend to paint the 3D cut slice. */
public record CakeFlavour(String id, String label, BigDecimal price, String spongeColor, String fillingColor) {
}
