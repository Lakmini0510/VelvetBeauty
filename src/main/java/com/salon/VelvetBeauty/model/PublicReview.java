package com.salon.VelvetBeauty.model;

/**
 * Concrete implementation for non-verified public reviews.
 * Demonstrates: Inheritance from Review and Polymorphic display presentation.
 */
public class PublicReview extends Review {

    public PublicReview() {
        super();
    }

    public PublicReview(String id, String customerId, String customerName, String serviceId, 
                        String serviceName, String stylistId, String stylistName, int rating, 
                        String comment, String status, String reviewDate) {
        super(id, customerId, customerName, serviceId, serviceName, stylistId, stylistName, rating, comment, status, reviewDate);
    }

    @Override
    public boolean isVerified() {
        return false;
    }

    @Override
    public String getBadgeTitle() {
        return "Public Guest Review";
    }

    @Override
    public String displayForUser(User viewingUser) {
        if (viewingUser instanceof Admin) {
            return "[ADMIN MODERATION VIEW] Review #" + getId() + " [Public User: " + getCustomerName() 
                    + "] - Status: " + getStatus() + " - Rating: " + getRating() + "/5 - Service: " 
                    + getServiceName() + " - Text: \"" + getComment() + "\"";
        } else {
            return "★ " + getRating() + "/5 • \"" + getComment() + "\" — " + getCustomerName() + " (" + getBadgeTitle() + ")";
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
                "PUBLIC",
                ""
        );
    }
}
