package com.salon.VelvetBeauty.service;

import com.salon.VelvetBeauty.model.Admin;
import com.salon.VelvetBeauty.model.AdminActivityLog;
import com.salon.VelvetBeauty.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing Salon Administrator accounts, privileges, and audit logging.
 */
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public Optional<Admin> getAdminById(String id) {
        return adminRepository.findById(id);
    }

    public Optional<Admin> getAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Admin registerAdmin(String username, String password, String fullName, 
                               String email, String phone, String adminRole, String department, 
                               String registeredBy) {
        String id = "ADM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Admin admin = new Admin(id, username, password, fullName, email, phone, now, adminRole, department);
        adminRepository.save(admin);

        logAction(registeredBy != null ? registeredBy : "System", "REGISTER_ADMIN", 
                "Registered new administrator: " + username + " (" + adminRole + ")");
        return admin;
    }

    public boolean updateAdmin(String id, String email, String phone, String password, 
                               String adminRole, String department, String modifiedBy) {
        Optional<Admin> existingOpt = adminRepository.findById(id);
        if (existingOpt.isEmpty()) return false;

        Admin admin = existingOpt.get();
        if (email != null && !email.trim().isEmpty()) admin.setEmail(email.trim());
        if (phone != null && !phone.trim().isEmpty()) admin.setPhone(phone.trim());
        if (password != null && !password.trim().isEmpty()) admin.setPassword(password.trim());
        if (adminRole != null && !adminRole.trim().isEmpty()) admin.setAdminRole(adminRole.trim());
        if (department != null && !department.trim().isEmpty()) admin.setDepartment(department.trim());

        adminRepository.save(admin);
        logAction(modifiedBy != null ? modifiedBy : "System", "UPDATE_ADMIN", 
                "Updated details for admin ID: " + id);
        return true;
    }

    public boolean deleteAdmin(String id, String deletedBy) {
        Optional<Admin> admin = adminRepository.findById(id);
        if (admin.isPresent()) {
            boolean removed = adminRepository.deleteById(id);
            if (removed) {
                logAction(deletedBy != null ? deletedBy : "System", "DELETE_ADMIN", 
                        "Deleted admin account: " + admin.get().getUsername());
            }
            return removed;
        }
        return false;
    }

    public void logAction(String adminUsername, String action, String details) {
        String id = "LOG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        AdminActivityLog log = new AdminActivityLog(id, adminUsername, action, details, timestamp);
        adminRepository.logActivity(log);
    }

    public List<AdminActivityLog> getActivityLogs() {
        return adminRepository.findAllLogs();
    }
}
