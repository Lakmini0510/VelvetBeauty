package com.salon.VelvetBeauty.controller;

import com.salon.VelvetBeauty.model.Customer;
import com.salon.VelvetBeauty.model.User;
import com.salon.VelvetBeauty.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controller managing customer profile operations and membership upgrades.
 */
@Controller
public class CustomerController {

    private final UserService userService;

    public CustomerController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (user instanceof Customer customer) {
            Optional<Customer> freshOpt = userService.getCustomerById(customer.getId());
            freshOpt.ifPresent(fresh -> {
                session.setAttribute("currentUser", fresh);
                model.addAttribute("customer", fresh);
            });
        }
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String email,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String password,
                                @RequestParam(required = false) String membershipType,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        String cleanEmail = email != null ? email.trim() : "";
        String cleanPhone = phone != null ? phone.trim() : "";

        // 1. Email validation
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidEmail(cleanEmail)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a valid email address.");
            return "redirect:/profile";
        }
        if (userService.isEmailTaken(cleanEmail, user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "The email address '" + cleanEmail + "' is already in use by another account.");
            return "redirect:/profile";
        }

        // 2. Phone validation
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidPhone(cleanPhone)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a valid phone number with 9-15 digits.");
            return "redirect:/profile";
        }

        // 3. Password validation (if provided)
        if (password != null && !password.trim().isEmpty()) {
            if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidPassword(password)) {
                redirectAttributes.addFlashAttribute("errorMessage", "New password must be between 6 and 64 characters and cannot contain ';;'.");
                return "redirect:/profile";
            }
        }

        // 4. Membership type validation
        if (membershipType != null && !membershipType.trim().isEmpty()) {
            if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidMembershipType(membershipType)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid membership tier selected.");
                return "redirect:/profile";
            }
        }

        boolean updated = userService.updateProfile(user.getId(), cleanEmail, cleanPhone, password, membershipType);
        if (updated) {
            Optional<Customer> freshOpt = userService.getCustomerById(user.getId());
            freshOpt.ifPresent(fresh -> session.setAttribute("currentUser", fresh));
            redirectAttributes.addFlashAttribute("successMessage", "Your profile details have been updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile. Please try again.");
        }

        return "redirect:/profile";
    }
}
