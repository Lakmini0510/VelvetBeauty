package com.salon.VelvetBeauty.model;

import java.util.Arrays;
import java.util.List;

/**
 * Concrete service offering for Hair Styling & Treatments.
 * Demonstrates: Inheritance from SalonService and Polymorphic surcharge calculation for hair length.
 */
public class HairService extends SalonService {
    private String defaultHairLength; // "Short", "Medium", "Long", "Extra Long"

    public HairService() {
        super();
        setCategory("HAIR");
        this.defaultHairLength = "Medium";
    }

    public HairService(String id, String name, double basePrice, int durationMinutes, 
                       String description, String imageUrl, boolean active, String defaultHairLength) {
        super(id, name, "HAIR", basePrice, durationMinutes, description, imageUrl, active);
        this.defaultHairLength = defaultHairLength != null ? defaultHairLength : "Medium";
    }

    public String getDefaultHairLength() {
        return defaultHairLength;
    }

    public void setDefaultHairLength(String defaultHairLength) {
        this.defaultHairLength = defaultHairLength;
    }

    @Override
    public String displayDetails() {
        return "Hair Care Protocol: Consultation & Scalp Evaluation, Botanical Clarifying Wash, Precision Cut / Color Treatment, Argan Oil Thermal Shield Blowout. Length option: " + defaultHairLength;
    }

    @Override
    public double calculateCustomizedPrice(String customizationOption) {
        if (customizationOption == null) return getBasePrice();
        String opt = customizationOption.trim().toLowerCase();
        if (opt.contains("short")) {
            return getBasePrice(); // No surcharge
        } else if (opt.contains("medium")) {
            return getBasePrice() + 10.0; // +$10
        } else if (opt.contains("extra long")) {
            return getBasePrice() + 35.0; // +$35
        } else if (opt.contains("long")) {
            return getBasePrice() + 20.0; // +$20
        }
        return getBasePrice();
    }

    @Override
    public String getCustomizationLabel() {
        return "Hair Length Surcharge";
    }

    @Override
    public List<String> getAvailableCustomizations() {
        return Arrays.asList("Short (Standard)", "Medium (+ $10.00)", "Long (+ $20.00)", "Extra Long (+ $35.00)");
    }

    @Override
    public String toFileRecord() {
        // Format: ID;;NAME;;CATEGORY;;BASE_PRICE;;DURATION;;DESCRIPTION;;IMAGE_URL;;ACTIVE;;CUSTOM_PROP
        return String.join(";;",
                getId() != null ? getId() : "",
                getName() != null ? getName() : "",
                getCategory(),
                String.valueOf(getBasePrice()),
                String.valueOf(getDurationMinutes()),
                getDescription() != null ? getDescription() : "",
                getImageUrl() != null ? getImageUrl() : "",
                String.valueOf(isActive()),
                defaultHairLength != null ? defaultHairLength : "Medium"
        );
    }
}
