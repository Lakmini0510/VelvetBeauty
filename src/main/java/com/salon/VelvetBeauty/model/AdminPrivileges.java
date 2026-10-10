package com.salon.VelvetBeauty.model;

/**
 * Abstraction Interface representing privileged administrative actions.
 * Demonstrates: Abstraction - defining administrative contracts separated from concrete user implementations.
 */
public interface AdminPrivileges {
    boolean canApproveAppointments();
    boolean canManageUsers();
    boolean canManageServices();
    boolean canManageStylists();
    boolean canModerateReviews();
    String getAdminRole();
}
