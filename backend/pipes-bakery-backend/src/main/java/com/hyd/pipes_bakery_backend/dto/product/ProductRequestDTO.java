package com.hyd.pipes_bakery_backend.dto.product;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Datos necesarios para crear o actualizar un producto.")
public class ProductRequestDTO {

    @Schema(description = "Nombre comercial del producto.", example = "Cinnamon Roll")
    @NotBlank(message = "Product name is required")
    private String name;
    @Schema(description = "Descripcion visible del producto.", example = "Roll suave de canela con glaseado de vainilla.")
    private String description;

    @Schema(description = "Precio del producto.", example = "4.50")
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;

    @Schema(description = "Lista de ingredientes principales.", example = "[\"harina\", \"canela\", \"azucar\"]")
    @NotNull
    @NotEmpty(message = "Ingredients list cannot be empty")
    private List<@NotBlank(message = "Elements in ingredients list cannot be blank") String> ingredients;

    @Schema(description = "Nombre del fichero de imagen del producto.", example = "cinnamonRoll.jpg")
    @NotBlank(message = "Image file is required")
    @Pattern(regexp = "^[A-Za-z0-9_-][A-Za-z0-9._-]*$", message = "Image file must be a plain file name")
    private String imageFile;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public BigDecimal getPrice() {
        return price;
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
    public String getImageFile() {
        return imageFile;
    }
    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
