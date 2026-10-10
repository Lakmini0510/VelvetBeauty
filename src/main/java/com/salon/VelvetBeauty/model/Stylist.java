package com.salon.VelvetBeauty.model;

/**
 * Abstract base class representing a salon stylist or staff specialist.
 * Demonstrates: Encapsulation (private fields, protected constructors) and
 * polymorphic contracts for schedule display and salary/commission computation.
 */
public abstract class Stylist {
    private String id;
    private String name;
    private String specialty;
    private String workingDays; // comma separated: e.g. "Mon, Tue, Wed, Thu, Fri"
    private String workingHours; // e.g. "09:00 - 18:00"
    private String phone;
    private String email;
    private String imageUrl;
    private String bio;
    private double rating;
    private boolean active;

    public Stylist() {
        this.active = true;
        this.rating = 5.0;
    }

    public Stylist(String id, String name, String specialty, String workingDays, 
                   String workingHours, String phone, String email, String imageUrl, 
                   String bio, double rating, boolean active) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.workingDays = workingDays;
        this.workingHours = workingHours;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.rating = rating;
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(String workingDays) {
        this.workingDays = workingDays;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Polymorphic method: displays employment schedule and availability hours.
     */
    public abstract String displaySchedule();

    /**
     * Polymorphic method: computes monthly payroll based on employment agreement
     * (fixed salary + bonus for full-time, hourly wage + service commission for part-time).
     */
    public abstract double calculateMonthlyCompensation(int appointmentsCompleted, double revenueBroughtIn);

    /**
     * Returns the employment category (e.g. "Full-Time Staff" or "Part-Time Consultant").
     */
    public abstract String getEmploymentType();

    /**
     * Serializes stylist record for stylists.txt persistence.
     */
    public abstract String toFileRecord();
}
