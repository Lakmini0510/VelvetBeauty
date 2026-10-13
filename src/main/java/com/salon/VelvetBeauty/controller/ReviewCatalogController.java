package com.salon.VelvetBeauty.controller;

import com.salon.VelvetBeauty.model.Appointment;
import com.salon.VelvetBeauty.model.Review;
import com.salon.VelvetBeauty.model.SalonService;
import com.salon.VelvetBeauty.model.Stylist;
import com.salon.VelvetBeauty.model.User;
import com.salon.VelvetBeauty.service.AppointmentService;
import com.salon.VelvetBeauty.service.ReviewService;
import com.salon.VelvetBeauty.service.SalonCatalogService;
import com.salon.VelvetBeauty.service.StylistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller for Customer reviews browsing, verified submission, and editing.
 */
@Controller
@RequestMapping("/reviews")
public class ReviewCatalogController {

    private final ReviewService reviewService;
    private final SalonCatalogService catalogService;
    private final StylistService stylistService;
    private final AppointmentService appointmentService;

    public ReviewCatalogController(ReviewService reviewService, 
                                   SalonCatalogService catalogService, 
                                   StylistService stylistService, 
                                   AppointmentService appointmentService) {
        this.reviewService = reviewService;
        this.catalogService = catalogService;
        this.stylistService = stylistService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String listReviews(@RequestParam(required = false) String serviceId,
                              @RequestParam(required = false) String stylistId,
                              Model model) {
        List<Review> reviews;
        if (serviceId != null && !serviceId.isEmpty()) {
            reviews = reviewService.getReviewsByService(serviceId);
        } else if (stylistId != null && !stylistId.isEmpty()) {
            reviews = reviewService.getReviewsByStylist(stylistId);
        } else {
            reviews = reviewService.getApprovedReviews();
        }

        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", reviewService.getAverageRating());
        model.addAttribute("services", catalogService.getAllServices());
        model.addAttribute("stylists", stylistService.getActiveStylists());
        model.addAttribute("selectedServiceId", serviceId);
        model.addAttribute("selectedStylistId", stylistId);

        return "reviews";
    }

    @GetMapping("/new")
    public String showReviewForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Appointment> completedAppts = appointmentService.getAppointmentsByCustomer(user.getId())
                .stream()
                .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus()))
                .toList();

        model.addAttribute("completedAppointments", completedAppts);
        model.addAttribute("services", catalogService.getAllServices());
        model.addAttribute("stylists", stylistService.getActiveStylists());
        return "submit-review";
    }

    @PostMapping("/new")
    public String handleSubmitReview(@RequestParam(required = false) String serviceId,
                                     @RequestParam(required = false) String stylistId,
                                     @RequestParam(required = false) Integer rating,
                                     @RequestParam(required = false) String comment,
                                     @RequestParam(required = false) String appointmentId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        // 1. Service must be selected
        if (serviceId == null || serviceId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select the service you experienced.");
            return "redirect:/reviews/new";
        }
        if (catalogService.getServiceById(serviceId.trim()).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The selected service was not found.");
            return "redirect:/reviews/new";
        }

        // 2. Stylist must be selected
        if (stylistId == null || stylistId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select the stylist you reviewed.");
            return "redirect:/reviews/new";
        }
        if (stylistService.getStylistById(stylistId.trim()).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "The selected stylist was not found.");
            return "redirect:/reviews/new";
        }

        // 3. Rating validation (1-5)
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidRating(rating)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a star rating between 1 and 5.");
            return "redirect:/reviews/new";
        }

        // 4. Comment validation
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidComment(comment)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Your feedback must be between 10 and 1000 characters and cannot contain ';;'.");
            return "redirect:/reviews/new";
        }

        Optional<SalonService> srvOpt = catalogService.getServiceById(serviceId.trim());
        Optional<Stylist> styOpt = stylistService.getStylistById(stylistId.trim());

        String serviceName = srvOpt.map(SalonService::getName).orElse("Salon Service");
        String stylistName = styOpt.map(Stylist::getName).orElse("Salon Stylist");

        Review review = reviewService.submitReview(
                user.getId(),
                user.getFullName(),
                serviceId.trim(),
                serviceName,
                stylistId.trim(),
                stylistName,
                rating,
                comment.trim(),
                appointmentId
        );

        if (review.isVerified()) {
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Thank you! Your verified client review with badge has been published.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Thank you! Your feedback has been received.");
        }

        return "redirect:/reviews";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Review> revOpt = reviewService.getReviewById(id);
        if (revOpt.isEmpty()) {
            return "redirect:/reviews";
        }

        Review review = revOpt.get();
        // Check ownership or admin
        if (!review.getCustomerId().equalsIgnoreCase(user.getId()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/reviews";
        }

        model.addAttribute("review", review);
        return "edit-review";
    }

    @PostMapping("/edit/{id}")
    public String handleEdit(@PathVariable String id,
                             @RequestParam(required = false) Integer rating,
                             @RequestParam(required = false) String comment,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidRating(rating)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a valid star rating between 1 and 5.");
            return "redirect:/reviews/edit/" + id;
        }

        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidComment(comment)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Feedback must be between 10 and 1000 characters and cannot contain ';;'.");
            return "redirect:/reviews/edit/" + id;
        }

        boolean updated = reviewService.updateReview(id, rating, comment.trim());
        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "Review updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update review.");
        }

        return "redirect:/reviews";
    }

    @PostMapping("/delete/{id}")
    public String handleDelete(@PathVariable String id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Review> revOpt = reviewService.getReviewById(id);
        if (revOpt.isPresent()) {
            Review r = revOpt.get();
            if (r.getCustomerId().equalsIgnoreCase(user.getId()) || "ADMIN".equalsIgnoreCase(user.getRole())) {
                reviewService.deleteReview(id);
                redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Unauthorized to delete this review.");
            }
        }

        return "redirect:/reviews";
    }
}
