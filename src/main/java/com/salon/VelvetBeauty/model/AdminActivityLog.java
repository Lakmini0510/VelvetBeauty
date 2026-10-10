package com.salon.VelvetBeauty.model;

/**
 * Encapsulated record representing an administrative action log.
 */
public class AdminActivityLog {
    private String id;
    private String adminUsername;
    private String action;
    private String details;
    private String timestamp;

    public AdminActivityLog() {
    }

    public AdminActivityLog(String id, String adminUsername, String action, String details, String timestamp) {
        this.id = id;
        this.adminUsername = adminUsername;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String toFileRecord() {
        return String.join(";;",
                id != null ? id : "",
                adminUsername != null ? adminUsername : "",
                action != null ? action : "",
                details != null ? details : "",
                timestamp != null ? timestamp : ""
        );
    }
}
