package com.salon.VelvetBeauty.model;

/**
 * Abstract Customer class extending User.
 * Demonstrates: Inheritance from User, and defines polymorphic contracts
 * for discount calculations and cancellation fee policies.
 */
public abstract class Customer extends User {
    private String membershipType; // "REGULAR" or "PREMIUM"
    private int loyaltyPoints;

    public Customer() {
        super();
        setRole("CUSTOMER");
    }

    public Customer(String id, String username, String password, String fullName, 
                    String email, String phone, String createdAt, String membershipType, int loyaltyPoints) {
        super(id, username, password, fullName, email, phone, "CUSTOMER", createdAt);
        this.membershipType = membershipType;
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    /**
     * Polymorphic method: Returns customer's discount percentage.
     */
    public abstract double getDiscountRate();

    /**
     * Polymorphic method: Computes final price after customer discount.
     */
    public abstract double applyDiscount(double basePrice);

    /**
     * Polymorphic method: Different cancellation fee rules for regular vs premium customers.
     * @param appointmentPrice the original price of the appointment
     * @param hoursBeforeAppointment hours remaining before the appointment
     * @return cancellation fee amount
     */
    public abstract double calculateCancellationFee(double appointmentPrice, long hoursBeforeAppointment);

    /**
     * Polymorphic perk summary.
     */
    public abstract String getMembershipPerks();
}
