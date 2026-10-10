package com.salon.VelvetBeauty.repository;

import com.salon.VelvetBeauty.model.Customer;
import com.salon.VelvetBeauty.model.PremiumCustomer;
import com.salon.VelvetBeauty.model.RegularCustomer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository handling CRUD operations for Customer accounts in users.txt.
 * Demonstrates: Polymorphic reconstitution of RegularCustomer and PremiumCustomer.
 */
@Repository
public class UserRepository {
    private static final String FILE_NAME = "users.txt";
    private final FileStorageManager fileStorage;

    public UserRepository(FileStorageManager fileStorage) {
        this.fileStorage = fileStorage;
    }

    public synchronized List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        List<String> lines = fileStorage.readAllLines(FILE_NAME);
        for (String line : lines) {
            Customer customer = parseLine(line);
            if (customer != null) {
                customers.add(customer);
            }
        }
        return customers;
    }

    public synchronized Optional<Customer> findById(String id) {
        if (id == null) return Optional.empty();
        return findAll().stream()
                .filter(c -> id.equalsIgnoreCase(c.getId()))
                .findFirst();
    }

    public synchronized Optional<Customer> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return findAll().stream()
                .filter(c -> username.equalsIgnoreCase(c.getUsername()))
                .findFirst();
    }

    public synchronized Optional<Customer> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return findAll().stream()
                .filter(c -> email.equalsIgnoreCase(c.getEmail()))
                .findFirst();
    }

    public synchronized void save(Customer customer) {
        List<Customer> all = findAll();
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equalsIgnoreCase(customer.getId())) {
                all.set(i, customer);
                updated = true;
                break;
            }
        }
        if (!updated) {
            all.add(customer);
        }
        saveAll(all);
    }

    public synchronized boolean deleteById(String id) {
        List<Customer> all = findAll();
        boolean removed = all.removeIf(c -> c.getId().equalsIgnoreCase(id));
        if (removed) {
            saveAll(all);
        }
        return removed;
    }

    public synchronized void saveAll(List<Customer> customers) {
        List<String> lines = new ArrayList<>();
        for (Customer c : customers) {
            lines.add(c.toFileRecord());
        }
        fileStorage.writeAllLines(FILE_NAME, lines);
    }

    private Customer parseLine(String line) {
        try {
            String[] parts = line.split(";;", -1);
            if (parts.length >= 8) {
                String id = parts[0];
                String username = parts[1];
                String password = parts[2];
                String fullName = parts[3];
                String email = parts[4];
                String phone = parts[5];
                String createdAt = parts[6];
                String membershipType = parts[7];
                int loyaltyPoints = parts.length > 8 && !parts[8].isEmpty() ? Integer.parseInt(parts[8]) : 0;

                if ("PREMIUM".equalsIgnoreCase(membershipType)) {
                    String vipTier = parts.length > 9 && !parts[9].isEmpty() ? parts[9] : "GOLD";
                    return new PremiumCustomer(id, username, password, fullName, email, phone, createdAt, loyaltyPoints, vipTier);
                } else {
                    return new RegularCustomer(id, username, password, fullName, email, phone, createdAt, loyaltyPoints);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing user record line [" + line + "]: " + e.getMessage());
        }
        return null;
    }
}
