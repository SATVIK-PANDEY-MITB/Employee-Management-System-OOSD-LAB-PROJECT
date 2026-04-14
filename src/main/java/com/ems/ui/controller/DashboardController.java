package com.ems.ui.controller;

import com.ems.service.EmployeeService;
import com.ems.service.DepartmentService;
import com.ems.service.AttendanceService;
import com.ems.util.AlertUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Comparator;
import java.util.Map;

/**
 * Dashboard Controller for displaying key metrics
 */
public class DashboardController {
    private EmployeeService employeeService;
    private DepartmentService departmentService;
    private AttendanceService attendanceService;
    private Label totalEmployeesValueLabel;
    private Label activeEmployeesValueLabel;
    private Label totalDepartmentsValueLabel;
    private Label totalSalaryValueLabel;
    private Label departmentBreakdownLabel;
    private Label attendanceTrendLabel;

    public DashboardController(EmployeeService employeeService, DepartmentService departmentService, AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.attendanceService = attendanceService;
    }

    /**
     * Create and return dashboard view
     */
    public VBox getDashboardView() {
        VBox mainBox = new VBox(20);
        mainBox.setStyle("-fx-padding: 20;");

        // Title
        Label titleLabel = new Label("Employee Management Dashboard");
        titleLabel.setFont(new Font("Arial", 28));
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Statistics HBox
        HBox statsBox = new HBox(20);
        statsBox.setStyle("-fx-padding: 20; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #cccccc;");
        statsBox.setPrefHeight(150);

        // Total Employees Card
        totalEmployeesValueLabel = new Label();
        VBox employeeCard = createStatCard("Total Employees", totalEmployeesValueLabel, "#3498db");
        
        // Active Employees Card
        activeEmployeesValueLabel = new Label();
        VBox activeCard = createStatCard("Active Employees", activeEmployeesValueLabel, "#27ae60");
        
        // Total Departments Card
        totalDepartmentsValueLabel = new Label();
        VBox deptCard = createStatCard("Total Departments", totalDepartmentsValueLabel, "#e74c3c");
        
        // Total Salary Expense Card
        totalSalaryValueLabel = new Label();
        VBox salaryCard = createStatCard("Total Salary Expense", totalSalaryValueLabel, "#f39c12");

        statsBox.getChildren().addAll(employeeCard, activeCard, deptCard, salaryCard);

        // Welcome message
        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-padding: 20; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #e8e8e8; -fx-background-color: #f5f5f5;");

        Label welcomeLabel = new Label("Welcome to Employee Management System");
        welcomeLabel.setFont(new Font("Arial", 18));
        welcomeLabel.setStyle("-fx-font-weight: bold;");

        Label infoDetails = new Label(
                "This system helps you manage employees, departments, and attendance records.\n" +
                "Use the tabs above to navigate through different sections.\n\n" +
                "Features:\n" +
                "• Employee Management - Add, view, update, and delete employees\n" +
                "• Department Management - Manage departments\n" +
                "• Attendance Tracking - Track employee attendance records"
        );
        infoDetails.setWrapText(true);

        infoBox.getChildren().addAll(welcomeLabel, infoDetails);

        VBox analyticsBox = createAnalyticsBox();

        mainBox.getChildren().addAll(titleLabel, statsBox, infoBox, analyticsBox);
        refreshDashboard();
        return mainBox;
    }

    private VBox createAnalyticsBox() {
        VBox analyticsBox = new VBox(8);
        analyticsBox.setStyle("-fx-padding: 20; -fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #cccccc; -fx-background-color: #fafafa;");

        HBox analyticsHeader = new HBox(12);
        analyticsHeader.setAlignment(Pos.CENTER_LEFT);

        Label analyticsTitle = new Label("Analytics");
        analyticsTitle.setFont(new Font("Arial", 16));
        analyticsTitle.setStyle("-fx-font-weight: bold;");

        Button refreshAnalyticsButton = new Button("Refresh Analytics");
        refreshAnalyticsButton.setOnAction(e -> refreshDashboard());
        refreshAnalyticsButton.setStyle("-fx-padding: 6 12;");

        analyticsHeader.getChildren().addAll(analyticsTitle, refreshAnalyticsButton);

        departmentBreakdownLabel = new Label();
        departmentBreakdownLabel.setWrapText(true);

        attendanceTrendLabel = new Label();
        attendanceTrendLabel.setWrapText(true);

        analyticsBox.getChildren().addAll(analyticsHeader, departmentBreakdownLabel, attendanceTrendLabel);
        return analyticsBox;
    }

    private String buildDepartmentBreakdown() {
        StringBuilder builder = new StringBuilder("Department Headcount:\n");
        java.util.List<String> departments = employeeService.getAllDepartments();
        if (departments.isEmpty()) {
            return "Department Headcount: No departments available";
        }

        departments.stream()
                .sorted(Comparator.naturalOrder())
                .forEach(department -> builder
                        .append("- ")
                        .append(department)
                        .append(": ")
                        .append(employeeService.getEmployeesByDepartment(department).size())
                        .append("\n"));
        return builder.toString();
    }

    private String buildAttendanceTrend() {
        StringBuilder builder = new StringBuilder("Monthly Attendance Trend:\n");
        Map<String, Integer> trend = attendanceService.getMonthlyAttendanceTrend();
        if (trend.isEmpty()) {
            return "Monthly Attendance Trend: No attendance records available";
        }

        trend.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> builder
                        .append("- ")
                        .append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n"));
        return builder.toString();
    }

    /**
     * Create a statistic card
     */
    private VBox createStatCard(String title, Label valueLabel, String color) {
        VBox card = new VBox(10);
        card.setStyle("-fx-padding: 20; -fx-border-style: solid; -fx-border-width: 1; -fx-border-radius: 5; " +
                "-fx-background-color: " + color + "; -fx-border-color: " + color + ";");
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setFont(new Font("Arial", 14));
        titleLabel.setStyle("-fx-text-fill: white;");

        valueLabel.setFont(new Font("Arial", 24));
        valueLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    /**
     * Refresh dashboard values from current in-memory data.
     */
    public void refreshDashboard() {
        if (totalEmployeesValueLabel != null) {
            totalEmployeesValueLabel.setText(String.valueOf(employeeService.getTotalEmployees()));
        }
        if (activeEmployeesValueLabel != null) {
            activeEmployeesValueLabel.setText(String.valueOf(employeeService.getActiveEmployees().size()));
        }
        if (totalDepartmentsValueLabel != null) {
            totalDepartmentsValueLabel.setText(String.valueOf(departmentService.getTotalDepartments()));
        }
        if (totalSalaryValueLabel != null) {
            totalSalaryValueLabel.setText("₹" + String.format("%.2f", employeeService.getTotalSalaryExpense()));
        }
        if (departmentBreakdownLabel != null) {
            departmentBreakdownLabel.setText(buildDepartmentBreakdown());
        }
        if (attendanceTrendLabel != null) {
            attendanceTrendLabel.setText(buildAttendanceTrend());
        }
    }
}
