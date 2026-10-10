package com.salon.VelvetBeauty.repository;

import com.salon.VelvetBeauty.model.PublicReview;
import com.salon.VelvetBeauty.model.Review;
import com.salon.VelvetBeauty.model.VerifiedReview;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository handling CRUD operations for Customer Feedback & Reviews in reviews.txt.
 * Demonstrates: Polymorphic instantiation of PublicReview and VerifiedReview.
 */
@Repository
public class ReviewRepository {
    private static final String FILE_NAME = "reviews.txt";
    private final FileStorageManager fileStorage;

    public ReviewRepository(FileStorageManager fileStorage) {
        this.fileStorage = fileStorage;
    }

    public synchronized List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        List<String> lines = fileStorage.readAllLines(FILE_NAME);
        for (String line : lines) {
            Review review = parseLine(line);
            if (review != null) {
                reviews.add(review);
            }
        }
        return reviews;
    }

    public synchronized Optional<Review> findById(String id) {
        if (id == null) return Optional.empty();
        return findAll().stream()
                .filter(r -> id.equalsIgnoreCase(r.getId()))
                .findFirst();
    }

    public synchronized List<Review> findApproved() {
        return findAll().stream()
                .filter(r -> "APPROVED".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());
    }

    public synchronized List<Review> findByServiceId(String serviceId) {
        return findApproved().stream()
                .filter(r -> serviceId != null && serviceId.equalsIgnoreCase(r.getServiceId()))
                .collect(Collectors.toList());
    }

    public synchronized List<Review> findByStylistId(String stylistId) {
        return findApproved().stream()
                .filter(r -> stylistId != null && stylistId.equalsIgnoreCase(r.getStylistId()))
                .collect(Collectors.toList());
    }

    public synchronized void save(Review review) {
        List<Review> all = findAll();
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equalsIgnoreCase(review.getId())) {
                all.set(i, review);
                updated = true;
                break;
            }
        }
        if (!updated) {
            all.add(review);
        }
        saveAll(all);
    }

    public synchronized boolean deleteById(String id) {
        List<Review> all = findAll();
        boolean removed = all.removeIf(r -> r.getId().equalsIgnoreCase(id));
        if (removed) {
            saveAll(all);
        }
        return removed;
    }

    public synchronized void saveAll(List<Review> reviews) {
        List<String> lines = new ArrayList<>();
        for (Review r : reviews) {
            lines.add(r.toFileRecord());
        }
        fileStorage.writeAllLines(FILE_NAME, lines);
    }

    private Review parseLine(String line) {
        try {
            String[] parts = line.split(";;", -1);
            if (parts.length >= 11) {
                String id = parts[0];
                String customerId = parts[1];
                String customerName = parts[2];
                String serviceId = parts[3];
                String serviceName = parts[4];
                String stylistId = parts[5];
                String stylistName = parts[6];
                int rating = Integer.parseInt(parts[7]);
                String comment = parts[8];
                String status = parts[9];
                String date = parts[10];
                String type = parts.length > 11 ? parts[11] : "PUBLIC";
                String apptId = parts.length > 12 ? parts[12] : "";

                if ("VERIFIED".equalsIgnoreCase(type)) {
                    return new VerifiedReview(id, customerId, customerName, serviceId, serviceName, stylistId, stylistName, rating, comment, status, date, apptId);
                } else {
                    return new PublicReview(id, customerId, customerName, serviceId, serviceName, stylistId, stylistName, rating, comment, status, date);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing review line: " + e.getMessage());
        }
        return null;
    }
}
