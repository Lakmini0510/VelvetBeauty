package com.salon.VelvetBeauty.service;

import com.salon.VelvetBeauty.model.*;
import com.salon.VelvetBeauty.repository.AppointmentRepository;
import com.salon.VelvetBeauty.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing Appointment booking, rescheduling, cancellation, and time slot availability.
 * Demonstrates:
 * - Abstraction: Implements AvailabilityChecker to conceal scheduling conflict resolution.
 * - Polymorphism: Enforces customer-specific discount logic and distinct cancellation fee rules
 *   between RegularCustomer and PremiumCustomer.
 */
@Service
public class AppointmentService implements AvailabilityChecker {

    public static final List<String> STANDARD_TIME_SLOTS = List.of(
            "09:00 AM", "10:30 AM", "12:00 PM", "01:30 PM", "03:00 PM", "04:30 PM", "06:00 PM"
    );

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final SalonCatalogService catalogService;
    private final StylistService stylistService;

    public AppointmentService(AppointmentRepository appointmentRepository, 
                              UserRepository userRepository, 
                              SalonCatalogService catalogService, 
                              StylistService stylistService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.catalogService = catalogService;
        this.stylistService = stylistService;
    }

    // --- Abstraction: AvailabilityChecker implementation ---

    @Override
    public boolean isSlotAvailable(String stylistId, LocalDate date, String timeSlot, String excludeAppointmentId) {
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<Appointment> existing = appointmentRepository.findAll().stream()
                .filter(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                .filter(a -> stylistId.equalsIgnoreCase(a.getStylistId()))
                .filter(a -> dateStr.equalsIgnoreCase(a.getAppointmentDate()))
                .filter(a -> timeSlot.equalsIgnoreCase(a.getTimeSlot()))
                .filter(a -> excludeAppointmentId == null || !excludeAppointmentId.equalsIgnoreCase(a.getId()))
                .toList();

        return existing.isEmpty();
    }

    @Override
    public List<String> getAvailableSlots(String stylistId, LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        Set<String> bookedSlots = appointmentRepository.findAll().stream()
                .filter(a -> !"CANCELLED".equalsIgnoreCase(a.getStatus()))
                .filter(a -> stylistId.equalsIgnoreCase(a.getStylistId()))
                .filter(a -> dateStr.equalsIgnoreCase(a.getAppointmentDate()))
                .map(Appointment::getTimeSlot)
                .collect(Collectors.toSet());

        return STANDARD_TIME_SLOTS.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    // --- CRUD Operations ---

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .sorted((a, b) -> b.getAppointmentDate().compareTo(a.getAppointmentDate()))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByCustomer(String customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    public Optional<Appointment> getAppointmentById(String id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Books a new appointment, applying polymorphic customer discounts and service customizations.
     */
    public Appointment bookAppointment(Customer customer, String serviceId, String customizationOption, 
                                       String stylistId, LocalDate date, String timeSlot, String notes) {
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidFutureOrTodayDate(date)) {
            throw new IllegalArgumentException("Appointment date cannot be in the past.");
        }

        if (!STANDARD_TIME_SLOTS.contains(timeSlot)) {
            throw new IllegalArgumentException("Invalid time slot selected: " + timeSlot);
        }

        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidNotes(notes)) {
            throw new IllegalArgumentException("Special requests notes must be under 500 characters and cannot contain ';;'.");
        }

        // Validate availability abstraction
        if (!isSlotAvailable(stylistId, date, timeSlot, null)) {
            throw new IllegalStateException("Stylist is unavailable at selected time slot " + timeSlot + " on " + date);
        }

        Optional<SalonService> serviceOpt = catalogService.getServiceById(serviceId);
        if (serviceOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid service selected: " + serviceId);
        }
        SalonService service = serviceOpt.get();

        Optional<Stylist> stylistOpt = stylistService.getStylistById(stylistId);
        if (stylistOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid stylist selected: " + stylistId);
        }
        Stylist stylist = stylistOpt.get();

        // Polymorphic service customized price
        double customizedPrice = service.calculateCustomizedPrice(customizationOption);

        // Polymorphic customer discount
        double finalPrice = customer.applyDiscount(customizedPrice);
        double discountAmount = Math.round((customizedPrice - finalPrice) * 100.0) / 100.0;

        String id = "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Appointment appt = new Appointment(
                id,
                customer.getId(),
                customer.getUsername(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getMembershipType(),
                service.getId(),
                service.getName(),
                service.getCategory(),
                customizationOption != null ? customizationOption : "Standard",
                stylist.getId(),
                stylist.getName(),
                date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                timeSlot,
                "BOOKED",
                customizedPrice,
                discountAmount,
                finalPrice,
                0.0,
                "",
                notes != null ? notes : "",
                createdAt
        );

        appointmentRepository.save(appt);
        return appt;
    }

    /**
     * Reschedules an existing appointment to a new date and time.
     */
    public boolean rescheduleAppointment(String appointmentId, LocalDate newDate, String newTimeSlot) {
        if (!com.salon.VelvetBeauty.util.ValidationUtil.isValidFutureOrTodayDate(newDate)) {
            throw new IllegalArgumentException("Rescheduled appointment date cannot be in the past.");
        }

        if (!STANDARD_TIME_SLOTS.contains(newTimeSlot)) {
            throw new IllegalArgumentException("Invalid time slot: " + newTimeSlot);
        }

        Optional<Appointment> apptOpt = appointmentRepository.findById(appointmentId);
        if (apptOpt.isEmpty()) return false;

        Appointment appt = apptOpt.get();
        if ("CANCELLED".equalsIgnoreCase(appt.getStatus())) {
            throw new IllegalStateException("Cannot reschedule a cancelled appointment.");
        }

        if (!isSlotAvailable(appt.getStylistId(), newDate, newTimeSlot, appointmentId)) {
            throw new IllegalStateException("The requested slot " + newTimeSlot + " is already booked for this stylist.");
        }

        appt.setAppointmentDate(newDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        appt.setTimeSlot(newTimeSlot);
        appointmentRepository.save(appt);
        return true;
    }

    /**
     * Cancels an appointment, polymorphically computing cancellation fees based on customer tier.
     */
    public Appointment cancelAppointment(String appointmentId, String reason) {
        Optional<Appointment> apptOpt = appointmentRepository.findById(appointmentId);
        if (apptOpt.isEmpty()) {
            throw new IllegalArgumentException("Appointment not found: " + appointmentId);
        }

        Appointment appt = apptOpt.get();
        if ("CANCELLED".equalsIgnoreCase(appt.getStatus())) {
            return appt;
        }

        // Calculate hours remaining before appointment
        long hoursRemaining = calculateHoursRemaining(appt.getAppointmentDate(), appt.getTimeSlot());

        // Resolve customer polymorphically
        Customer customer = userRepository.findById(appt.getCustomerId()).orElse(null);

        // Apply polymorphic cancellation rules
        appt.applyCancellationPolicy(customer, hoursRemaining, reason);
        appointmentRepository.save(appt);

        return appt;
    }

    public boolean updateStatus(String appointmentId, String status) {
        Optional<Appointment> apptOpt = appointmentRepository.findById(appointmentId);
        if (apptOpt.isEmpty()) return false;

        Appointment appt = apptOpt.get();
        appt.setStatus(status.toUpperCase());
        appointmentRepository.save(appt);
        return true;
    }

    public boolean deleteAppointment(String appointmentId) {
        return appointmentRepository.deleteById(appointmentId);
    }

    private long calculateHoursRemaining(String dateStr, String timeSlotStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            String cleanTime = timeSlotStr.split(" ")[0]; // e.g. "09:00"
            boolean isPm = timeSlotStr.toUpperCase().contains("PM");
            String[] parts = cleanTime.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            if (isPm && hour < 12) hour += 12;
            if (!isPm && hour == 12) hour = 0;

            LocalDateTime apptTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
            LocalDateTime now = LocalDateTime.now();

            if (apptTime.isBefore(now)) return 0;
            return ChronoUnit.HOURS.between(now, apptTime);
        } catch (Exception e) {
            return 48; // Default safe assumption
        }
    }
}
