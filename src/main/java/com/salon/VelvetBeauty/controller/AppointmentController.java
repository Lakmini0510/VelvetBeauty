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
        model.addAttribute("minDate", LocalDate.now().toString());
        model.addAttribute("defaultDate", LocalDate.now().plusDays(1).toString());
        model.addAttribute("preselectedServiceId", serviceId);
        model.addAttribute("preselectedStylistId", stylistId);

        return "book-appointment";
    }

    @PostMapping("/book")
    public String handleBooking(@RequestParam(required = false) String serviceId,
                                @RequestParam(required = false) String customizationOption,
                                @RequestParam(required = false) String stylistId,
                                @RequestParam(required = false) String appointmentDate,
                                @RequestParam(required = false) String timeSlot,
                                @RequestParam(required = false) String notes,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!(user instanceof Customer customer)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only clients can reserve salon appointments.");
            return "redirect:/appointments/book";
        }

        if (serviceId == null || serviceId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a salon service.");
            return "redirect:/appointments/book";
        }

        if (stylistId == null || stylistId.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a stylist specialist.");
            return "redirect:/appointments/book";
        }

        if (appointmentDate == null || appointmentDate.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select an appointment date.");
            return "redirect:/appointments/book";
        }

        if (timeSlot == null || timeSlot.trim().isEmpty() || !AppointmentService.STANDARD_TIME_SLOTS.contains(timeSlot)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please choose a valid salon time slot.");
            return "redirect:/appointments/book";
        }

        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidNotes(notes)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Appointment notes must not exceed 500 characters and cannot contain ';;'.");
            return "redirect:/appointments/book";
        }

        // Re-fetch customer to ensure latest points/membership status
        customer = userService.getCustomerById(customer.getId()).orElse(customer);

        try {
            LocalDate date = LocalDate.parse(appointmentDate.trim());
            if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidFutureOrTodayDate(date)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Appointment date cannot be in the past.");
                return "redirect:/appointments/book";
            }

            Appointment appt = appointmentService.bookAppointment(
                    customer, serviceId.trim(), customizationOption, stylistId.trim(), date, timeSlot.trim(), notes
            );

            redirectAttributes.addFlashAttribute("successMessage", 
                    "Appointment booked successfully! Ref: " + appt.getId() + ". Discount applied: Rs. " + appt.getDiscountAmount());
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
                        "Appointment " + id + " has been cancelled. Late cancellation fee applied: Rs. " + appt.getCancellationFee());
            } else {
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Appointment " + id + " was cancelled with Rs. 0.00 fee according to your membership guarantee.");
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
