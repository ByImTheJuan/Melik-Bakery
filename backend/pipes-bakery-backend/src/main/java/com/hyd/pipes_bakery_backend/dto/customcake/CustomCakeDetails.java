package com.hyd.pipes_bakery_backend.dto.customcake;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Validated snapshot of a personalized cake: the chosen ids plus their labels at the time
 * it was added to the cart. Travels with the cart line, the checkout snapshot and the order item.
 * Lists must stay mutable (ArrayList) because carts are stored in Redis with type information.
 * Unknown properties are ignored so snapshots written by older versions can still be read.
 */
@Schema(description = "Detalle de una torta personalizada.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomCakeDetails {

    private boolean hasDietaryRestrictions;
    private String dietaryRestrictions;
    private String sizeId;
    private String sizeLabel;
    private String sizeDescription;
    private Integer servings;
    private List<Integer> tierDiameters = new ArrayList<>();
    private String flavourId;
    private String flavourLabel;
    private int decorativeTiers;
    private String colorId;
    private String colorLabel;
    private String colorHex;
    private String text;
    private String imageFile;
    private List<String> extraIds = new ArrayList<>();
    private List<String> extraLabels = new ArrayList<>();
    private String notes;

    public CustomCakeDetails() {
    }

    public boolean isHasDietaryRestrictions() {
        return hasDietaryRestrictions;
    }

    public void setHasDietaryRestrictions(boolean hasDietaryRestrictions) {
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

    public String getSizeLabel() {
        return sizeLabel;
    }

    public void setSizeLabel(String sizeLabel) {
        this.sizeLabel = sizeLabel;
    }

    public String getSizeDescription() {
        return sizeDescription;
    }

    public void setSizeDescription(String sizeDescription) {
        this.sizeDescription = sizeDescription;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public List<Integer> getTierDiameters() {
        return tierDiameters;
    }

    public void setTierDiameters(List<Integer> tierDiameters) {
        this.tierDiameters = tierDiameters == null ? new ArrayList<>() : new ArrayList<>(tierDiameters);
    }

    public String getFlavourId() {
        return flavourId;
    }

    public void setFlavourId(String flavourId) {
        this.flavourId = flavourId;
    }

    public String getFlavourLabel() {
        return flavourLabel;
    }

    public void setFlavourLabel(String flavourLabel) {
        this.flavourLabel = flavourLabel;
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

    public String getColorLabel() {
        return colorLabel;
    }

    public void setColorLabel(String colorLabel) {
        this.colorLabel = colorLabel;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
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
        this.extraIds = extraIds == null ? new ArrayList<>() : new ArrayList<>(extraIds);
    }

    public List<String> getExtraLabels() {
        return extraLabels;
    }

    public void setExtraLabels(List<String> extraLabels) {
        this.extraLabels = extraLabels == null ? new ArrayList<>() : new ArrayList<>(extraLabels);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
