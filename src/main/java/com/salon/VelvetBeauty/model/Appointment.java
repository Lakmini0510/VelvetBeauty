package com.salon.VelvetBeauty.model;

/**
 * Encapsulated model representing a salon appointment reservation.
 * Demonstrates: Encapsulation (secured booking data, validation logic) and
 * integrates with polymorphic customer cancellation fee computation.
 */
public class Appointment {
    private String id;
    private String customerId;
    private String customerUsername;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String membershipType; // "REGULAR", "PREMIUM"
    private String serviceId;
    private String serviceName;
    private String serviceCategory;
    private String serviceCustomization;
    private String stylistId;
    private String stylistName;
    private String appointmentDate; // YYYY-MM-DD
    private String timeSlot; // e.g. "10:00 AM"
    private String status; // "BOOKED", "COMPLETED", "CANCELLED"
    private double basePrice;
    private double discountAmount;
    private double finalPrice;
    private double cancellationFee;
    private String cancellationReason;
    private String notes;
    private String createdAt;

    public Appointment() {
        this.status = "BOOKED";
        this.cancellationFee = 0.0;
    }

    public Appointment(String id, String customerId, String customerUsername, String customerName, 
                       String customerEmail, String customerPhone, String membershipType, 
                       String serviceId, String serviceName, String serviceCategory, 
                       String serviceCustomization, String stylistId, String stylistName, 
                       String appointmentDate, String timeSlot, String status, double basePrice, 
                       double discountAmount, double finalPrice, double cancellationFee, 
                       String cancellationReason, String notes, String createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerUsername = customerUsername;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.membershipType = membershipType;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceCategory = serviceCategory;
        this.serviceCustomization = serviceCustomization;
        this.stylistId = stylistId;
        this.stylistName = stylistName;
        this.appointmentDate = appointmentDate;
        this.timeSlot = timeSlot;
        this.status = status != null ? status : "BOOKED";
        this.basePrice = basePrice;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
        this.cancellationFee = cancellationFee;
        this.cancellationReason = cancellationReason;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters and Setters demonstrating encapsulation
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getServiceCustomization() {
        return serviceCustomization;
    }

    public void setServiceCustomization(String serviceCustomization) {
        this.serviceCustomization = serviceCustomization;
    }

    public String getStylistId() {
        return stylistId;
    }

    public void setStylistId(String stylistId) {
        this.stylistId = stylistId;
    }

    public String getStylistName() {
        return stylistName;
    }

    public void setStylistName(String stylistName) {
        this.stylistName = stylistName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Applies polymorphic cancellation fee rule according to customer membership.
     */
    public void applyCancellationPolicy(Customer customer, long hoursRemaining, String reason) {
        this.status = "CANCELLED";
        this.cancellationReason = reason != null ? reason : "Customer Request";
        if (customer != null) {
            this.cancellationFee = customer.calculateCancellationFee(this.finalPrice, hoursRemaining);
        } else {
            // Default policy if customer object not resolved
            this.cancellationFee = (hoursRemaining < 24) ? 4500.0 : 0.0;
        }
    }

    public String toFileRecord() {
        // Format: ID;;CUSTOMER_ID;;CUSTOMER_USER;;CUSTOMER_NAME;;CUSTOMER_EMAIL;;CUSTOMER_PHONE;;MEMBERSHIP;;SERVICE_ID;;SERVICE_NAME;;SERVICE_CAT;;CUSTOMIZATION;;STYLIST_ID;;STYLIST_NAME;;DATE;;TIME_SLOT;;STATUS;;BASE_PRICE;;DISCOUNT;;FINAL_PRICE;;CANCEL_FEE;;CANCEL_REASON;;NOTES;;CREATED_AT
        return String.join(";;",
                id != null ? id : "",
                customerId != null ? customerId : "",
                customerUsername != null ? customerUsername : "",
                customerName != null ? customerName : "",
                customerEmail != null ? customerEmail : "",
                customerPhone != null ? customerPhone : "",
                membershipType != null ? membershipType : "",
                serviceId != null ? serviceId : "",
                serviceName != null ? serviceName : "",
                serviceCategory != null ? serviceCategory : "",
                serviceCustomization != null ? serviceCustomization : "",
                stylistId != null ? stylistId : "",
                stylistName != null ? stylistName : "",
                appointmentDate != null ? appointmentDate : "",
                timeSlot != null ? timeSlot : "",
                status != null ? status : "BOOKED",
                String.valueOf(basePrice),
                String.valueOf(discountAmount),
                String.valueOf(finalPrice),
                String.valueOf(cancellationFee),
                cancellationReason != null ? cancellationReason : "",
                notes != null ? notes : "",
                createdAt != null ? createdAt : ""
        );
    }
}
