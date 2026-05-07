package com.hyd.pipes_bakery_backend.controller;

import java.util.List;

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

import static com.hyd.pipes_bakery_backend.config.OpenApiConfig.SECURITY_SCHEME_NAME;
import com.hyd.pipes_bakery_backend.dto.product.ProductOrderRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductRequestDTO;
import com.hyd.pipes_bakery_backend.dto.product.ProductResponseDTO;
import com.hyd.pipes_bakery_backend.exception.ApiError;
import com.hyd.pipes_bakery_backend.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos", description = "Consulta publica de productos y gestion administrativa del catalogo.")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products
    @GetMapping
    @Operation(summary = "Listar productos", description = "Devuelve todos los productos disponibles en el catalogo.")
    @ApiResponse(responseCode = "200", description = "Listado de productos",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class))))
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    // GET /api/products/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Devuelve el detalle de un producto concreto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ProductResponseDTO getProductById(@Parameter(description = "ID del producto", example = "1") @NonNull @PathVariable Long id) {
        return productService.getProductById(id);
    }

    // POST /api/products
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto en el catalogo. Requiere JWT de administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o no valido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos de administrador",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ProductResponseDTO createProduct(@Valid @RequestBody ProductRequestDTO product) {
        return productService.createProduct(product);
    }

    // UPDATE
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente. Requiere JWT de administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada no validos",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o no valido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos de administrador",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ProductResponseDTO updateProduct(@Parameter(description = "ID del producto", example = "1") @NonNull @PathVariable Long id, @Valid @RequestBody ProductRequestDTO updatedProduct) {
        return productService.updateProduct(id, updatedProduct);
    }

    // DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catalogo. Requiere JWT de administrador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado", content = @Content),
            @ApiResponse(responseCode = "401", description = "Token ausente o no valido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos de administrador",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public void deleteProduct(@Parameter(description = "ID del producto", example = "1") @NonNull @PathVariable Long id) {
        productService.deleteProduct(id);
    }


    // PUT /api/products/order
    @PutMapping("/order")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = SECURITY_SCHEME_NAME)
    @Operation(
            summary = "Reordenar productos",
            description = "Persiste el orden completo en el que se muestran los productos del catalogo. Requiere JWT de administrador."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orden actualizado",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class)))),
            @ApiResponse(responseCode = "400", description = "El orden enviado no incluye todos los productos exactamente una vez",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Token ausente o no valido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permisos de administrador",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public List<ProductResponseDTO> updateProductOrder(@Valid @RequestBody ProductOrderRequestDTO request) {
        return productService.updateProductOrder(request.getProductIds());
    }
}
