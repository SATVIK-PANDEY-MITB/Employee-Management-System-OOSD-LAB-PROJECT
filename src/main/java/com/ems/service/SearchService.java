package com.ems.service;

import com.ems.model.Attendance;
import com.ems.model.Department;
import com.ems.model.Employee;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Provides search and sort helpers for UI tables.
 */
public class SearchService {
    public List<Employee> searchEmployees(List<Employee> employees, String query) {
        String normalized = normalize(query);
        if (normalized.isEmpty()) {
            return employees;
        }
        return employees.stream()
                .filter(employee -> matchesEmployee(employee, normalized))
                .collect(Collectors.toList());
    }

    public List<Employee> sortEmployeesByName(List<Employee> employees) {
        return employees.stream().sorted(Comparator.comparing(Employee::getEmployeeName, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
    }

    public List<Department> searchDepartments(List<Department> departments, String query) {
        String normalized = normalize(query);
        if (normalized.isEmpty()) {
            return departments;
        }
        return departments.stream()
                .filter(department -> contains(department.getDepartmentName(), normalized) || contains(department.getHead(), normalized))
                .collect(Collectors.toList());
    }

    public List<Attendance> searchAttendance(List<Attendance> records, String query) {
        String normalized = normalize(query);
        if (normalized.isEmpty()) {
            return records;
        }
        return records.stream()
                .filter(record -> contains(String.valueOf(record.getEmployeeId()), normalized)
                        || contains(record.getDate(), normalized)
                        || contains(record.getStatus(), normalized)
                        || contains(record.getRemarks(), normalized))
                .collect(Collectors.toList());
    }

    private boolean matchesEmployee(Employee employee, String query) {
        return contains(String.valueOf(employee.getEmployeeId()), query)
                || contains(employee.getEmployeeName(), query)
                || contains(employee.getDesignation(), query)
                || contains(employee.getDepartment(), query)
                || contains(employee.getEmail(), query)
                || contains(employee.getPhoneNumber(), query)
                || contains(employee.getStatus(), query);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}