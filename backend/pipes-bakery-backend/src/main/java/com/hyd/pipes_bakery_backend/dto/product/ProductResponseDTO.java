package com.hyd.pipes_bakery_backend.dto.product;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto devuelto por la API.")
public class ProductResponseDTO {
    @Schema(description = "Identificador unico del producto.", example = "3")
    private Long id;
    @Schema(description = "Nombre comercial del producto.", example = "Cinnamon Roll")
    private String name;
    @Schema(description = "Descripcion visible del producto.", example = "Roll suave de canela con glaseado de vainilla.")
    private String description;
    @Schema(description = "Precio del producto.", example = "4.50")
    private BigDecimal price;
    @Schema(description = "Ingredientes principales del producto.", example = "[\"harina\", \"canela\", \"azucar\"]")
    private List<String> ingredients;
    @Schema(description = "URL o ruta publica de la imagen del producto.", example = "/images/products/cinnamonRoll.jpg")
    private String imageUrl;
    @Schema(description = "Posicion del producto en el catalogo.", example = "2")
    private int displayOrder;

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(Long id, String name, String description, BigDecimal price, List<String> ingredients, String imageUrl) {
        this(id, name, description, price, ingredients, imageUrl, 0);
    }

    public ProductResponseDTO(Long id, String name, String description, BigDecimal price, List<String> ingredients, String imageUrl, int displayOrder) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }   
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public int getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
