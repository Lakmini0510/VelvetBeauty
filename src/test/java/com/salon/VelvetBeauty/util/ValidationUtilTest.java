package com.salon.VelvetBeauty.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "first.last@domain.co.uk", "salon_test+sub@gmail.com"})
    @DisplayName("Valid emails should pass validation")
    void testValidEmails(String email) {
        assertTrue(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "plainaddress", "@missinguser.com", "user@.com", "user@domain", ""})
    @DisplayName("Invalid emails should fail validation")
    void testInvalidEmails(String email) {
        assertFalse(ValidationUtil.isValidEmail(email));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0771234567", "+94771234567", "+1 (555) 012-3456", "011-2345678", "071 999 8888"})
    @DisplayName("Valid phone numbers should pass validation")
    void testValidPhones(String phone) {
        assertTrue(ValidationUtil.isValidPhone(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "abcdef", "12-34", "+--()", ""})
    @DisplayName("Invalid phone numbers should fail validation")
    void testInvalidPhones(String phone) {
        assertFalse(ValidationUtil.isValidPhone(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"clara_rose", "admin123", "stylist_01"})
    @DisplayName("Valid usernames should pass validation")
    void testValidUsernames(String username) {
        assertTrue(ValidationUtil.isValidUsername(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "user with spaces", "user@name!", ""})
    @DisplayName("Invalid usernames should fail validation")
    void testInvalidUsernames(String username) {
        assertFalse(ValidationUtil.isValidUsername(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {"secret123", "p@ssword", "correct horse battery staple"})
    @DisplayName("Valid passwords should pass validation")
    void testValidPasswords(String password) {
        assertTrue(ValidationUtil.isValidPassword(password));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "pass;;word", ""})
    @DisplayName("Passwords with delimiters or too short should fail validation")
    void testInvalidPasswords(String password) {
        assertFalse(ValidationUtil.isValidPassword(password));
    }

    @Test
    @DisplayName("Delimiter safety check detects ';;'")
    void testDelimiterCheck() {
        assertTrue(ValidationUtil.containsDelimiter("clean text", "malicious;;injection"));
        assertFalse(ValidationUtil.containsDelimiter("clean text", "another normal field"));
    }
}
