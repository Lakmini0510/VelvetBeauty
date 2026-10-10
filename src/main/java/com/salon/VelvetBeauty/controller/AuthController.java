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
    public String handleLogin(@RequestParam String username, 
                              @RequestParam String password, 
                              HttpSession session, 
                              RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.authenticate(username, password);
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
    public String handleRegister(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String fullName,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 @RequestParam(defaultValue = "REGULAR") String membershipType,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (userService.isUsernameTaken(username)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username '" + username + "' is already taken.");
            return "redirect:/register";
        }

        Customer customer = userService.registerCustomer(username, password, fullName, email, phone, membershipType);
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
