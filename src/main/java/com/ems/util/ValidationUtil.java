package com.ems.util;

/**
 * Utility class for input validation
 */
public class ValidationUtil {

    /**
     * Validate if name is valid
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z\\s]+$");
    }

    /**
     * Validate if email is valid
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * Validate if phone number is valid
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10}$");
    }

    /**
     * Validate if salary is valid
     */
    public static boolean isValidSalary(String salary) {
        try {
            double sal = Double.parseDouble(salary);
            return sal > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate if employee ID is valid
     */
    public static boolean isValidId(String id) {
        try {
            int empId = Integer.parseInt(id);
            return empId > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate if date is valid (YYYY-MM-DD format)
     */
    public static boolean isValidDate(String date) {
        return date != null && date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }

    /**
     * Check if string is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
