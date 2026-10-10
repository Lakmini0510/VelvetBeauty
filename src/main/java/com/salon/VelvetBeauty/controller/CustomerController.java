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
    public String updateProfile(@RequestParam String email,
                                @RequestParam String phone,
                                @RequestParam(required = false) String password,
                                @RequestParam(required = false) String membershipType,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        boolean updated = userService.updateProfile(user.getId(), email, phone, password, membershipType);
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
