package com.salon.VelvetBeauty.util;

import java.util.regex.Pattern;

/**
 * Utility class providing centralized input validations and file-delimiter safety
 * across all customer, stylist, appointment, and administrative workflows.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Accommodates Sri Lankan local (07XXXXXXXX, 011XXXXXXX), international (+94XXXXXXXXX),
    // and standard global E.164 phone formats
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+?[0-9]{1,4}[-.\s]?)?(\\(?[0-9]{2,5}\\)?[-.\s]?)?[0-9]{3,4}[-.\s]?[0-9]{3,4}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_]{3,30}$"
    );

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z\\s.'-]{2,60}$"
    );

    public static final String DELIMITER = ";;";

    private ValidationUtil() {
        // Utility class
    }

    /**
     * Validates email address format and length.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        if (trimmed.length() < 5 || trimmed.length() > 100) return false;
        if (containsDelimiter(trimmed)) return false;
        return EMAIL_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Validates phone numbers for Sri Lankan and international formats.
     * Ensures stripped digit count is between 9 and 15 digits.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String trimmed = phone.trim();
        if (trimmed.isEmpty() || containsDelimiter(trimmed)) return false;

        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            return false;
        }

        String digitsOnly = trimmed.replaceAll("[^0-9]", "");
        return digitsOnly.length() >= 9 && digitsOnly.length() <= 15;
    }

    /**
     * Validates usernames: 3-30 alphanumeric characters and underscores only.
     */
    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        String trimmed = username.trim();
        if (containsDelimiter(trimmed)) return false;
        return USERNAME_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Validates passwords: minimum 6 characters, maximum 64 characters.
     */
    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        if (password.length() < 6 || password.length() > 64) return false;
        return !containsDelimiter(password);
    }

    /**
     * Validates customer and staff full legal names: 2 to 60 characters.
     */
    public static boolean isValidName(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        if (containsDelimiter(trimmed)) return false;
        return NAME_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Checks if any string contains the file database delimiter ';;'.
     */
    public static boolean containsDelimiter(String... inputs) {
        if (inputs == null) return false;
        for (String input : inputs) {
            if (input != null && input.contains(DELIMITER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates service and wage prices (must be positive, non-zero and reasonable).
     */
    public static boolean isValidPrice(Double price) {
        return price != null && !price.isNaN() && !price.isInfinite() && price > 0 && price <= 50000000.0;
    }

    /**
     * Validates appointment and service duration in minutes (between 5 and 480 mins).
     */
    public static boolean isValidDuration(Integer durationMinutes) {
        return durationMinutes != null && durationMinutes >= 5 && durationMinutes <= 480;
    }

    /**
     * Validates star rating for customer reviews (1 to 5 stars).
     */
    public static boolean isValidRating(Integer rating) {
        return rating != null && rating >= 1 && rating <= 5;
    }

    /**
     * Validates review comment: 10 to 1000 characters, no database delimiters.
     */
    public static boolean isValidComment(String comment) {
        if (comment == null) return false;
        String trimmed = comment.trim();
        if (trimmed.length() < 10 || trimmed.length() > 1000) return false;
        return !containsDelimiter(trimmed);
    }

    /**
     * Validates appointment notes: optional, maximum 500 characters, no database delimiters.
     */
    public static boolean isValidNotes(String notes) {
        if (notes == null || notes.trim().isEmpty()) return true;
        String trimmed = notes.trim();
        if (trimmed.length() > 500) return false;
        return !containsDelimiter(trimmed);
    }

    /**
     * Validates service category: must be HAIR, SKIN, or NAIL.
     */
    public static boolean isValidCategory(String category) {
        if (category == null) return false;
        String trimmed = category.trim().toUpperCase();
        return trimmed.equals("HAIR") || trimmed.equals("SKIN") || trimmed.equals("NAIL");
    }

    /**
     * Validates membership type: must be REGULAR or PREMIUM.
     */
    public static boolean isValidMembershipType(String type) {
        if (type == null) return false;
        String trimmed = type.trim().toUpperCase();
        return trimmed.equals("REGULAR") || trimmed.equals("PREMIUM");
    }

    /**
     * Validates administrative roles: SALON_OWNER or RECEPTIONIST.
     */
    public static boolean isValidAdminRole(String role) {
        if (role == null) return false;
        String trimmed = role.trim().toUpperCase();
        return trimmed.equals("SALON_OWNER") || trimmed.equals("RECEPTIONIST");
    }

    /**
     * Validates that an appointment date is not in the past.
     */
    public static boolean isValidFutureOrTodayDate(java.time.LocalDate date) {
        if (date == null) return false;
        return !date.isBefore(java.time.LocalDate.now());
    }
}
