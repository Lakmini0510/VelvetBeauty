package com.salon.VelvetBeauty.model;

/**
 * Abstract base class representing customer feedback and reviews.
 * Demonstrates: Encapsulation (secured review fields) and Polymorphic display presentation
 * differing between administrative and regular customer viewers.
 */
public abstract class Review {
    private String id;
    private String customerId;
    private String customerName;
    private String serviceId;
    private String serviceName;
    private String stylistId;
    private String stylistName;
    private int rating; // 1 to 5
    private String comment;
    private String status; // "APPROVED", "PENDING", "REJECTED"
    private String reviewDate;

    public Review() {
        this.status = "APPROVED";
        this.rating = 5;
    }

    public Review(String id, String customerId, String customerName, String serviceId, 
                  String serviceName, String stylistId, String stylistName, int rating, 
                  String comment, String status, String reviewDate) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.stylistId = stylistId;
        this.stylistName = stylistName;
        this.rating = rating;
        this.comment = comment;
        this.status = status != null ? status : "APPROVED";
        this.reviewDate = reviewDate;
    }

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getStarsDisplay() {
        int r = Math.max(1, Math.min(5, this.rating));
        return "★".repeat(r) + "☆".repeat(5 - r);
    }

    /**
     * Polymorphic method: displays tailored review information based on the viewer's role
     * (Admin sees moderation metadata & status controls; Customer sees formatted rating & badge).
     */
    public abstract String displayForUser(User viewingUser);

    /**
     * Polymorphic verification status.
     */
    public abstract boolean isVerified();

    /**
     * Polymorphic badge text.
     */
    public abstract String getBadgeTitle();

    /**
     * Serialization format for reviews.txt
     */
    public abstract String toFileRecord();
}
