package com.salon.VelvetBeauty.service;

import com.salon.VelvetBeauty.model.PublicReview;
import com.salon.VelvetBeauty.model.Review;
import com.salon.VelvetBeauty.model.VerifiedReview;
import com.salon.VelvetBeauty.repository.AppointmentRepository;
import com.salon.VelvetBeauty.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing customer reviews, ratings, and admin moderation.
 * Demonstrates: Polymorphic handling of PublicReview and VerifiedReview.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;

    public ReviewService(ReviewRepository reviewRepository, AppointmentRepository appointmentRepository) {
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getApprovedReviews() {
        return reviewRepository.findApproved();
    }

    public List<Review> getReviewsByService(String serviceId) {
        return reviewRepository.findByServiceId(serviceId);
    }

    public List<Review> getReviewsByStylist(String stylistId) {
        return reviewRepository.findByStylistId(stylistId);
    }

    public Optional<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }

    /**
     * Submits a review. Automatically checks if customer has a COMPLETED appointment
     * to instantiate a VerifiedReview with trusted verification badge, otherwise PublicReview.
     */
    public Review submitReview(String customerId, String customerName, String serviceId, 
                               String serviceName, String stylistId, String stylistName, 
                               int rating, String comment, String appointmentId) {
        String id = "REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // Check if verified through completed appointment
        boolean isVerified = false;
        if (appointmentId != null && !appointmentId.trim().isEmpty()) {
            isVerified = appointmentRepository.findById(appointmentId.trim())
                    .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus()))
                    .isPresent();
        }

        Review review;
        if (isVerified) {
            review = new VerifiedReview(id, customerId, customerName, serviceId, serviceName, 
                    stylistId, stylistName, rating, comment, "APPROVED", date, appointmentId);
        } else {
            review = new PublicReview(id, customerId, customerName, serviceId, serviceName, 
                    stylistId, stylistName, rating, comment, "APPROVED", date);
        }

        reviewRepository.save(review);
        return review;
    }

    /**
     * Updates an existing review comment and rating.
     */
    public boolean updateReview(String id, int rating, String comment) {
        Optional<Review> revOpt = reviewRepository.findById(id);
        if (revOpt.isEmpty()) return false;

        Review review = revOpt.get();
        review.setRating(rating);
        review.setComment(comment);
        reviewRepository.save(review);
        return true;
    }

    /**
     * Moderates review status (APPROVED, REJECTED, PENDING).
     */
    public boolean moderateReview(String id, String status) {
        Optional<Review> revOpt = reviewRepository.findById(id);
        if (revOpt.isEmpty()) return false;

        Review review = revOpt.get();
        review.setStatus(status.toUpperCase());
        reviewRepository.save(review);
        return true;
    }

    /**
     * Deletes a review from reviews.txt.
     */
    public boolean deleteReview(String id) {
        return reviewRepository.deleteById(id);
    }

    public double getAverageRating() {
        List<Review> approved = getApprovedReviews();
        if (approved.isEmpty()) return 5.0;
        return Math.round(approved.stream().mapToInt(Review::getRating).average().orElse(5.0) * 10.0) / 10.0;
    }
}
