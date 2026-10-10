package com.salon.VelvetBeauty.model;

import java.util.List;

/**
 * Abstract base class representing a salon service offering.
 * Demonstrates: Encapsulation (secured private fields) and polymorphic contract
 * for custom price calculations and service details presentation.
 */
public abstract class SalonService {
    private String id;
    private String name;
    private String category; // "HAIR", "SKIN", "NAIL"
    private double basePrice;
    private int durationMinutes;
    private String description;
    private String imageUrl;
    private boolean active;

    public SalonService() {
        this.active = true;
    }

    public SalonService(String id, String name, String category, double basePrice, 
                        int durationMinutes, String description, String imageUrl, boolean active) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.basePrice = basePrice;
        this.durationMinutes = durationMinutes;
        this.description = description;
        this.imageUrl = imageUrl;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Polymorphic method: displays comprehensive, category-specific service details.
     */
    public abstract String displayDetails();

    /**
     * Polymorphic method: calculates final price based on category-specific customizations
     * (e.g., hair length surcharge, skin serum grade, nail finish).
     */
    public abstract double calculateCustomizedPrice(String customizationOption);

    /**
     * Polymorphic method: returns label for customization dropdown (e.g., "Hair Length", "Serum Booster", "Nail Finish").
     */
    public abstract String getCustomizationLabel();

    /**
     * Polymorphic method: list of available customization options.
     */
    public abstract List<String> getAvailableCustomizations();

    /**
     * Serializes service data for services.txt persistence.
     */
    public abstract String toFileRecord();
}
