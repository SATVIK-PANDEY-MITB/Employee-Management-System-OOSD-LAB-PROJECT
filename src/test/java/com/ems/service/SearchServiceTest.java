package com.ems.service;

import com.ems.model.Employee;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchServiceTest {
    private final SearchService searchService = new SearchService();

    @Test
    void searchesEmployeesByNameAndDepartment() {
        List<Employee> employees = List.of(
                new Employee(1, "Rajesh Kumar", "Manager", 50000, "IT", "9876543210", "rajesh@company.com", "Active", "2022-01-15"),
                new Employee(2, "Priya Singh", "Developer", 40000, "HR", "9876543211", "priya@company.com", "Active", "2022-03-20")
        );

        List<Employee> byName = searchService.searchEmployees(employees, "rajesh");
        assertEquals(1, byName.size());
        assertEquals("Rajesh Kumar", byName.get(0).getEmployeeName());

        List<Employee> byDepartment = searchService.searchEmployees(employees, "hr");
        assertEquals(1, byDepartment.size());
        assertEquals("Priya Singh", byDepartment.get(0).getEmployeeName());
    }

    @Test
    void sortsEmployeesByName() {
        List<Employee> employees = List.of(
                new Employee(2, "Zara", "Developer", 40000, "IT", "9876543211", "zara@company.com", "Active", "2022-03-20"),
                new Employee(1, "Aaron", "Manager", 50000, "HR", "9876543210", "aaron@company.com", "Active", "2022-01-15")
        );

        List<Employee> sorted = searchService.sortEmployeesByName(employees);
        assertTrue(sorted.get(0).getEmployeeName().compareToIgnoreCase(sorted.get(1).getEmployeeName()) <= 0);
    }
}