package com.salon.VelvetBeauty.controller;

import com.salon.VelvetBeauty.model.*;
import com.salon.VelvetBeauty.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller managing administrative operations across all 6 salon components.
 * Demonstrates: OOP Abstraction (admin-only operations hidden from clients, role permissions).
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;
    private final SalonCatalogService catalogService;
    private final StylistService stylistService;
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;

    public AdminController(UserService userService, 
                           AdminService adminService, 
                           SalonCatalogService catalogService, 
                           StylistService stylistService, 
                           AppointmentService appointmentService, 
                           ReviewService reviewService) {
        this.userService = userService;
        this.adminService = adminService;
        this.catalogService = catalogService;
        this.stylistService = stylistService;
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
    }

    private Admin verifyAdmin(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user instanceof Admin admin) {
            return admin;
        }
        return null;
    }

    // ==========================================
    // 1. DASHBOARD & OVERVIEW
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: Administrative credentials required.");
            return "redirect:/login";
        }

        List<Customer> customers = userService.getAllCustomers();
        List<Appointment> appointments = appointmentService.getAllAppointments();
        List<SalonService> services = catalogService.getAllServices();
        List<Stylist> stylists = stylistService.getAllStylists();
        List<Review> reviews = reviewService.getAllReviews();

        double totalRevenue = appointments.stream()
                .filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus()))
                .mapToDouble(Appointment::getFinalPrice)
                .sum();

        long pendingReviews = reviews.stream()
                .filter(r -> "PENDING".equalsIgnoreCase(r.getStatus()))
                .count();

        model.addAttribute("admin", admin);
        model.addAttribute("totalCustomers", customers.size());
        model.addAttribute("totalAppointments", appointments.size());
        model.addAttribute("totalServices", services.size());
        model.addAttribute("totalStylists", stylists.size());
        model.addAttribute("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
        model.addAttribute("pendingReviews", pendingReviews);
        model.addAttribute("recentAppointments", appointments.stream().limit(5).toList());
        model.addAttribute("recentLogs", adminService.getActivityLogs().stream().limit(6).toList());

        return "admin/dashboard";
    }

    // ==========================================
    // 2. CUSTOMER MANAGEMENT (Component 01)
    // ==========================================
    @GetMapping("/customers")
    public String listCustomers(@RequestParam(required = false) String search,
                                HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        List<Customer> customers = userService.getAllCustomers();
        if (search != null && !search.trim().isEmpty()) {
            String q = search.trim().toLowerCase();
            customers = customers.stream()
                    .filter(c -> c.getUsername().toLowerCase().contains(q) 
                            || c.getFullName().toLowerCase().contains(q)
                            || c.getEmail().toLowerCase().contains(q)
                            || c.getId().toLowerCase().contains(q))
                    .toList();
        }

        model.addAttribute("customers", customers);
        model.addAttribute("searchQuery", search != null ? search : "");
        return "admin/customers";
    }

    @PostMapping("/customers/upgrade/{id}")
    public String toggleCustomerMembership(@PathVariable String id,
                                          @RequestParam String membershipType,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        Optional<Customer> custOpt = userService.getCustomerById(id);
        if (custOpt.isPresent()) {
            Customer c = custOpt.get();
            userService.updateProfile(id, c.getEmail(), c.getPhone(), c.getPassword(), membershipType);
            adminService.logAction(admin.getUsername(), "MEMBERSHIP_UPDATE", 
                    "Updated customer " + c.getUsername() + " membership to " + membershipType);
            redirectAttributes.addFlashAttribute("successMessage", "Membership updated to " + membershipType);
        }

        return "redirect:/admin/customers";
    }

    @PostMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable String id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        if (!admin.canManageUsers()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Salon Owner privilege required to delete customers.");
            return "redirect:/admin/customers";
        }

        boolean deleted = userService.deleteCustomer(id);
        if (deleted) {
            adminService.logAction(admin.getUsername(), "DELETE_CUSTOMER", "Deleted customer account ID: " + id);
            redirectAttributes.addFlashAttribute("successMessage", "Customer account removed successfully.");
        }
        return "redirect:/admin/customers";
    }

    // ==========================================
    // 3. SERVICE MANAGEMENT (Component 02)
    // ==========================================
    @GetMapping("/services")
    public String manageServices(HttpSession session, Model model) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("services", catalogService.getAllServices());
        return "admin/services";
    }

    @GetMapping("/services/new")
    public String newServiceForm(HttpSession session, Model model) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        return "admin/service-form";
    }

    @PostMapping("/services/new")
    public String createService(@RequestParam String name,
                                @RequestParam String category,
                                @RequestParam double basePrice,
                                @RequestParam int durationMinutes,
                                @RequestParam String description,
                                @RequestParam(required = false) String imageUrl,
                                @RequestParam(required = false) String customProp,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        SalonService s = catalogService.createService(name, category, basePrice, durationMinutes, 
                description, imageUrl, customProp);
        adminService.logAction(admin.getUsername(), "CREATE_SERVICE", "Added new service: " + s.getName());

        redirectAttributes.addFlashAttribute("successMessage", "Service '" + s.getName() + "' created successfully!");
        return "redirect:/admin/services";
    }

    @GetMapping("/services/edit/{id}")
    public String editServiceForm(@PathVariable String id, HttpSession session, Model model) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        Optional<SalonService> srvOpt = catalogService.getServiceById(id);
        if (srvOpt.isEmpty()) return "redirect:/admin/services";

        model.addAttribute("service", srvOpt.get());
        return "admin/service-edit";
    }

    @PostMapping("/services/edit/{id}")
    public String updateService(@PathVariable String id,
                                @RequestParam String name,
                                @RequestParam double basePrice,
                                @RequestParam int durationMinutes,
                                @RequestParam String description,
                                @RequestParam(required = false) String imageUrl,
                                @RequestParam(defaultValue = "true") boolean active,
                                @RequestParam(required = false) String customProp,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        catalogService.updateService(id, name, basePrice, durationMinutes, description, imageUrl, active, customProp);
        adminService.logAction(admin.getUsername(), "UPDATE_SERVICE", "Updated service ID: " + id);

        redirectAttributes.addFlashAttribute("successMessage", "Service updated successfully!");
        return "redirect:/admin/services";
    }

    @PostMapping("/services/delete/{id}")
    public String deleteService(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        catalogService.deleteService(id);
        adminService.logAction(admin.getUsername(), "DELETE_SERVICE", "Deleted service ID: " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Service removed successfully.");
        return "redirect:/admin/services";
    }

    // ==========================================
    // 4. STYLIST MANAGEMENT (Component 04)
    // ==========================================
    @GetMapping("/stylists")
    public String manageStylists(HttpSession session, Model model) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        List<Stylist> stylists = stylistService.getAllStylists();
        model.addAttribute("stylists", stylists);
        return "admin/stylists";
    }

    @GetMapping("/stylists/new")
    public String newStylistForm(HttpSession session) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";
        return "admin/stylist-form";
    }

    @PostMapping("/stylists/new")
    public String createStylist(@RequestParam String name,
                                @RequestParam String specialty,
                                @RequestParam String workingDays,
                                @RequestParam String workingHours,
                                @RequestParam String phone,
                                @RequestParam String email,
                                @RequestParam(required = false) String imageUrl,
                                @RequestParam String bio,
                                @RequestParam String type,
                                @RequestParam double rateOrSalary,
                                @RequestParam double commissionOrBonus,
                                @RequestParam(required = false) String extraParam,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        Stylist s = stylistService.createStylist(name, specialty, workingDays, workingHours, phone, email, 
                imageUrl, bio, type, rateOrSalary, commissionOrBonus, extraParam);
        adminService.logAction(admin.getUsername(), "CREATE_STYLIST", "Added stylist: " + s.getName() + " (" + type + ")");

        redirectAttributes.addFlashAttribute("successMessage", "Stylist '" + s.getName() + "' onboarded successfully!");
        return "redirect:/admin/stylists";
    }

    @GetMapping("/stylists/edit/{id}")
    public String editStylistForm(@PathVariable String id, HttpSession session, Model model) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        Optional<Stylist> sOpt = stylistService.getStylistById(id);
        if (sOpt.isEmpty()) return "redirect:/admin/stylists";

        model.addAttribute("stylist", sOpt.get());
        return "admin/stylist-edit";
    }

    @PostMapping("/stylists/edit/{id}")
    public String updateStylist(@PathVariable String id,
                                @RequestParam String name,
                                @RequestParam String specialty,
                                @RequestParam String workingDays,
                                @RequestParam String workingHours,
                                @RequestParam String phone,
                                @RequestParam String email,
                                @RequestParam String bio,
                                @RequestParam(required = false) String imageUrl,
                                @RequestParam(defaultValue = "true") boolean active,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        stylistService.updateStylist(id, name, specialty, workingDays, workingHours, phone, email, bio, imageUrl, active);
        adminService.logAction(admin.getUsername(), "UPDATE_STYLIST", "Updated stylist ID: " + id);

        redirectAttributes.addFlashAttribute("successMessage", "Stylist profile updated!");
        return "redirect:/admin/stylists";
    }

    @PostMapping("/stylists/delete/{id}")
    public String deleteStylist(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        stylistService.deleteStylist(id);
        adminService.logAction(admin.getUsername(), "DELETE_STYLIST", "Removed stylist ID: " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Stylist record removed.");
        return "redirect:/admin/stylists";
    }

    // ==========================================
    // 5. APPOINTMENT MANAGEMENT (Component 03)
    // ==========================================
    @GetMapping("/appointments")
    public String manageAppointments(@RequestParam(required = false) String status,
                                     HttpSession session, Model model) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        List<Appointment> all = appointmentService.getAllAppointments();
        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            all = all.stream().filter(a -> status.equalsIgnoreCase(a.getStatus())).toList();
        }

        model.addAttribute("appointments", all);
        model.addAttribute("selectedStatus", status != null ? status : "ALL");
        return "admin/appointments";
    }

    @PostMapping("/appointments/status/{id}")
    public String updateAppointmentStatus(@PathVariable String id,
                                          @RequestParam String status,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        appointmentService.updateStatus(id, status);
        adminService.logAction(admin.getUsername(), "APPOINTMENT_STATUS", 
                "Changed appointment " + id + " status to " + status);

        redirectAttributes.addFlashAttribute("successMessage", "Appointment " + id + " marked as " + status);
        return "redirect:/admin/appointments";
    }

    @PostMapping("/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        appointmentService.deleteAppointment(id);
        adminService.logAction(admin.getUsername(), "DELETE_APPOINTMENT", "Deleted appointment " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment deleted from active roster.");
        return "redirect:/admin/appointments";
    }

    // ==========================================
    // 6. ADMIN STAFF & AUDIT LOGS (Component 05)
    // ==========================================
    @GetMapping("/admins")
    public String manageAdmins(HttpSession session, Model model) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("admins", adminService.getAllAdmins());
        model.addAttribute("logs", adminService.getActivityLogs());
        return "admin/admins";
    }

    @PostMapping("/admins/new")
    public String createAdminAccount(@RequestParam String username,
                                     @RequestParam String password,
                                     @RequestParam String fullName,
                                     @RequestParam String email,
                                     @RequestParam String phone,
                                     @RequestParam String adminRole,
                                     @RequestParam String department,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        if (!admin.canManageUsers()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only Salon Owners can provision new administrator accounts.");
            return "redirect:/admin/admins";
        }

        adminService.registerAdmin(username, password, fullName, email, phone, adminRole, department, admin.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Admin account created for " + fullName);
        return "redirect:/admin/admins";
    }

    @PostMapping("/admins/delete/{id}")
    public String deleteAdminAccount(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        if (admin.getId().equalsIgnoreCase(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete your own active administrator account!");
            return "redirect:/admin/admins";
        }

        adminService.deleteAdmin(id, admin.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Admin account removed.");
        return "redirect:/admin/admins";
    }

    // ==========================================
    // 7. REVIEW MODERATION (Component 06)
    // ==========================================
    @GetMapping("/reviews")
    public String manageReviews(HttpSession session, Model model) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin/reviews";
    }

    @PostMapping("/reviews/moderate/{id}")
    public String moderateReview(@PathVariable String id,
                                 @RequestParam String status,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        reviewService.moderateReview(id, status);
        adminService.logAction(admin.getUsername(), "MODERATE_REVIEW", "Review " + id + " marked as " + status);
        redirectAttributes.addFlashAttribute("successMessage", "Review status updated to " + status);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/delete/{id}")
    public String deleteReviewAdmin(@PathVariable String id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = verifyAdmin(session);
        if (admin == null) return "redirect:/login";

        reviewService.deleteReview(id);
        adminService.logAction(admin.getUsername(), "DELETE_REVIEW", "Deleted review ID: " + id);
        redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully.");
        return "redirect:/admin/reviews";
    }
}
