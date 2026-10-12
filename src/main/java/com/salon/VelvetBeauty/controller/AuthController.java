package com.salon.VelvetBeauty.controller;

import com.salon.VelvetBeauty.model.Admin;
import com.salon.VelvetBeauty.model.Customer;
import com.salon.VelvetBeauty.model.User;
import com.salon.VelvetBeauty.service.AdminService;
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
 * Controller managing authentication, customer registration, and session state.
 */
@Controller
public class AuthController {

    private final UserService userService;
    private final AdminService adminService;

    public AuthController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session, Model model) {
        if (session.getAttribute("currentUser") != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user instanceof Admin) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/profile";
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam(required = false) String username, 
                              @RequestParam(required = false) String password, 
                              HttpSession session, 
                              RedirectAttributes redirectAttributes) {
        if (username == null || username.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please enter your username.");
            return "redirect:/login";
        }
        if (password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("enteredUsername", username.trim());
            redirectAttributes.addFlashAttribute("errorMessage", "Please enter your password.");
            return "redirect:/login";
        }
        if (com.salon.VelvetBeauty.util.ValidationUtil.containsDelimiter(username, password)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid characters detected in login credentials.");
            return "redirect:/login";
        }

        Optional<User> userOpt = userService.authenticate(username.trim(), password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("currentUser", user);

            if (user instanceof Admin admin) {
                adminService.logAction(admin.getUsername(), "LOGIN", "Logged into administrative console");
                redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + admin.getFullName() + "! (Admin Role: " + admin.getAdminRole() + ")");
                return "redirect:/admin/dashboard";
            } else {
                Customer customer = (Customer) user;
                redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + customer.getFullName() + "! Enjoy your " + customer.getMembershipType() + " salon benefits.");
                return "redirect:/appointments/my";
            }
        } else {
            redirectAttributes.addFlashAttribute("enteredUsername", username.trim());
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password. Please try again.");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/profile";
        }
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam(required = false) String username,
                                 @RequestParam(required = false) String password,
                                 @RequestParam(required = false) String fullName,
                                 @RequestParam(required = false) String email,
                                 @RequestParam(required = false) String phone,
                                 @RequestParam(defaultValue = "REGULAR") String membershipType,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String cleanUsername = username != null ? username.trim() : "";
        String cleanFullName = fullName != null ? fullName.trim() : "";
        String cleanEmail = email != null ? email.trim() : "";
        String cleanPhone = phone != null ? phone.trim() : "";
        String cleanTier = (membershipType != null && com.salon.VelvetBeauty.util.ValidationUtil.isValidMembershipType(membershipType))
                ? membershipType.trim().toUpperCase() : "REGULAR";

        // Preserve input fields in flash attributes so client does not lose input
        redirectAttributes.addFlashAttribute("enteredUsername", cleanUsername);
        redirectAttributes.addFlashAttribute("enteredFullName", cleanFullName);
        redirectAttributes.addFlashAttribute("enteredEmail", cleanEmail);
        redirectAttributes.addFlashAttribute("enteredPhone", cleanPhone);
        redirectAttributes.addFlashAttribute("enteredMembershipType", cleanTier);

        // 1. Username validation
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidUsername(cleanUsername)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Username must be 3-30 characters long and contain only letters, numbers, and underscores.");
            return "redirect:/register";
        }
        if (userService.isUsernameTaken(cleanUsername)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username '" + cleanUsername + "' is already taken. Please choose another.");
            return "redirect:/register";
        }

        // 2. Password validation
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidPassword(password)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Password must be between 6 and 64 characters and cannot contain reserved delimiter characters (;;).");
            return "redirect:/register";
        }

        // 3. Full Name validation
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidName(cleanFullName)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Please enter a valid legal name (2-60 alphabetic characters, spaces, hyphens, or apostrophes).");
            return "redirect:/register";
        }

        // 4. Email validation
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidEmail(cleanEmail)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Please enter a valid email address (e.g. name@example.com).");
            return "redirect:/register";
        }
        if (userService.isEmailTaken(cleanEmail)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "An account is already registered with email '" + cleanEmail + "'. Please sign in instead.");
            return "redirect:/register";
        }

        // 5. Phone validation
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidPhone(cleanPhone)) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Please provide a valid contact telephone number with 9-15 digits (e.g. +94 77 123 4567).");
            return "redirect:/register";
        }

        Customer customer = userService.registerCustomer(cleanUsername, password, cleanFullName, cleanEmail, cleanPhone, cleanTier);
        session.setAttribute("currentUser", customer);

        redirectAttributes.addFlashAttribute("successMessage", 
                "Account created successfully! Welcome to Velvet Beauty as a " + customer.getMembershipType() + " member.");
        return "redirect:/appointments/book";
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user instanceof Admin admin) {
            adminService.logAction(admin.getUsername(), "LOGOUT", "Admin logged out");
        }
        session.invalidate();
        redirectAttributes.addFlashAttribute("infoMessage", "You have been logged out securely. See you soon!");
        return "redirect:/login";
    }
}
