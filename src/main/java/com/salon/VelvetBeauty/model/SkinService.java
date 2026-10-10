package com.salon.VelvetBeauty.model;

import java.util.Arrays;
import java.util.List;

/**
 * Concrete service offering for Facials & Skin Therapy.
 * Demonstrates: Inheritance from SalonService and Polymorphic surcharge calculation for specialized skin serums.
 */
public class SkinService extends SalonService {
    private String recommendedSkinType; // "All Types", "Sensitive & Rosacea", "Dry & Dehydrated", "Oily & Acne-Prone"

    public SkinService() {
        super();
        setCategory("SKIN");
        this.recommendedSkinType = "All Types";
    }

    public SkinService(String id, String name, double basePrice, int durationMinutes, 
                       String description, String imageUrl, boolean active, String recommendedSkinType) {
        super(id, name, "SKIN", basePrice, durationMinutes, description, imageUrl, active);
        this.recommendedSkinType = recommendedSkinType != null ? recommendedSkinType : "All Types";
    }

    public String getRecommendedSkinType() {
        return recommendedSkinType;
    }

    public void setRecommendedSkinType(String recommendedSkinType) {
        this.recommendedSkinType = recommendedSkinType;
    }

    @Override
    public String displayDetails() {
        return "Dermal Spa Protocol: Ultrasonic Cleansing, Enzymatic Pore Clarification, Cold Steam Nutrient Infusion, Hydro-Jelly Masque, Peptide Barrier Finish. Best for: " + recommendedSkinType;
    }

    @Override
    public double calculateCustomizedPrice(String customizationOption) {
        if (customizationOption == null) return getBasePrice();
        String opt = customizationOption.trim().toLowerCase();
        if (opt.contains("standard")) {
            return getBasePrice();
        } else if (opt.contains("organic herbal")) {
            return getBasePrice() + 15.0; // +$15
        } else if (opt.contains("hyaluronic")) {
            return getBasePrice() + 25.0; // +$25
        } else if (opt.contains("24k gold")) {
            return getBasePrice() + 45.0; // +$45
        }
        return getBasePrice();
    }

    @Override
    public String getCustomizationLabel() {
        return "Serum & Treatment Booster";
    }

    @Override
    public List<String> getAvailableCustomizations() {
        return Arrays.asList("Standard Hydrating (Included)", "Organic Herbal Elixir (+ $15.00)", "Hyaluronic Glass Skin (+ $25.00)", "24K Gold Luxury Radiance (+ $45.00)");
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
                recommendedSkinType != null ? recommendedSkinType : "All Types"
        );
    }
}
