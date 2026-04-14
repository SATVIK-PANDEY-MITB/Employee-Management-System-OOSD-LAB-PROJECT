package com.ems.service;

import com.ems.model.Department;
import com.ems.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DepartmentService class for managing department operations.
 */
public class DepartmentService {
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final AuditService auditService = new AuditService();

    public DepartmentService() {
        seedSampleData();
    }

    private void seedSampleData() {
        if (databaseManager.isTableEmpty("departments")) {
            addDepartmentInternal(new Department(101, "Information Technology", "Rajesh Kumar", 25, "Handles all IT operations"));
            addDepartmentInternal(new Department(102, "Human Resources", "Amit Patel", 10, "Manages HR and recruitment"));
            addDepartmentInternal(new Department(103, "Finance", "Anjali Sharma", 15, "Financial operations and accounting"));
            addDepartmentInternal(new Department(104, "Marketing", "Vikram Singh", 12, "Marketing and brand management"));
        }
    }

    public boolean addDepartment(String name, String head, int totalEmployees, String description) {
        Department department = new Department(getNextDepartmentId(), name, head, totalEmployees, description);
        boolean saved = addDepartmentInternal(department);
        if (saved) {
            auditService.log("system", "ADD_DEPARTMENT", department.toString());
        }
        return saved;
    }

    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM departments ORDER BY department_id")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    departments.add(mapDepartment(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load departments", e);
        }
        return departments;
    }

    public Department getDepartmentById(int id) {
        return getAllDepartments().stream().filter(department -> department.getDepartmentId() == id).findFirst().orElse(null);
    }

    public boolean updateDepartment(int id, String name, String head, int totalEmployees, String description) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE departments SET department_name = ?, head = ?, total_employees = ?, description = ? WHERE department_id = ?")) {
            statement.setString(1, name);
            statement.setString(2, head);
            statement.setInt(3, totalEmployees);
            statement.setString(4, description);
            statement.setInt(5, id);
            int updated = statement.executeUpdate();
            if (updated > 0) {
                auditService.log("system", "UPDATE_DEPARTMENT", String.valueOf(id));
            }
            return updated > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update department", e);
        }
    }

    public boolean deleteDepartment(int id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM departments WHERE department_id = ?")) {
            statement.setInt(1, id);
            int deleted = statement.executeUpdate();
            if (deleted > 0) {
                auditService.log("system", "DELETE_DEPARTMENT", String.valueOf(id));
            }
            return deleted > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete department", e);
        }
    }

    public int getTotalDepartments() {
        return getAllDepartments().size();
    }

    private int getNextDepartmentId() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(department_id), 100) + 1 FROM departments")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 101;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to calculate next department id", e);
        }
    }

    private boolean addDepartmentInternal(Department department) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO departments(department_id, department_name, head, total_employees, description) VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, department.getDepartmentId());
            statement.setString(2, department.getDepartmentName());
            statement.setString(3, department.getHead());
            statement.setInt(4, department.getTotalEmployees());
            statement.setString(5, department.getDescription());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save department", e);
        }
    }

    private Department mapDepartment(ResultSet resultSet) throws SQLException {
        return new Department(
                resultSet.getInt("department_id"),
                resultSet.getString("department_name"),
                resultSet.getString("head"),
                resultSet.getInt("total_employees"),
                resultSet.getString("description")
        );
    }
}
