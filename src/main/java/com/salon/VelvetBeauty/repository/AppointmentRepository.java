package com.salon.VelvetBeauty.repository;

import com.salon.VelvetBeauty.model.Appointment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository handling CRUD operations for Salon Appointments in appointments.txt.
 */
@Repository
public class AppointmentRepository {
    private static final String FILE_NAME = "appointments.txt";
    private final FileStorageManager fileStorage;

    public AppointmentRepository(FileStorageManager fileStorage) {
        this.fileStorage = fileStorage;
    }

    public synchronized List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();
        List<String> lines = fileStorage.readAllLines(FILE_NAME);
        for (String line : lines) {
            Appointment appt = parseLine(line);
            if (appt != null) {
                appointments.add(appt);
            }
        }
        return appointments;
    }

    public synchronized Optional<Appointment> findById(String id) {
        if (id == null) return Optional.empty();
        return findAll().stream()
                .filter(a -> id.equalsIgnoreCase(a.getId()))
                .findFirst();
    }

    public synchronized List<Appointment> findByCustomerId(String customerId) {
        if (customerId == null) return new ArrayList<>();
        return findAll().stream()
                .filter(a -> customerId.equalsIgnoreCase(a.getCustomerId()))
                .sorted((a, b) -> b.getAppointmentDate().compareTo(a.getAppointmentDate()))
                .collect(Collectors.toList());
    }

    public synchronized List<Appointment> findByStylistId(String stylistId) {
        if (stylistId == null) return new ArrayList<>();
        return findAll().stream()
                .filter(a -> stylistId.equalsIgnoreCase(a.getStylistId()))
                .collect(Collectors.toList());
    }

    public synchronized void save(Appointment appointment) {
        List<Appointment> all = findAll();
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equalsIgnoreCase(appointment.getId())) {
                all.set(i, appointment);
                updated = true;
                break;
            }
        }
        if (!updated) {
            all.add(appointment);
        }
        saveAll(all);
    }

    public synchronized boolean deleteById(String id) {
        List<Appointment> all = findAll();
        boolean removed = all.removeIf(a -> a.getId().equalsIgnoreCase(id));
        if (removed) {
            saveAll(all);
        }
        return removed;
    }

    public synchronized void saveAll(List<Appointment> appointments) {
        List<String> lines = new ArrayList<>();
        for (Appointment a : appointments) {
            lines.add(a.toFileRecord());
        }
        fileStorage.writeAllLines(FILE_NAME, lines);
    }

    private Appointment parseLine(String line) {
        try {
            String[] parts = line.split(";;", -1);
            if (parts.length >= 19) {
                String id = parts[0];
                String customerId = parts[1];
                String customerUsername = parts[2];
                String customerName = parts[3];
                String customerEmail = parts[4];
                String customerPhone = parts[5];
                String membershipType = parts[6];
                String serviceId = parts[7];
                String serviceName = parts[8];
                String serviceCategory = parts[9];
                String serviceCustomization = parts[10];
                String stylistId = parts[11];
                String stylistName = parts[12];
                String date = parts[13];
                String timeSlot = parts[14];
                String status = parts[15];
                double basePrice = Double.parseDouble(parts[16]);
                double discount = Double.parseDouble(parts[17]);
                double finalPrice = Double.parseDouble(parts[18]);
                double cancelFee = parts.length > 19 && !parts[19].isEmpty() ? Double.parseDouble(parts[19]) : 0.0;
                String cancelReason = parts.length > 20 ? parts[20] : "";
                String notes = parts.length > 21 ? parts[21] : "";
                String createdAt = parts.length > 22 ? parts[22] : "";

                return new Appointment(id, customerId, customerUsername, customerName, customerEmail, 
                        customerPhone, membershipType, serviceId, serviceName, serviceCategory, 
                        serviceCustomization, stylistId, stylistName, date, timeSlot, status, 
                        basePrice, discount, finalPrice, cancelFee, cancelReason, notes, createdAt);
            }
        } catch (Exception e) {
            System.err.println("Error parsing appointment line: " + e.getMessage());
        }
        return null;
    }
}
