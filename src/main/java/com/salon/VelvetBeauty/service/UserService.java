package com.salon.VelvetBeauty.service;

import com.salon.VelvetBeauty.model.Admin;
import com.salon.VelvetBeauty.model.Customer;
import com.salon.VelvetBeauty.model.PremiumCustomer;
import com.salon.VelvetBeauty.model.RegularCustomer;
import com.salon.VelvetBeauty.model.User;
import com.salon.VelvetBeauty.repository.AdminRepository;
import com.salon.VelvetBeauty.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing Customer and User authentication and profile operations.
 * Demonstrates: OOP encapsulation, polymorphism handling across Regular and Premium customers.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public UserService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    /**
     * Authenticates a user (checking both admin and customer stores).
     */
    public Optional<User> authenticate(String username, String password) {
        if (username == null || password == null) return Optional.empty();

        // 1. Check Admins first
        Optional<Admin> admin = adminRepository.findByUsername(username.trim());
        if (admin.isPresent() && admin.get().getPassword().equals(password.trim())) {
            return Optional.of(admin.get());
        }

        // 2. Check Customers
        Optional<Customer> customer = userRepository.findByUsername(username.trim());
        if (customer.isPresent() && customer.get().getPassword().equals(password.trim())) {
            return Optional.of(customer.get());
        }

        return Optional.empty();
    }

    /**
     * Registers a new customer into users.txt.
     */
    public Customer registerCustomer(String username, String password, String fullName, 
                                     String email, String phone, String membershipType) {
        String id = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        Customer customer;
        if ("PREMIUM".equalsIgnoreCase(membershipType)) {
            customer = new PremiumCustomer(id, username, password, fullName, email, phone, createdAt, 100, "GOLD");
        } else {
            customer = new RegularCustomer(id, username, password, fullName, email, phone, createdAt, 20);
        }

        userRepository.save(customer);
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return userRepository.findAll();
    }

    public Optional<Customer> getCustomerById(String id) {
        return userRepository.findById(id);
    }

    public Optional<Customer> getCustomerByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Updates customer profile details.
     */
    public boolean updateProfile(String id, String email, String phone, String password, String membershipType) {
        Optional<Customer> existingOpt = userRepository.findById(id);
        if (existingOpt.isEmpty()) return false;

        Customer existing = existingOpt.get();
        if (email != null && !email.trim().isEmpty()) existing.setEmail(email.trim());
        if (phone != null && !phone.trim().isEmpty()) existing.setPhone(phone.trim());
        if (password != null && !password.trim().isEmpty()) existing.setPassword(password.trim());

        // Handle membership upgrade or switch polymorphically
        if (membershipType != null && !membershipType.equalsIgnoreCase(existing.getMembershipType())) {
            if ("PREMIUM".equalsIgnoreCase(membershipType)) {
                existing = new PremiumCustomer(existing.getId(), existing.getUsername(), existing.getPassword(),
                        existing.getFullName(), existing.getEmail(), existing.getPhone(), existing.getCreatedAt(),
                        existing.getLoyaltyPoints() + 50, "GOLD");
            } else {
                existing = new RegularCustomer(existing.getId(), existing.getUsername(), existing.getPassword(),
                        existing.getFullName(), existing.getEmail(), existing.getPhone(), existing.getCreatedAt(),
                        existing.getLoyaltyPoints());
            }
        }

        userRepository.save(existing);
        return true;
    }

    /**
     * Deletes a customer account from users.txt.
     */
    public boolean deleteCustomer(String id) {
        return userRepository.deleteById(id);
    }

    public boolean isUsernameTaken(String username) {
        if (username == null) return false;
        String trimmed = username.trim();
        return userRepository.findByUsername(trimmed).isPresent() 
                || adminRepository.findByUsername(trimmed).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return isEmailTaken(email, null);
    }

    public boolean isEmailTaken(String email, String excludeUserId) {
        if (email == null) return false;
        String trimmed = email.trim();
        
        boolean takenByCustomer = userRepository.findAll().stream()
                .filter(c -> excludeUserId == null || !excludeUserId.equalsIgnoreCase(c.getId()))
                .anyMatch(c -> trimmed.equalsIgnoreCase(c.getEmail()));
                
        boolean takenByAdmin = adminRepository.findAll().stream()
                .filter(a -> excludeUserId == null || !excludeUserId.equalsIgnoreCase(a.getId()))
                .anyMatch(a -> trimmed.equalsIgnoreCase(a.getEmail()));

        return takenByCustomer || takenByAdmin;
    }
}
