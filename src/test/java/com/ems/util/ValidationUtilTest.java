package com.ems.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationUtilTest {

    @Test
    void validatesEmployeeName() {
        assertTrue(ValidationUtil.isValidName("Rajesh Kumar"));
        assertFalse(ValidationUtil.isValidName("Rajesh123"));
    }

    @Test
    void validatesContactDetails() {
        assertTrue(ValidationUtil.isValidEmail("test@company.com"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertFalse(ValidationUtil.isValidPhone("12345"));
    }

    @Test
    void validatesSalaryAndDate() {
        assertTrue(ValidationUtil.isValidSalary("45000"));
        assertFalse(ValidationUtil.isValidSalary("-10"));
        assertTrue(ValidationUtil.isValidDate("2026-04-14"));
        assertFalse(ValidationUtil.isValidDate("14-04-2026"));
    }
}