package com.ems.service;

import com.ems.model.Employee;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeServiceTest {
    static {
        System.setProperty("ems.db.url", "jdbc:h2:mem:ems_test;DB_CLOSE_DELAY=-1");
    }

    private final EmployeeService employeeService = new EmployeeService();

    @BeforeAll
    static void prepare() {
        System.setProperty("ems.db.url", "jdbc:h2:mem:ems_test;DB_CLOSE_DELAY=-1");
    }

    @Test
    void addsUpdatesAndDeletesEmployee() {
        int initialCount = employeeService.getTotalEmployees();
        assertTrue(employeeService.addEmployee("Test User", "Developer", 12345, "IT", "9999999999", "test@company.com", "Active", "2026-04-14"));

        Employee savedEmployee = employeeService.getAllEmployees().stream()
                .filter(employee -> "Test User".equals(employee.getEmployeeName()))
                .findFirst()
                .orElse(null);
        assertNotNull(savedEmployee);

        assertTrue(employeeService.updateEmployee(savedEmployee.getEmployeeId(), "Test User", "Lead Developer", 15000, "IT", "9999999999", "test@company.com", "Active"));
        assertTrue(employeeService.deleteEmployee(savedEmployee.getEmployeeId()));
        assertEquals(initialCount, employeeService.getTotalEmployees());
    }
}