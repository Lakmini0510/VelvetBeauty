package com.salon.VelvetBeauty.repository;

import com.salon.VelvetBeauty.model.HairService;
import com.salon.VelvetBeauty.model.NailService;
import com.salon.VelvetBeauty.model.SalonService;
import com.salon.VelvetBeauty.model.SkinService;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository handling CRUD operations for Salon Catalog Services in services.txt.
 * Demonstrates: Polymorphic instantiation of HairService, SkinService, and NailService.
 */
@Repository
public class ServiceRepository {
    private static final String FILE_NAME = "services.txt";
    private final FileStorageManager fileStorage;

    public ServiceRepository(FileStorageManager fileStorage) {
        this.fileStorage = fileStorage;
    }

    public synchronized List<SalonService> findAll() {
        List<SalonService> services = new ArrayList<>();
        List<String> lines = fileStorage.readAllLines(FILE_NAME);
        for (String line : lines) {
            SalonService service = parseLine(line);
            if (service != null) {
                services.add(service);
            }
        }
        return services;
    }

    public synchronized Optional<SalonService> findById(String id) {
        if (id == null) return Optional.empty();
        return findAll().stream()
                .filter(s -> id.equalsIgnoreCase(s.getId()))
                .findFirst();
    }

    public synchronized List<SalonService> findByCategory(String category) {
        if (category == null || category.equalsIgnoreCase("ALL")) {
            return findAll();
        }
        return findAll().stream()
                .filter(s -> category.equalsIgnoreCase(s.getCategory()))
                .collect(Collectors.toList());
    }

    public synchronized List<SalonService> search(String query, String category) {
        return findAll().stream()
                .filter(s -> {
                    boolean matchCategory = category == null || category.equalsIgnoreCase("ALL") 
                            || category.equalsIgnoreCase(s.getCategory());
                    boolean matchQuery = query == null || query.trim().isEmpty() 
                            || s.getName().toLowerCase().contains(query.toLowerCase())
                            || s.getDescription().toLowerCase().contains(query.toLowerCase());
                    return matchCategory && matchQuery;
                })
                .collect(Collectors.toList());
    }

    public synchronized void save(SalonService service) {
        List<SalonService> all = findAll();
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equalsIgnoreCase(service.getId())) {
                all.set(i, service);
                updated = true;
                break;
            }
        }
        if (!updated) {
            all.add(service);
        }
        saveAll(all);
    }

    public synchronized boolean deleteById(String id) {
        List<SalonService> all = findAll();
        boolean removed = all.removeIf(s -> s.getId().equalsIgnoreCase(id));
        if (removed) {
            saveAll(all);
        }
        return removed;
    }

    public synchronized void saveAll(List<SalonService> services) {
        List<String> lines = new ArrayList<>();
        for (SalonService s : services) {
            lines.add(s.toFileRecord());
        }
        fileStorage.writeAllLines(FILE_NAME, lines);
    }

    private SalonService parseLine(String line) {
        try {
            String[] parts = line.split(";;", -1);
            if (parts.length >= 8) {
                String id = parts[0];
                String name = parts[1];
                String category = parts[2].toUpperCase();
                double basePrice = Double.parseDouble(parts[3]);
                int duration = Integer.parseInt(parts[4]);
                String description = parts[5];
                String imageUrl = parts[6];
                boolean active = Boolean.parseBoolean(parts[7]);
                String customProp = parts.length > 8 ? parts[8] : "";

                switch (category) {
                    case "HAIR":
                        return new HairService(id, name, basePrice, duration, description, imageUrl, active, customProp);
                    case "SKIN":
                        return new SkinService(id, name, basePrice, duration, description, imageUrl, active, customProp);
                    case "NAIL":
                        return new NailService(id, name, basePrice, duration, description, imageUrl, active, customProp);
                    default:
                        return new HairService(id, name, basePrice, duration, description, imageUrl, active, customProp);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing service line: " + e.getMessage());
        }
        return null;
    }
}
