package com.salon.VelvetBeauty.repository;

import com.salon.VelvetBeauty.model.FullTimeStylist;
import com.salon.VelvetBeauty.model.PartTimeStylist;
import com.salon.VelvetBeauty.model.Stylist;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository handling CRUD operations for Salon Stylists in stylists.txt.
 * Demonstrates: Polymorphic instantiation of FullTimeStylist and PartTimeStylist.
 */
@Repository
public class StylistRepository {
    private static final String FILE_NAME = "stylists.txt";
    private final FileStorageManager fileStorage;

    public StylistRepository(FileStorageManager fileStorage) {
        this.fileStorage = fileStorage;
    }

    public synchronized List<Stylist> findAll() {
        List<Stylist> stylists = new ArrayList<>();
        List<String> lines = fileStorage.readAllLines(FILE_NAME);
        for (String line : lines) {
            Stylist stylist = parseLine(line);
            if (stylist != null) {
                stylists.add(stylist);
            }
        }
        return stylists;
    }

    public synchronized Optional<Stylist> findById(String id) {
        if (id == null) return Optional.empty();
        return findAll().stream()
                .filter(s -> id.equalsIgnoreCase(s.getId()))
                .findFirst();
    }

    public synchronized List<Stylist> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        String q = query.toLowerCase();
        return findAll().stream()
                .filter(s -> s.getName().toLowerCase().contains(q) 
                        || s.getSpecialty().toLowerCase().contains(q)
                        || s.getWorkingDays().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public synchronized void save(Stylist stylist) {
        List<Stylist> all = findAll();
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equalsIgnoreCase(stylist.getId())) {
                all.set(i, stylist);
                updated = true;
                break;
            }
        }
        if (!updated) {
            all.add(stylist);
        }
        saveAll(all);
    }

    public synchronized boolean deleteById(String id) {
        List<Stylist> all = findAll();
        boolean removed = all.removeIf(s -> s.getId().equalsIgnoreCase(id));
        if (removed) {
            saveAll(all);
        }
        return removed;
    }

    public synchronized void saveAll(List<Stylist> stylists) {
        List<String> lines = new ArrayList<>();
        for (Stylist s : stylists) {
            lines.add(s.toFileRecord());
        }
        fileStorage.writeAllLines(FILE_NAME, lines);
    }

    private Stylist parseLine(String line) {
        try {
            String[] parts = line.split(";;", -1);
            if (parts.length >= 12) {
                String id = parts[0];
                String name = parts[1];
                String specialty = parts[2];
                String workingDays = parts[3];
                String workingHours = parts[4];
                String phone = parts[5];
                String email = parts[6];
                String imageUrl = parts[7];
                String bio = parts[8];
                double rating = Double.parseDouble(parts[9]);
                boolean active = Boolean.parseBoolean(parts[10]);
                String type = parts[11];

                if ("PART_TIME".equalsIgnoreCase(type)) {
                    double hourlyRate = parts.length > 12 && !parts[12].isEmpty() ? Double.parseDouble(parts[12]) : 25.0;
                    int weeklyHours = parts.length > 13 && !parts[13].isEmpty() ? Integer.parseInt(parts[13]) : 20;
                    double commRate = parts.length > 14 && !parts[14].isEmpty() ? Double.parseDouble(parts[14]) : 0.15;
                    return new PartTimeStylist(id, name, specialty, workingDays, workingHours, phone, email, imageUrl, bio, rating, active, hourlyRate, weeklyHours, commRate);
                } else {
                    double fixedSalary = parts.length > 12 && !parts[12].isEmpty() ? Double.parseDouble(parts[12]) : 3500.0;
                    double bonusRate = parts.length > 13 && !parts[13].isEmpty() ? Double.parseDouble(parts[13]) : 0.08;
                    String benefits = parts.length > 14 ? parts[14] : "Health, 401(k)";
                    return new FullTimeStylist(id, name, specialty, workingDays, workingHours, phone, email, imageUrl, bio, rating, active, fixedSalary, bonusRate, benefits);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing stylist line: " + e.getMessage());
        }
        return null;
    }
}
