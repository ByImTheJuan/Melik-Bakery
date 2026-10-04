package com.hyd.pipes_bakery_backend.customcake;

import java.math.BigDecimal;

/** An optional decorative element placed on the cake. */
public record CakeExtra(String id, String label, BigDecimal price) {
}
