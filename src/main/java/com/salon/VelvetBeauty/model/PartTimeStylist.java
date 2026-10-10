package com.salon.VelvetBeauty.model;

/**
 * Concrete implementation for Part-Time Salon Stylists.
 * Demonstrates: Inheritance from Stylist and Polymorphic hourly wage + service commission calculation.
 */
public class PartTimeStylist extends Stylist {
    private double hourlyRate;
    private int weeklyHours;
    private double serviceCommissionRate; // e.g. 0.20 (20% commission on billed revenue)

    public PartTimeStylist() {
        super();
        this.hourlyRate = 28.0;
        this.weeklyHours = 20;
        this.serviceCommissionRate = 0.20;
    }

    public PartTimeStylist(String id, String name, String specialty, String workingDays, 
                           String workingHours, String phone, String email, String imageUrl, 
                           String bio, double rating, boolean active, double hourlyRate, 
                           int weeklyHours, double serviceCommissionRate) {
        super(id, name, specialty, workingDays, workingHours, phone, email, imageUrl, bio, rating, active);
        this.hourlyRate = hourlyRate;
        this.weeklyHours = weeklyHours;
        this.serviceCommissionRate = serviceCommissionRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(int weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public double getServiceCommissionRate() {
        return serviceCommissionRate;
    }

    public void setServiceCommissionRate(double serviceCommissionRate) {
        this.serviceCommissionRate = serviceCommissionRate;
    }

    @Override
    public String displaySchedule() {
        return "Part-Time Flexible (" + weeklyHours + "h/wk): " + getWorkingHours() + " on [" + getWorkingDays() + "]. On-call weekend availability.";
    }

    @Override
    public double calculateMonthlyCompensation(int appointmentsCompleted, double revenueBroughtIn) {
        double basePay = hourlyRate * weeklyHours * 4; // 4 weeks approx per month
        double commission = revenueBroughtIn * serviceCommissionRate;
        return Math.round((basePay + commission) * 100.0) / 100.0;
    }

    @Override
    public String getEmploymentType() {
        return "Part-Time Visiting Stylist";
    }

    @Override
    public String toFileRecord() {
        // Format: ID;;NAME;;SPECIALTY;;WORKING_DAYS;;WORKING_HOURS;;PHONE;;EMAIL;;IMAGE_URL;;BIO;;RATING;;ACTIVE;;TYPE;;HOURLY_RATE;;WEEKLY_HOURS;;COMMISSION_RATE
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
                "PART_TIME",
                String.valueOf(hourlyRate),
                String.valueOf(weeklyHours),
                String.valueOf(serviceCommissionRate)
        );
    }
}
