package com.salon.VelvetBeauty.model;

import java.util.Arrays;
import java.util.List;

/**
 * Concrete service offering for Manicure & Pedicure.
 * Demonstrates: Inheritance from SalonService and Polymorphic surcharge calculation for nail finishes.
 */
public class NailService extends SalonService {
    private String defaultFinish; // "Classic", "Gel", "Acrylic", "Nail Art"

    public NailService() {
        super();
        setCategory("NAIL");
        this.defaultFinish = "Classic";
    }

    public NailService(String id, String name, double basePrice, int durationMinutes, 
                       String description, String imageUrl, boolean active, String defaultFinish) {
        super(id, name, "NAIL", basePrice, durationMinutes, description, imageUrl, active);
        this.defaultFinish = defaultFinish != null ? defaultFinish : "Classic";
    }

    public String getDefaultFinish() {
        return defaultFinish;
    }

    public void setDefaultFinish(String defaultFinish) {
        this.defaultFinish = defaultFinish;
    }

    @Override
    public String displayDetails() {
        return "Nail Spa Protocol: Dead Sea Salt Soak, Precision Cuticle Grooming, Shea Butter Hand Massage, Non-Toxic Strengthening Base, UV LED High-Gloss Curing. Style: " + defaultFinish;
    }

    @Override
    public double calculateCustomizedPrice(String customizationOption) {
        if (customizationOption == null) return getBasePrice();
        String opt = customizationOption.trim().toLowerCase();
        if (opt.contains("classic")) {
            return getBasePrice();
        } else if (opt.contains("gel")) {
            return getBasePrice() + 3600.0; // +Rs. 3600
        } else if (opt.contains("acrylic")) {
            return getBasePrice() + 7500.0; // +Rs. 7500
        } else if (opt.contains("nail art") || opt.contains("art")) {
            return getBasePrice() + 10500.0; // +Rs. 10500
        }
        return getBasePrice();
    }

    @Override
    public String getCustomizationLabel() {
        return "Nail Finish & Art Option";
    }

    @Override
    public List<String> getAvailableCustomizations() {
        return Arrays.asList("Classic High-Gloss (Included)", "Long-Wear Gel Polish (+ Rs. 3600.00)", "Acrylic Sculpted Extensions (+ Rs. 7500.00)", "Hand-Painted Designer Nail Art (+ Rs. 10500.00)");
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
                defaultFinish != null ? defaultFinish : "Classic"
        );
    }
}
