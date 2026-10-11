package com.salon.VelvetBeauty.model;

/**
 * Concrete implementation for Regular Customers.
 * Demonstrates: Inheritance from Customer and Polymorphic discount / cancellation policies.
 */
public class RegularCustomer extends Customer {

    public RegularCustomer() {
        super();
        setMembershipType("REGULAR");
    }

    public RegularCustomer(String id, String username, String password, String fullName, 
                           String email, String phone, String createdAt, int loyaltyPoints) {
        super(id, username, password, fullName, email, phone, createdAt, "REGULAR", loyaltyPoints);
    }

    @Override
    public double getDiscountRate() {
        // Standard 5% member discount
        return 0.05;
    }

    @Override
    public double applyDiscount(double basePrice) {
        return Math.round((basePrice * (1.0 - getDiscountRate())) * 100.0) / 100.0;
    }

    @Override
    public double calculateCancellationFee(double appointmentPrice, long hoursBeforeAppointment) {
        // Regular customers incur a Rs. 4500 cancellation fee if cancelled within 24 hours
        if (hoursBeforeAppointment < 24) {
            return 4500.00;
        }
        return 0.00;
    }

    @Override
    public String getMembershipPerks() {
        return "Standard Salon Booking, 5% Seasonal Discount, Standard Cancellation (Free >24h, Rs. 4500 if <24h)";
    }

    @Override
    public String getRoleDescription() {
        return "Regular Salon Client (5% Discount Privileges)";
    }

    @Override
    public String toFileRecord() {
        // Format: ID;;USERNAME;;PASSWORD;;FULL_NAME;;EMAIL;;PHONE;;CREATED_AT;;MEMBERSHIP;;LOYALTY_POINTS
        return String.join(";;",
                getId() != null ? getId() : "",
                getUsername() != null ? getUsername() : "",
                getPassword() != null ? getPassword() : "",
                getFullName() != null ? getFullName() : "",
                getEmail() != null ? getEmail() : "",
                getPhone() != null ? getPhone() : "",
                getCreatedAt() != null ? getCreatedAt() : "",
                getMembershipType(),
                String.valueOf(getLoyaltyPoints())
        );
    }
}
