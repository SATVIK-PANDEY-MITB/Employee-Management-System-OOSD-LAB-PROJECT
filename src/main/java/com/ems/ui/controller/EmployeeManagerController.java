package com.ems.ui.controller;

import com.ems.model.Employee;
import com.ems.service.ExportService;
import com.ems.service.EmployeeService;
import com.ems.service.SearchService;
import com.ems.util.AlertUtil;
import com.ems.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.ScrollPane;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Employee Manager Controller for managing employee operations
 */
public class EmployeeManagerController {
    private EmployeeService employeeService;
    private SearchService searchService = new SearchService();
    private ExportService exportService = new ExportService();
    private TableView<Employee> employeeTable;

    public EmployeeManagerController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Create and return employee management view
     */
    public ScrollPane getEmployeeManagementView() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(15));

        // Title
        Label titleLabel = new Label("Employee Management");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-font-weight: bold;");

        VBox searchSection = createSearchSection();

        // Form Section
        VBox formSection = createEmployeeForm();

        // Action Buttons
        HBox actionBox = createActionButtons();

        // Table Section
        VBox tableSection = createTableSection();

        // Keep list visible near top like earlier versions.
        mainBox.getChildren().addAll(titleLabel, searchSection, actionBox, tableSection, formSection);
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(mainBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private VBox createSearchSection() {
        VBox searchBox = new VBox(8);
        searchBox.setPadding(new Insets(0, 0, 5, 0));

        HBox row = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search employees by name, department, email, or ID");
        searchField.setPrefWidth(360);

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> handleSearch(searchField.getText()));

        Button clearButton = new Button("Clear Search");
        clearButton.setOnAction(e -> {
            searchField.clear();
            refreshEmployeeTable();
        });

        Button exportCsvButton = new Button("Export CSV");
        exportCsvButton.setOnAction(e -> handleExport("csv"));

        Button exportPdfButton = new Button("Export PDF");
        exportPdfButton.setOnAction(e -> handleExport("pdf"));

        Button editButton = new Button("Edit Selected");
        editButton.setOnAction(e -> handleEditSelectedEmployee());

        row.getChildren().addAll(searchField, searchButton, clearButton, exportCsvButton, exportPdfButton, editButton);
        searchBox.getChildren().add(row);
        return searchBox;
    }

    /**
     * Create employee form
     */
    private VBox createEmployeeForm() {
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #cccccc; -fx-padding: 15; -fx-background-color: #f9f9f9;");

        Label formTitle = new Label("Add / Update Employee");
        formTitle.setFont(new Font("Arial", 16));
        formTitle.setStyle("-fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Employee ID (Read-only, for viewing)
        Label idLabel = new Label("Employee ID:");
        TextField idField = new TextField();
        idField.setPromptText("Auto-generated");
        idField.setDisable(true);

        // Name
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter employee name");

        // Designation
        Label desigLabel = new Label("Designation:");
        ComboBox<String> designationCombo = new ComboBox<>();
        designationCombo.setItems(FXCollections.observableArrayList(
                "Manager", "Developer", "Analyst", "HR Manager", "Finance Manager", "Interns"
        ));
        designationCombo.setPrefWidth(200);

        // Salary
        Label salaryLabel = new Label("Salary:");
        TextField salaryField = new TextField();
        salaryField.setPromptText("Enter salary");

        // Department
        Label deptLabel = new Label("Department:");
        ComboBox<String> departmentCombo = new ComboBox<>();
        departmentCombo.setItems(FXCollections.observableArrayList(employeeService.getAllDepartments()));
        departmentCombo.setPrefWidth(200);

        // Phone Number
        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("10-digit phone number");

        // Email
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email address");

        // Status
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive", "On Leave"));
        statusCombo.setPrefWidth(200);

        // Joining Date
        Label dateLabel = new Label("Joining Date (YYYY-MM-DD):");
        TextField dateField = new TextField();
        dateField.setPromptText("2026-04-07");

        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(desigLabel, 0, 2);
        grid.add(designationCombo, 1, 2);
        grid.add(salaryLabel, 0, 3);
        grid.add(salaryField, 1, 3);
        grid.add(deptLabel, 0, 4);
        grid.add(departmentCombo, 1, 4);
        grid.add(phoneLabel, 0, 5);
        grid.add(phoneField, 1, 5);
        grid.add(emailLabel, 0, 6);
        grid.add(emailField, 1, 6);
        grid.add(statusLabel, 0, 7);
        grid.add(statusCombo, 1, 7);
        grid.add(dateLabel, 0, 8);
        grid.add(dateField, 1, 8);

        HBox formButtonBox = new HBox(10);
        formButtonBox.setPadding(new Insets(10, 0, 0, 0));

        Button addButton = new Button("Add Employee");
        addButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        addButton.setOnAction(e -> handleAddEmployee(nameField, designationCombo, salaryField, departmentCombo, phoneField, emailField, statusCombo, dateField));

        Button clearButton = new Button("Clear Form");
        clearButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        clearButton.setOnAction(e -> clearForm(nameField, designationCombo, salaryField, departmentCombo, phoneField, emailField, statusCombo, dateField));

        formButtonBox.getChildren().addAll(addButton, clearButton);

        formBox.getChildren().addAll(formTitle, grid, formButtonBox);
        return formBox;
    }

    /**
     * Create action buttons
     */
    private HBox createActionButtons() {
        HBox actionBox = new HBox(10);
        actionBox.setPadding(new Insets(10));

        Button refreshButton = new Button("Refresh Table");
        refreshButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        refreshButton.setOnAction(e -> refreshEmployeeTable());

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        deleteButton.setOnAction(e -> handleDeleteEmployee());

        Button filterButton = new Button("Filter by Department");
        filterButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        filterButton.setOnAction(e -> handleFilterByDepartment());

        Button showAllButton = new Button("Show All Employees");
        showAllButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        showAllButton.setOnAction(e -> refreshEmployeeTable());

        actionBox.getChildren().addAll(refreshButton, deleteButton, filterButton, showAllButton);
        return actionBox;
    }

    /**
     * Create table section
     */
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));

        Label tableTitle = new Label("Employee List");
        tableTitle.setFont(new Font("Arial", 16));
        tableTitle.setStyle("-fx-font-weight: bold;");

        employeeTable = new TableView<>();
        employeeTable.setPrefHeight(400);
        employeeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Create columns
        TableColumn<Employee, Integer> idColumn = new TableColumn<>("Employee ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEmployeeId()));
        idColumn.setMaxWidth(110);

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmployeeName()));

        TableColumn<Employee, String> designationColumn = new TableColumn<>("Designation");
        designationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDesignation()));

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getSalary()));
        salaryColumn.setMaxWidth(130);

        TableColumn<Employee, String> deptColumn = new TableColumn<>("Department");
        deptColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartment()));

        TableColumn<Employee, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhoneNumber()));

        TableColumn<Employee, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));
        statusColumn.setMaxWidth(120);

        employeeTable.getColumns().addAll(idColumn, nameColumn, designationColumn, salaryColumn, deptColumn, phoneColumn, statusColumn);

        refreshEmployeeTable();

        tableBox.getChildren().addAll(tableTitle, employeeTable);
        return tableBox;
    }

    /**
     * Refresh employee table
     */
    private void refreshEmployeeTable() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            ObservableList<Employee> data = FXCollections.observableArrayList(employees);
            employeeTable.setItems(data);
        } catch (Exception exception) {
            AlertUtil.showError("Employee Load Error", "Could not load employees: " + exception.getMessage());
        }
    }

    /**
     * Public refresh hook for tab navigation events.
     */
    public void refreshView() {
        refreshEmployeeTable();
    }

    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            refreshEmployeeTable();
            return;
        }
        List<Employee> results = searchService.searchEmployees(employeeService.getAllEmployees(), query);
        employeeTable.setItems(FXCollections.observableArrayList(results));
    }

    private void handleExport(String format) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            List<Employee> employeesToExport = employeeService.getAllEmployees();
            if (employeesToExport.isEmpty()) {
                AlertUtil.showWarning("Export", "No employees available to export.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName("employees_" + timestamp + ("pdf".equalsIgnoreCase(format) ? ".pdf" : ".csv"));
            File initialDir = new File("exports");
            if (!initialDir.exists()) {
                initialDir.mkdirs();
            }
            chooser.setInitialDirectory(initialDir);

            if ("pdf".equalsIgnoreCase(format)) {
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
                File selectedFile = chooser.showSaveDialog(employeeTable.getScene().getWindow());
                if (selectedFile == null) {
                    return;
                }
                List<String> lines = employeesToExport.stream()
                        .map(emp -> String.format("ID:%d | Name:%s | Designation:%s | Salary:%.2f | Department:%s | Phone:%s | Email:%s | Status:%s | Joining:%s",
                                emp.getEmployeeId(), emp.getEmployeeName(), emp.getDesignation(), emp.getSalary(), emp.getDepartment(),
                                emp.getPhoneNumber(), emp.getEmail(), emp.getStatus(), emp.getJoiningDate()))
                        .collect(Collectors.toList());
                Path target = selectedFile.toPath();
                exportService.exportTextToPdf("Employee Report", lines, target);
                AlertUtil.showSuccess("Export Complete", "Employee PDF exported to " + target.toAbsolutePath());
            } else {
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
                File selectedFile = chooser.showSaveDialog(employeeTable.getScene().getWindow());
                if (selectedFile == null) {
                    return;
                }
                Path target = selectedFile.toPath();
                exportService.exportEmployeesToCsv(employeesToExport, target);
                AlertUtil.showSuccess("Export Complete", "Employee CSV exported to " + target.toAbsolutePath());
            }
        } catch (IOException exception) {
            AlertUtil.showError("Export Error", exception.getMessage());
        } catch (Exception exception) {
            AlertUtil.showError("Export Error", "Failed to export data: " + exception.getMessage());
        }
    }

    private void handleEditSelectedEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Warning", "Please select an employee to edit");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Employee");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField(selected.getEmployeeName());
        TextField designationField = new TextField(selected.getDesignation());
        TextField salaryField = new TextField(String.valueOf(selected.getSalary()));
        TextField departmentField = new TextField(selected.getDepartment());
        TextField phoneField = new TextField(selected.getPhoneNumber());
        TextField emailField = new TextField(selected.getEmail());
        TextField statusField = new TextField(selected.getStatus());

        VBox content = new VBox(8,
                new Label("Name"), nameField,
                new Label("Designation"), designationField,
                new Label("Salary"), salaryField,
                new Label("Department"), departmentField,
                new Label("Phone"), phoneField,
                new Label("Email"), emailField,
                new Label("Status"), statusField);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (!ValidationUtil.isValidName(nameField.getText()) || !ValidationUtil.isValidSalary(salaryField.getText())
                        || !ValidationUtil.isValidPhone(phoneField.getText()) || !ValidationUtil.isValidEmail(emailField.getText())) {
                    AlertUtil.showError("Validation Error", "Please correct the fields before saving");
                    return;
                }
                if (employeeService.updateEmployee(selected.getEmployeeId(), nameField.getText(), designationField.getText(),
                        Double.parseDouble(salaryField.getText()), departmentField.getText(), phoneField.getText(),
                        emailField.getText(), statusField.getText())) {
                    refreshEmployeeTable();
                    AlertUtil.showSuccess("Success", "Employee updated successfully!");
                } else {
                    AlertUtil.showError("Error", "Failed to update employee");
                }
            }
        });
    }

    /**
     * Handle add employee
     */
    private void handleAddEmployee(TextField nameField, ComboBox<String> designationCombo, TextField salaryField,
                                   ComboBox<String> departmentCombo, TextField phoneField, TextField emailField,
                                   ComboBox<String> statusCombo, TextField dateField) {
        String name = nameField.getText();
        String designation = designationCombo.getValue();
        String salary = salaryField.getText();
        String department = departmentCombo.getValue();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String status = statusCombo.getValue();
        String joiningDate = dateField.getText();

        // Validation
        if (ValidationUtil.isEmpty(name)) {
            AlertUtil.showError("Validation Error", "Please enter employee name");
            return;
        }
        if (!ValidationUtil.isValidName(name)) {
            AlertUtil.showError("Validation Error", "Name should contain only letters");
            return;
        }
        if (designation == null) {
            AlertUtil.showError("Validation Error", "Please select designation");
            return;
        }
        if (!ValidationUtil.isValidSalary(salary)) {
            AlertUtil.showError("Validation Error", "Please enter valid salary");
            return;
        }
        if (department == null) {
            AlertUtil.showError("Validation Error", "Please select department");
            return;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            AlertUtil.showError("Validation Error", "Phone number must be 10 digits");
            return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            AlertUtil.showError("Validation Error", "Please enter valid email address");
            return;
        }
        if (status == null) {
            AlertUtil.showError("Validation Error", "Please select status");
            return;
        }
        if (!ValidationUtil.isValidDate(joiningDate)) {
            AlertUtil.showError("Validation Error", "Date must be in YYYY-MM-DD format");
            return;
        }

        if (employeeService.addEmployee(name, designation, Double.parseDouble(salary), department, phone, email, status, joiningDate)) {
            AlertUtil.showSuccess("Success", "Employee added successfully!");
            clearForm(nameField, designationCombo, salaryField, departmentCombo, phoneField, emailField, statusCombo, dateField);
            refreshEmployeeTable();
        } else {
            AlertUtil.showError("Error", "Failed to add employee");
        }
    }

    /**
     * Handle delete employee
     */
    private void handleDeleteEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Warning", "Please select an employee to delete");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Delete", "Are you sure you want to delete " + selected.getEmployeeName() + "?")) {
            if (employeeService.deleteEmployee(selected.getEmployeeId())) {
                AlertUtil.showSuccess("Success", "Employee deleted successfully!");
                refreshEmployeeTable();
            } else {
                AlertUtil.showError("Error", "Failed to delete employee");
            }
        }
    }

    /**
     * Handle filter by department
     */
    private void handleFilterByDepartment() {
        ObservableList<String> departmentOptions = FXCollections.observableArrayList();
        departmentOptions.add("All Departments");
        departmentOptions.addAll(employeeService.getAllDepartments());

        ChoiceDialog<String> dialog = new ChoiceDialog<>("All Departments", departmentOptions);
        dialog.setTitle("Filter by Department");
        dialog.setHeaderText("Select a department");
        dialog.setContentText("Department:");

        dialog.showAndWait().ifPresent(selected -> {
            if ("All Departments".equals(selected)) {
                refreshEmployeeTable();
            } else {
                List<Employee> filtered = employeeService.getEmployeesByDepartment(selected);
                ObservableList<Employee> data = FXCollections.observableArrayList(filtered);
                employeeTable.setItems(data);
            }
        });
    }

    /**
     * Clear form
     */
    private void clearForm(TextField nameField, ComboBox<String> designationCombo, TextField salaryField,
                          ComboBox<String> departmentCombo, TextField phoneField, TextField emailField,
                          ComboBox<String> statusCombo, TextField dateField) {
        nameField.clear();
        designationCombo.setValue(null);
        salaryField.clear();
        departmentCombo.setValue(null);
        phoneField.clear();
        emailField.clear();
        statusCombo.setValue(null);
        dateField.clear();
    }
}
