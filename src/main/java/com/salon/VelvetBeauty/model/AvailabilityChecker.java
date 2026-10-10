package com.salon.VelvetBeauty.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Abstraction Interface defining time slot availability verification.
 * Demonstrates: Abstraction - hides complex scheduling and conflict resolution algorithms
 * behind a clean interface.
 */
public interface AvailabilityChecker {
    /**
     * Checks if a stylist is available at the requested date and time slot.
     */
    boolean isSlotAvailable(String stylistId, LocalDate date, String timeSlot, String excludeAppointmentId);

    /**
     * Retrieves all available time slots for a given stylist on a particular date.
     */
    List<String> getAvailableSlots(String stylistId, LocalDate date);
}
