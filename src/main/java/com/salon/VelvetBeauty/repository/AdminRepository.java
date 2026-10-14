package com.salon.VelvetBeauty.repository;

import com.salon.VelvetBeauty.model.Admin;
import com.salon.VelvetBeauty.model.AdminActivityLog;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository handling CRUD operations for Administrator accounts in admins.txt
 * and audit trail entries in admin_logs.txt.
 */
@Repository
public class AdminRepository {
    private static final String ADMIN_FILE = "admins.txt";
    private static final String LOG_FILE = "admin_logs.txt";
    private final FileStorageManager fileStorage;

    public AdminRepository(FileStorageManager fileStorage) {
        this.fileStorage = fileStorage;
    }

    public synchronized List<Admin> findAll() {
        List<Admin> admins = new ArrayList<>();
        List<String> lines = fileStorage.readAllLines(ADMIN_FILE);
        for (String line : lines) {
            Admin admin = parseAdminLine(line);
            if (admin != null) {
                admins.add(admin);
            }
        }
        return admins;
    }

    public synchronized Optional<Admin> findById(String id) {
        if (id == null) return Optional.empty();
        return findAll().stream()
                .filter(a -> id.equalsIgnoreCase(a.getId()))
                .findFirst();
    }

    public synchronized Optional<Admin> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return findAll().stream()
                .filter(a -> username.equalsIgnoreCase(a.getUsername()))
                .findFirst();
    }

    public synchronized Optional<Admin> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return findAll().stream()
                .filter(a -> email.equalsIgnoreCase(a.getEmail()))
                .findFirst();
    }

    public synchronized void save(Admin admin) {
        List<Admin> all = findAll();
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equalsIgnoreCase(admin.getId())) {
                all.set(i, admin);
                updated = true;
                break;
            }
        }
        if (!updated) {
            all.add(admin);
        }
        saveAll(all);
    }

    public synchronized boolean deleteById(String id) {
        List<Admin> all = findAll();
        boolean removed = all.removeIf(a -> a.getId().equalsIgnoreCase(id));
        if (removed) {
            saveAll(all);
        }
        return removed;
    }

    public synchronized void saveAll(List<Admin> admins) {
        List<String> lines = new ArrayList<>();
        for (Admin a : admins) {
            lines.add(a.toFileRecord());
        }
        fileStorage.writeAllLines(ADMIN_FILE, lines);
    }

    public synchronized void logActivity(AdminActivityLog log) {
        fileStorage.appendLine(LOG_FILE, log.toFileRecord());
    }

    public synchronized List<AdminActivityLog> findAllLogs() {
        List<AdminActivityLog> logs = new ArrayList<>();
        List<String> lines = fileStorage.readAllLines(LOG_FILE);
        for (String line : lines) {
            String[] parts = line.split(";;", -1);
            if (parts.length >= 5) {
                logs.add(new AdminActivityLog(parts[0], parts[1], parts[2], parts[3], parts[4]));
            }
        }
        // Return most recent logs first
        return logs.stream().sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())).toList();
    }

    private Admin parseAdminLine(String line) {
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
                String adminRole = parts[7];
                String department = parts.length > 8 ? parts[8] : "Operations";
                return new Admin(id, username, password, fullName, email, phone, createdAt, adminRole, department);
            }
        } catch (Exception e) {
            System.err.println("Error parsing admin record: " + e.getMessage());
        }
        return null;
    }
}
