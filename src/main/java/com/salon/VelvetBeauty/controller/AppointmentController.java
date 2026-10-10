package com.salon.VelvetBeauty.controller;

import com.salon.VelvetBeauty.model.*;
import com.salon.VelvetBeauty.service.AppointmentService;
import com.salon.VelvetBeauty.service.SalonCatalogService;
import com.salon.VelvetBeauty.service.StylistService;
import com.salon.VelvetBeauty.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller managing customer appointment booking, cancellation, and rescheduling.
 */
@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SalonCatalogService catalogService;
    private final StylistService stylistService;
    private final UserService userService;

    public AppointmentController(AppointmentService appointmentService, 
                                 SalonCatalogService catalogService, 
                                 StylistService stylistService, 
                                 UserService userService) {
        this.appointmentService = appointmentService;
        this.catalogService = catalogService;
        this.stylistService = stylistService;
        this.userService = userService;
    }

    @GetMapping("/book")
    public String showBookingPage(@RequestParam(required = false) String serviceId,
                                  @RequestParam(required = false) String stylistId,
                                  HttpSession session,
                                  Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<SalonService> services = catalogService.getAllServices();
        List<Stylist> stylists = stylistService.getActiveStylists();

        model.addAttribute("services", services);
        model.addAttribute("stylists", stylists);
        model.addAttribute("timeSlots", AppointmentService.STANDARD_TIME_SLOTS);
        model.addAttribute("defaultDate", LocalDate.now().plusDays(1).toString());
        model.addAttribute("preselectedServiceId", serviceId);
        model.addAttribute("preselectedStylistId", stylistId);

        return "book-appointment";
    }

    @PostMapping("/book")
    public String handleBooking(@RequestParam String serviceId,
                                @RequestParam(required = false) String customizationOption,
                                @RequestParam String stylistId,
                                @RequestParam String appointmentDate,
                                @RequestParam String timeSlot,
                                @RequestParam(required = false) String notes,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!(user instanceof Customer customer)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only customers can book appointments.");
            return "redirect:/appointments/book";
        }

        // Re-fetch customer to ensure latest points/membership status
        customer = userService.getCustomerById(customer.getId()).orElse(customer);

        try {
            LocalDate date = LocalDate.parse(appointmentDate);
            Appointment appt = appointmentService.bookAppointment(
                    customer, serviceId, customizationOption, stylistId, date, timeSlot, notes
            );

            redirectAttributes.addFlashAttribute("successMessage", 
                    "Appointment booked successfully! Ref: " + appt.getId() + ". Discount applied: $" + appt.getDiscountAmount());
            return "redirect:/appointments/my";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/appointments/book";
        }
    }

    @GetMapping("/my")
    public String listMyAppointments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(user.getId());
        model.addAttribute("appointments", appointments);
        return "my-appointments";
    }

    @PostMapping("/cancel/{id}")
    public String handleCancel(@PathVariable String id,
                               @RequestParam(defaultValue = "Customer cancellation request") String reason,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            Appointment appt = appointmentService.cancelAppointment(id, reason);
            if (appt.getCancellationFee() > 0) {
                redirectAttributes.addFlashAttribute("infoMessage", 
                        "Appointment " + id + " has been cancelled. Late cancellation fee applied: $" + appt.getCancellationFee());
            } else {
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Appointment " + id + " was cancelled with $0.00 fee according to your membership guarantee.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/appointments/my";
    }

    @GetMapping("/reschedule/{id}")
    public String showReschedulePage(@PathVariable String id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Appointment> apptOpt = appointmentService.getAppointmentById(id);
        if (apptOpt.isEmpty()) {
            return "redirect:/appointments/my";
        }

        model.addAttribute("appointment", apptOpt.get());
        model.addAttribute("timeSlots", AppointmentService.STANDARD_TIME_SLOTS);
        return "reschedule-appointment";
    }

    @PostMapping("/reschedule/{id}")
    public String handleReschedule(@PathVariable String id,
                                   @RequestParam String newDate,
                                   @RequestParam String newTimeSlot,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            LocalDate date = LocalDate.parse(newDate);
            boolean success = appointmentService.rescheduleAppointment(id, date, newTimeSlot);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Appointment rescheduled to " + newDate + " at " + newTimeSlot);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to reschedule appointment.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/appointments/my";
    }

    @GetMapping("/slots")
    @ResponseBody
    public List<String> getAvailableSlotsAjax(@RequestParam String stylistId, @RequestParam String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            return appointmentService.getAvailableSlots(stylistId, localDate);
        } catch (Exception e) {
            return AppointmentService.STANDARD_TIME_SLOTS;
        }
    }
}
