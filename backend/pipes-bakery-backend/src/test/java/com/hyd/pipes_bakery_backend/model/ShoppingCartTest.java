package com.hyd.pipes_bakery_backend.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyd.pipes_bakery_backend.dto.customcake.CustomCakeDetails;
import com.hyd.pipes_bakery_backend.exception.ResourceNotFoundException;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

class ShoppingCartTest {

    @Test
    void shouldNeverMergeCustomCakesEvenWhenIdentical() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());

        cart.addItem(cake("line-1", 1));
        cart.addItem(cake("line-2", 1));

        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getItemsTotal()).isEqualByComparingTo(new BigDecimal("280000"));
    }

    @Test
    void shouldStillMergeCatalogProductsNextToCustomCakes() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());

        cart.addItem(cake("line-1", 1));
        cart.addItem(new CartItem(3L, "Brookie", 1, new BigDecimal("9000"), "brookie.jpg"));
        cart.addItem(new CartItem(3L, "Brookie", 2, new BigDecimal("9000"), "brookie.jpg"));

        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getItemByProductId(3L).getQuantity()).isEqualTo(3);
    }

    @Test
    void shouldIgnoreCustomCakesWhenLookingUpByProductId() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());
        cart.addItem(cake("line-1", 1));

        // Custom cakes carry productId 0 internally; they must never match a product lookup
        assertThat(cart.getItemByProductId(0L)).isNull();
    }

    @Test
    void shouldUpdateAndRemoveCustomCakesByLineId() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());
        cart.addItem(cake("line-1", 1));
        cart.addItem(cake("line-2", 1));

        cart.updateCustomCakeQuantity("line-2", 3);
        assertThat(cart.getItemByLineId("line-2").getQuantity()).isEqualTo(3);
        assertThat(cart.getItemsTotal()).isEqualByComparingTo(new BigDecimal("560000"));

        cart.removeCustomCake("line-1");
        assertThat(cart.getItems()).extracting(CartItem::getLineId).containsExactly("line-2");
    }

    @Test
    void shouldFailForUnknownLineIds() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());

        assertThatThrownBy(() -> cart.removeCustomCake("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> cart.updateCustomCakeQuantity("missing", 2))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldRoundTripCustomCakesThroughTheRedisSerializer() {
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());
        cart.addItem(cake("line-1", 2));

        ShoppingCart restored = (ShoppingCart) serializer.deserialize(serializer.serialize(cart));

        CartItem item = restored.getItems().get(0);
        assertThat(item.getType()).isEqualTo(CartItemType.CUSTOM_CAKE);
        assertThat(item.getLineId()).isEqualTo("line-1");
        assertThat(item.getCustomCake().getExtraLabels()).containsExactly("Velas");
        assertThat(restored.getItemsTotal()).isEqualByComparingTo(new BigDecimal("280000"));
    }

    @Test
    void shouldReadCartItemsStoredBeforeCustomCakesExisted() throws Exception {
        String legacyItem = """
                {"productId":3,"productName":"Brookie","quantity":1,"unitPriceAtAdd":9000,"productImage":"brookie.jpg"}
                """;

        CartItem item = new ObjectMapper().readValue(legacyItem, CartItem.class);

        assertThat(item.getType()).isEqualTo(CartItemType.PRODUCT);
        assertThat(item.isCustomCakeLine()).isFalse();
    }

    private CartItem cake(String lineId, int quantity) {
        CustomCakeDetails details = new CustomCakeDetails();
        details.setSizeId("M");
        details.setFlavourId("vainilla");
        details.setDecorativeTiers(0);
        details.setColorId("chantilly");
        details.setExtraIds(java.util.List.of("velas"));
        details.setExtraLabels(java.util.List.of("Velas"));
        return CartItem.customCake(lineId, "Torta personalizada", quantity, new BigDecimal("140000"), details);
    }
}
