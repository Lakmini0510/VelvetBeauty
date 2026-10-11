package com.salon.VelvetBeauty.model;

/**
 * Concrete implementation for Full-Time Salon Stylists.
 * Demonstrates: Inheritance from Stylist and Polymorphic fixed salary + bonus calculations.
 */
public class FullTimeStylist extends Stylist {
    private double monthlyFixedSalary;
    private double performanceBonusRate; // e.g. 0.08 (8% commission bonus)
    private String benefitsPackage;

    public FullTimeStylist() {
        super();
        this.monthlyFixedSalary = 1140000.0;
        this.performanceBonusRate = 0.08;
        this.benefitsPackage = "Comprehensive Health Insurance, 401k Matching, Paid Vacation";
    }

    public FullTimeStylist(String id, String name, String specialty, String workingDays, 
                           String workingHours, String phone, String email, String imageUrl, 
                           String bio, double rating, boolean active, double monthlyFixedSalary, 
                           double performanceBonusRate, String benefitsPackage) {
        super(id, name, specialty, workingDays, workingHours, phone, email, imageUrl, bio, rating, active);
        this.monthlyFixedSalary = monthlyFixedSalary;
        this.performanceBonusRate = performanceBonusRate;
        this.benefitsPackage = benefitsPackage != null ? benefitsPackage : "Health, Dental & Paid Leave";
    }

    public double getMonthlyFixedSalary() {
        return monthlyFixedSalary;
    }

    public void setMonthlyFixedSalary(double monthlyFixedSalary) {
        this.monthlyFixedSalary = monthlyFixedSalary;
    }

    public double getPerformanceBonusRate() {
        return performanceBonusRate;
    }

    public void setPerformanceBonusRate(double performanceBonusRate) {
        this.performanceBonusRate = performanceBonusRate;
    }

    public String getBenefitsPackage() {
        return benefitsPackage;
    }

    public void setBenefitsPackage(String benefitsPackage) {
        this.benefitsPackage = benefitsPackage;
    }

    @Override
    public String displaySchedule() {
        return "Full-Time Resident (40h/wk): " + getWorkingHours() + " on [" + getWorkingDays() + "]. Guaranteed studio station.";
    }

    @Override
    public double calculateMonthlyCompensation(int appointmentsCompleted, double revenueBroughtIn) {
        double bonus = revenueBroughtIn * performanceBonusRate;
        return Math.round((monthlyFixedSalary + bonus) * 100.0) / 100.0;
    }

    @Override
    public String getEmploymentType() {
        return "Full-Time Resident Stylist";
    }

    @Override
    public String toFileRecord() {
        // Format: ID;;NAME;;SPECIALTY;;WORKING_DAYS;;WORKING_HOURS;;PHONE;;EMAIL;;IMAGE_URL;;BIO;;RATING;;ACTIVE;;TYPE;;FIXED_SALARY;;BONUS_RATE;;BENEFITS
        return String.join(";;",
                getId() != null ? getId() : "",
                getName() != null ? getName() : "",
                getSpecialty() != null ? getSpecialty() : "",
                getWorkingDays() != null ? getWorkingDays() : "",
                getWorkingHours() != null ? getWorkingHours() : "",
                getPhone() != null ? getPhone() : "",
                getEmail() != null ? getEmail() : "",
                getImageUrl() != null ? getImageUrl() : "",
                getBio() != null ? getBio() : "",
                String.valueOf(getRating()),
                String.valueOf(isActive()),
                "FULL_TIME",
                String.valueOf(monthlyFixedSalary),
                String.valueOf(performanceBonusRate),
                benefitsPackage != null ? benefitsPackage : ""
        );
    }
}
