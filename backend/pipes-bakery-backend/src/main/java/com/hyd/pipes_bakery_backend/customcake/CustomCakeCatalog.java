package com.hyd.pipes_bakery_backend.customcake;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 * Single source of truth for the options and prices of the cake personalization module.
 * The frontend renders whatever this catalog exposes and the server always prices a cake from it.
 */
@Component
public class CustomCakeCatalog {

    public static final int MAX_TEXT_LENGTH = 40;
    public static final int MAX_NOTES_LENGTH = 500;
    public static final int MAX_DIETARY_LENGTH = 200;

    /**
     * Decorative tiers are fake (non-edible) tiers, decorated like the cake, that go underneath it
     * so it looks bigger. They don't change the real cake chosen in the size field.
     */
    public static final int MAX_DECORATIVE_TIERS = 2;
    public static final BigDecimal DECORATIVE_TIER_PRICE = new BigDecimal("35000");
    public static final BigDecimal TEXT_PRICE = new BigDecimal("8000");
    public static final BigDecimal IMAGE_PRICE = new BigDecimal("18000");

    private static final List<CakeSize> SIZES = List.of(
            new CakeSize("S", "Pequeña", "15 cm", 8, new BigDecimal("95000"), List.of(15)),
            new CakeSize("M", "Mediana", "20 cm", 16, new BigDecimal("140000"), List.of(20)),
            new CakeSize("L", "Grande", "2 pisos de 20 y 15 cm", 24, new BigDecimal("230000"), List.of(20, 15))
    );

    private static final List<CakeFlavour> FLAVOURS = List.of(
            new CakeFlavour("vainilla", "Vainilla", BigDecimal.ZERO, "#f3dca2", "#fff6e3"),
            new CakeFlavour("chocolate", "Chocolate", BigDecimal.ZERO, "#5b3420", "#3a2014"),
            new CakeFlavour("red-velvet", "Red velvet", new BigDecimal("10000"), "#9e2a2b", "#fbf4ea"),
            new CakeFlavour("limon", "Limón", BigDecimal.ZERO, "#f1e08a", "#fff3b8"),
            new CakeFlavour("zanahoria", "Zanahoria", new BigDecimal("8000"), "#c98a4b", "#f7ecd9"),
            new CakeFlavour("arequipe", "Arequipe", new BigDecimal("8000"), "#e9c99f", "#b9783f")
    );

    private static final List<CakeColor> COLORS = List.of(
            new CakeColor("chantilly", "Crema chantilly", "#fbf5ea"),
            new CakeColor("vainilla", "Vainilla", "#f3e2bd"),
            new CakeColor("fresa", "Rosa fresa", "#eebcb8"),
            new CakeColor("caramelo", "Caramelo", "#d9a066"),
            new CakeColor("chocolate", "Chocolate", "#6b3f26"),
            new CakeColor("pistacho", "Pistacho", "#c9d3a0"),
            new CakeColor("lavanda", "Lavanda", "#d6c6dd")
    );

    private static final List<CakeExtra> EXTRAS = List.of(
            new CakeExtra("chispas", "Chispas", new BigDecimal("6000")),
            new CakeExtra("frutos-rojos", "Frutos rojos", new BigDecimal("20000")),
            new CakeExtra("macarons", "Macarons", new BigDecimal("22000")),
            new CakeExtra("goteo-chocolate", "Goteo de chocolate", new BigDecimal("12000")),
            new CakeExtra("flores", "Flores", new BigDecimal("18000")),
            new CakeExtra("velas", "Velas", new BigDecimal("5000")),
            new CakeExtra("hoja-oro", "Hoja de oro", new BigDecimal("15000"))
    );

    public List<CakeSize> getSizes() {
        return SIZES;
    }

    public List<CakeFlavour> getFlavours() {
        return FLAVOURS;
    }

    public List<CakeColor> getColors() {
        return COLORS;
    }

    public List<CakeExtra> getExtras() {
        return EXTRAS;
    }

    public Optional<CakeSize> findSize(String id) {
        return SIZES.stream().filter(size -> size.id().equals(id)).findFirst();
    }

    public Optional<CakeFlavour> findFlavour(String id) {
        return FLAVOURS.stream().filter(flavour -> flavour.id().equals(id)).findFirst();
    }

    public Optional<CakeColor> findColor(String id) {
        return COLORS.stream().filter(color -> color.id().equals(id)).findFirst();
    }

    public Optional<CakeExtra> findExtra(String id) {
        return EXTRAS.stream().filter(extra -> extra.id().equals(id)).findFirst();
    }
}
