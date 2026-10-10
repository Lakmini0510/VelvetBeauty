package com.salon.VelvetBeauty.model;

/**
 * Admin entity representing staff administrators (Salon Owner, Receptionist).
 * Demonstrates: Encapsulation (secured admin fields), Inheritance (inherits from User),
 * and Abstraction (implements AdminPrivileges).
 */
public class Admin extends User implements AdminPrivileges {
    private String adminRole; // "SALON_OWNER" or "RECEPTIONIST"
    private String department;
    private String lastActive;

    public Admin() {
        super();
        setRole("ADMIN");
        this.adminRole = "SALON_OWNER";
        this.department = "Management";
    }

    public Admin(String id, String username, String password, String fullName, 
                 String email, String phone, String createdAt, String adminRole, String department) {
        super(id, username, password, fullName, email, phone, "ADMIN", createdAt);
        this.adminRole = (adminRole != null && !adminRole.isEmpty()) ? adminRole : "SALON_OWNER";
        this.department = (department != null && !department.isEmpty()) ? department : "Operations";
        this.lastActive = createdAt;
    }

    @Override
    public String getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLastActive() {
        return lastActive;
    }

    public void setLastActive(String lastActive) {
        this.lastActive = lastActive;
    }

    // Abstraction methods from AdminPrivileges
    @Override
    public boolean canApproveAppointments() {
        return true; // Both salon owner and receptionist can approve/manage appointments
    }

    @Override
    public boolean canManageUsers() {
        return "SALON_OWNER".equalsIgnoreCase(adminRole);
    }

    @Override
    public boolean canManageServices() {
        return "SALON_OWNER".equalsIgnoreCase(adminRole);
    }

    @Override
    public boolean canManageStylists() {
        return "SALON_OWNER".equalsIgnoreCase(adminRole);
    }

    @Override
    public boolean canModerateReviews() {
        return true; // Both can moderate feedback
    }

    @Override
    public String getRoleDescription() {
        return "Admin: " + ("SALON_OWNER".equalsIgnoreCase(adminRole) ? "Salon Owner (Full Control)" : "Front Desk Receptionist");
    }

    @Override
    public String toFileRecord() {
        // Format: ID;;USERNAME;;PASSWORD;;FULL_NAME;;EMAIL;;PHONE;;CREATED_AT;;ADMIN_ROLE;;DEPARTMENT
        return String.join(";;",
                getId() != null ? getId() : "",
                getUsername() != null ? getUsername() : "",
                getPassword() != null ? getPassword() : "",
                getFullName() != null ? getFullName() : "",
                getEmail() != null ? getEmail() : "",
                getPhone() != null ? getPhone() : "",
                getCreatedAt() != null ? getCreatedAt() : "",
                getAdminRole(),
                getDepartment() != null ? getDepartment() : "Operations"
        );
    }
}
