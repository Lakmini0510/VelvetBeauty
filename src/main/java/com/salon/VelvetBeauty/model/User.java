package com.salon.VelvetBeauty.model;

/**
 * Base abstract class representing any system user.
 * Demonstrates: Encapsulation (private fields, protected constructors, public accessors).
 */
public abstract class User {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role; // "CUSTOMER" or "ADMIN"
    private String createdAt;

    public User() {
    }

    public User(String id, String username, String password, String fullName, String email, String phone, String role, String createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters demonstrating encapsulation
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Polymorphic method to get the user's role description.
     */
    public abstract String getRoleDescription();

    /**
     * Serialization format for storing in text file.
     */
    public abstract String toFileRecord();
}
