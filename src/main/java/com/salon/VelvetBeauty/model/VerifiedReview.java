package com.salon.VelvetBeauty.model;

/**
 * Concrete implementation for verified customer reviews linked to a completed appointment.
 * Demonstrates: Inheritance from Review and Polymorphic display presentation.
 */
public class VerifiedReview extends Review {
    private String appointmentId;

    public VerifiedReview() {
        super();
    }

    public VerifiedReview(String id, String customerId, String customerName, String serviceId, 
                          String serviceName, String stylistId, String stylistName, int rating, 
                          String comment, String status, String reviewDate, String appointmentId) {
        super(id, customerId, customerName, serviceId, serviceName, stylistId, stylistName, rating, comment, status, reviewDate);
        this.appointmentId = appointmentId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    @Override
    public boolean isVerified() {
        return true;
    }

    @Override
    public String getBadgeTitle() {
        return "Verified Client ✓";
    }

    @Override
    public String displayForUser(User viewingUser) {
        if (viewingUser instanceof Admin) {
            return "[ADMIN VIEW - VERIFIED VISIT] Appt Ref #" + appointmentId + " | Client: " 
                    + getCustomerName() + " | Stylist: " + getStylistName() + " | Rating: " 
                    + getRating() + "/5 | Status: " + getStatus() + " | Comment: \"" + getComment() + "\"";
        } else {
            return "★ " + getRating() + "/5 • \"" + getComment() + "\" — " + getCustomerName() 
                    + " [Verified Client ✓ • Stylist: " + getStylistName() + "]";
        }
    }

    @Override
    public String toFileRecord() {
        // Format: ID;;CUSTOMER_ID;;CUSTOMER_NAME;;SERVICE_ID;;SERVICE_NAME;;STYLIST_ID;;STYLIST_NAME;;RATING;;COMMENT;;STATUS;;DATE;;TYPE;;EXTRA
        return String.join(";;",
                getId() != null ? getId() : "",
                getCustomerId() != null ? getCustomerId() : "",
                getCustomerName() != null ? getCustomerName() : "",
                getServiceId() != null ? getServiceId() : "",
                getServiceName() != null ? getServiceName() : "",
                getStylistId() != null ? getStylistId() : "",
                getStylistName() != null ? getStylistName() : "",
                String.valueOf(getRating()),
                getComment() != null ? getComment() : "",
                getStatus() != null ? getStatus() : "APPROVED",
                getReviewDate() != null ? getReviewDate() : "",
                "VERIFIED",
                appointmentId != null ? appointmentId : ""
        );
    }
}
