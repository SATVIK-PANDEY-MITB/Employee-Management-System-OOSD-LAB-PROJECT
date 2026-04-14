package com.ems.service;

import com.ems.model.Employee;
import com.ems.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * EmployeeService class for managing employee operations.
 */
public class EmployeeService {
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final AuditService auditService = new AuditService();

    public EmployeeService() {
        seedSampleData();
    }

    private void seedSampleData() {
        if (databaseManager.isTableEmpty("employees")) {
            addEmployeeInternal(new Employee(1001, "Rajesh Kumar", "Manager", 50000, "IT", "9876543210", "rajesh@company.com", "Active", "2022-01-15"));
            addEmployeeInternal(new Employee(1002, "Priya Singh", "Developer", 40000, "IT", "9876543211", "priya@company.com", "Active", "2022-03-20"));
            addEmployeeInternal(new Employee(1003, "Amit Patel", "HR Manager", 45000, "HR", "9876543212", "amit@company.com", "Active", "2021-07-10"));
        }
    }

    public boolean addEmployee(String name, String designation, double salary, String department,
                               String phone, String email, String status, String joiningDate) {
        int nextEmployeeId = getNextEmployeeId();
        Employee employee = new Employee(nextEmployeeId, name, designation, salary, department, phone, email, status, joiningDate);
        boolean saved = addEmployeeInternal(employee);
        if (saved) {
            auditService.log("system", "ADD_EMPLOYEE", employee.toString());
        }
        return saved;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees ORDER BY employee_id")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employees.add(mapEmployee(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load employees", e);
        }
        return employees;
    }

    public Employee getEmployeeById(int id) {
        return getAllEmployees().stream().filter(emp -> emp.getEmployeeId() == id).findFirst().orElse(null);
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return getAllEmployees().stream()
                .filter(emp -> emp.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public List<Employee> getActiveEmployees() {
        return getAllEmployees().stream()
                .filter(emp -> emp.getStatus().equalsIgnoreCase("Active"))
                .collect(Collectors.toList());
    }

    public boolean updateEmployee(int id, String name, String designation, double salary,
                                  String department, String phone, String email, String status) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE employees SET employee_name = ?, designation = ?, salary = ?, department = ?, phone_number = ?, email = ?, status = ? WHERE employee_id = ?")) {
            statement.setString(1, name);
            statement.setString(2, designation);
            statement.setDouble(3, salary);
            statement.setString(4, department);
            statement.setString(5, phone);
            statement.setString(6, email);
            statement.setString(7, status);
            statement.setInt(8, id);
            int updated = statement.executeUpdate();
            if (updated > 0) {
                auditService.log("system", "UPDATE_EMPLOYEE", String.valueOf(id));
            }
            return updated > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update employee", e);
        }
    }

    public boolean deleteEmployee(int id) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM employees WHERE employee_id = ?")) {
            statement.setInt(1, id);
            int deleted = statement.executeUpdate();
            if (deleted > 0) {
                auditService.log("system", "DELETE_EMPLOYEE", String.valueOf(id));
            }
            return deleted > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete employee", e);
        }
    }

    public int getTotalEmployees() {
        return getAllEmployees().size();
    }

    public List<String> getAllDepartments() {
        Set<String> departments = new HashSet<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT department_name FROM departments UNION SELECT department FROM employees ORDER BY 1")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    departments.add(resultSet.getString(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load departments", e);
        }
        return new ArrayList<>(departments);
    }

    public double getTotalSalaryExpense() {
        return getActiveEmployees().stream().mapToDouble(Employee::getSalary).sum();
    }

    private int getNextEmployeeId() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(employee_id), 1000) + 1 FROM employees")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 1001;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to calculate next employee id", e);
        }
    }

    private boolean addEmployeeInternal(Employee employee) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO employees(employee_id, employee_name, designation, salary, department, phone_number, email, status, joining_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setInt(1, employee.getEmployeeId());
            statement.setString(2, employee.getEmployeeName());
            statement.setString(3, employee.getDesignation());
            statement.setDouble(4, employee.getSalary());
            statement.setString(5, employee.getDepartment());
            statement.setString(6, employee.getPhoneNumber());
            statement.setString(7, employee.getEmail());
            statement.setString(8, employee.getStatus());
            statement.setString(9, employee.getJoiningDate());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save employee", e);
        }
    }

    private Employee mapEmployee(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt("employee_id"),
                resultSet.getString("employee_name"),
                resultSet.getString("designation"),
                resultSet.getDouble("salary"),
                resultSet.getString("department"),
                resultSet.getString("phone_number"),
                resultSet.getString("email"),
                resultSet.getString("status"),
                resultSet.getString("joining_date")
        );
    }
}
