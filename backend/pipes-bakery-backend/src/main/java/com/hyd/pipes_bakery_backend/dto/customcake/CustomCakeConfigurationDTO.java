package com.hyd.pipes_bakery_backend.dto.customcake;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Elecciones del cliente en el modulo de personalizacion de tortas.")
public class CustomCakeConfigurationDTO {

    @Schema(description = "Indica si quien va a comer la torta tiene restricciones alimentarias.", example = "true")
    @NotNull(message = "Please tell us whether there are dietary restrictions")
    private Boolean hasDietaryRestrictions;

    @Schema(description = "Restricciones alimentarias, obligatorias si hasDietaryRestrictions es true.", example = "Sin gluten")
    @Size(max = 300, message = "Dietary restrictions are too long")
    private String dietaryRestrictions;

    @Schema(description = "Identificador del tamano (la torta real).", example = "M")
    @NotBlank(message = "Size is required")
    private String sizeId;

    @Schema(description = "Identificador del sabor.", example = "chocolate")
    @NotBlank(message = "Flavour is required")
    private String flavourId;

    @Schema(description = "Pisos decorativos (falsos, no comestibles) que se anaden debajo de la torta.", example = "1")
    private int decorativeTiers;

    @Schema(description = "Identificador del color de la cobertura.", example = "fresa")
    @NotBlank(message = "Color is required")
    private String colorId;

    @Schema(description = "Texto opcional escrito sobre la torta.", example = "Feliz cumpleanos Ana")
    @Size(max = 60, message = "Text is too long")
    private String text;

    @Schema(description = "Foto subida por el cliente para imprimir sobre la torta.", example = "custom-cakes/cake-1b2c3d4e.jpg")
    private String imageFile;

    @Schema(description = "Elementos decorativos adicionales.", example = "[\"chispas\", \"velas\"]")
    @Size(max = 10, message = "Too many decorative elements")
    private List<String> extraIds = new ArrayList<>();

    @Schema(description = "Notas del cliente para el pastelero.", example = "Por favor, letras en dorado")
    @Size(max = 600, message = "Notes are too long")
    private String notes;

    public CustomCakeConfigurationDTO() {
    }

    public Boolean getHasDietaryRestrictions() {
        return hasDietaryRestrictions;
    }

    public void setHasDietaryRestrictions(Boolean hasDietaryRestrictions) {
        this.hasDietaryRestrictions = hasDietaryRestrictions;
    }

    public String getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public void setDietaryRestrictions(String dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getFlavourId() {
        return flavourId;
    }

    public void setFlavourId(String flavourId) {
        this.flavourId = flavourId;
    }

    public int getDecorativeTiers() {
        return decorativeTiers;
    }

    public void setDecorativeTiers(int decorativeTiers) {
        this.decorativeTiers = decorativeTiers;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public List<String> getExtraIds() {
        return extraIds;
    }

    public void setExtraIds(List<String> extraIds) {
        this.extraIds = extraIds;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
