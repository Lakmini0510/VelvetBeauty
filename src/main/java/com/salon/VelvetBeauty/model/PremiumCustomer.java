package com.salon.VelvetBeauty.model;

/**
 * Concrete implementation for Premium VIP Customers.
 * Demonstrates: Inheritance from Customer and Polymorphic VIP discount & zero-fee cancellation policies.
 */
public class PremiumCustomer extends Customer {
    private String vipTier; // "GOLD", "PLATINUM", "DIAMOND"

    public PremiumCustomer() {
        super();
        setMembershipType("PREMIUM");
        this.vipTier = "GOLD";
    }

    public PremiumCustomer(String id, String username, String password, String fullName, 
                           String email, String phone, String createdAt, int loyaltyPoints, String vipTier) {
        super(id, username, password, fullName, email, phone, createdAt, "PREMIUM", loyaltyPoints);
        this.vipTier = (vipTier != null && !vipTier.isEmpty()) ? vipTier : "GOLD";
    }

    public String getVipTier() {
        return vipTier;
    }

    public void setVipTier(String vipTier) {
        this.vipTier = vipTier;
    }

    @Override
    public double getDiscountRate() {
        // Premium VIP 20% discount on all services
        return 0.20;
    }

    @Override
    public double applyDiscount(double basePrice) {
        return Math.round((basePrice * (1.0 - getDiscountRate())) * 100.0) / 100.0;
    }

    @Override
    public double calculateCancellationFee(double appointmentPrice, long hoursBeforeAppointment) {
        // Premium VIP customers enjoy 100% Free Cancellation at any time
        return 0.00;
    }

    @Override
    public String getMembershipPerks() {
        return "VIP " + vipTier + " Member: 20% Off All Services, 100% Free Cancellation Guarantee, Priority Stylist Matching, Complimentary Organic Beverages";
    }

    @Override
    public String getRoleDescription() {
        return "VIP Premium Client (" + vipTier + " Tier - 20% Discount)";
    }

    @Override
    public String toFileRecord() {
        // Format: ID;;USERNAME;;PASSWORD;;FULL_NAME;;EMAIL;;PHONE;;CREATED_AT;;MEMBERSHIP;;LOYALTY_POINTS;;VIP_TIER
        return String.join(";;",
                getId() != null ? getId() : "",
                getUsername() != null ? getUsername() : "",
                getPassword() != null ? getPassword() : "",
                getFullName() != null ? getFullName() : "",
                getEmail() != null ? getEmail() : "",
                getPhone() != null ? getPhone() : "",
                getCreatedAt() != null ? getCreatedAt() : "",
                getMembershipType(),
                String.valueOf(getLoyaltyPoints()),
                vipTier != null ? vipTier : "GOLD"
        );
    }
}
