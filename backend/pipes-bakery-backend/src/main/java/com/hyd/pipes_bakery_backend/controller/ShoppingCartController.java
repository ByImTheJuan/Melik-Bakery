package com.hyd.pipes_bakery_backend.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hyd.pipes_bakery_backend.dto.order.CheckoutOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.order.OrderResponseDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.AddCartItemRequestDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.ShoppingCartResponseDTO;
import com.hyd.pipes_bakery_backend.dto.shoppingCart.UpdateCartItemQuantityRequestDTO;
import com.hyd.pipes_bakery_backend.exception.ApiError;
import com.hyd.pipes_bakery_backend.service.OrderService;
import com.hyd.pipes_bakery_backend.service.ShoppingCartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Carrito", description = "Creacion, consulta y modificacion del carrito de compra.")
public class ShoppingCartController {

    private final ShoppingCartService cartService;
    private final OrderService orderService;

    public ShoppingCartController(ShoppingCartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    //POST /api/cart
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear carrito", description = "Crea un carrito vacio y devuelve su identificador.")
    @ApiResponse(responseCode = "201", description = "Carrito creado",
            content = @Content(schema = @Schema(implementation = ShoppingCartResponseDTO.class)))
    public ShoppingCartResponseDTO createCart() {
        System.out.println("Creating new cart (controller)...");
        return cartService.createCart();
    }

    //GET /api/cart/{cartId}
    @GetMapping("/{cartId}")
    @Operation(summary = "Obtener carrito", description = "Devuelve el estado actual de un carrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito encontrado",
                    content = @Content(schema = @Schema(implementation = ShoppingCartResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ShoppingCartResponseDTO getCart(@Parameter(description = "UUID del carrito", example = "f4a9b6de-0c5d-4cb2-9a47-8dc413951f0f") @PathVariable UUID cartId) {
        return cartService.getCartById(cartId);
    }

    //POST /api/cart/{cartId}/items
    @PostMapping("/{cartId}/items")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Anadir producto al carrito", description = "Anade un producto al carrito o incrementa su cantidad.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito actualizado",
                    content = @Content(schema = @Schema(implementation = ShoppingCartResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ShoppingCartResponseDTO addItem(
            @Parameter(description = "UUID del carrito", example = "f4a9b6de-0c5d-4cb2-9a47-8dc413951f0f") @PathVariable UUID cartId,
            @NonNull @RequestBody @Valid AddCartItemRequestDTO request) {

        return cartService.addItem(cartId, request);
    }

    //PUT /api/cart/{cartId}/items/{productId}
    @PutMapping("/{cartId}/items/{productId}") 
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar cantidad de producto", description = "Cambia la cantidad de un producto dentro del carrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrito actualizado",
                    content = @Content(schema = @Schema(implementation = ShoppingCartResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ShoppingCartResponseDTO updateItemQuantity(
            @Parameter(description = "UUID del carrito", example = "f4a9b6de-0c5d-4cb2-9a47-8dc413951f0f") @PathVariable UUID cartId,
            @Parameter(description = "ID del producto", example = "3") @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemQuantityRequestDTO quantity) {

        return cartService.updateItemQuantity(cartId, productId, quantity);
    }


    //CHECKOUT /api/cart/{cartId}/checkout
    @PostMapping("/{cartId}/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Confirmar pedido", description = "Convierte un carrito en un pedido usando los datos de envio proporcionados.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado",
                    content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos o carrito vacio",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public OrderResponseDTO checkout(
            @Parameter(description = "UUID del carrito", example = "f4a9b6de-0c5d-4cb2-9a47-8dc413951f0f") @PathVariable UUID cartId,
            @RequestBody @Valid CheckoutOrderRequestDTO dto)   {

        return orderService.checkout(cartId, dto);
    }

    //DELETE /api/cart/{cartId}/items/{productId}
    @DeleteMapping("/{cartId}/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar producto del carrito", description = "Quita una linea de producto del carrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado del carrito", content = @Content),
            @ApiResponse(responseCode = "404", description = "Carrito o producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public void removeItem(
            @Parameter(description = "UUID del carrito", example = "f4a9b6de-0c5d-4cb2-9a47-8dc413951f0f") @PathVariable UUID cartId,
            @Parameter(description = "ID del producto", example = "3") @PathVariable Long productId) {

        cartService.removeItem(cartId, productId);
    }

    //DELETE /api/cart/{cartId}
    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos de un carrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrito vaciado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public void clearCart(@Parameter(description = "UUID del carrito", example = "f4a9b6de-0c5d-4cb2-9a47-8dc413951f0f") @PathVariable UUID cartId) {
        cartService.clearCart(cartId);
    }
}
