package com.ems.ui.controller;

import com.ems.model.Department;
import com.ems.service.ExportService;
import com.ems.service.DepartmentService;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Department Manager Controller for managing department operations
 */
public class DepartmentManagerController {
    private DepartmentService departmentService;
    private EmployeeService employeeService;
    private SearchService searchService = new SearchService();
    private ExportService exportService = new ExportService();
    private TableView<Department> departmentTable;

    public DepartmentManagerController(DepartmentService departmentService, EmployeeService employeeService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    /**
     * Create and return department management view
     */
    public VBox getDepartmentManagementView() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(15));

        // Title
        Label titleLabel = new Label("Department Management");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-font-weight: bold;");

        VBox searchSection = createSearchSection();

        // Form Section
        VBox formSection = createDepartmentForm();

        // Action Buttons
        HBox actionBox = createActionButtons();

        // Table Section
        VBox tableSection = createTableSection();

        mainBox.getChildren().addAll(titleLabel, searchSection, formSection, actionBox, tableSection);
        return mainBox;
    }

    private VBox createSearchSection() {
        VBox searchBox = new VBox(8);
        HBox row = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search departments by name or head");
        searchField.setPrefWidth(320);

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> handleSearch(searchField.getText()));

        Button clearButton = new Button("Clear Search");
        clearButton.setOnAction(e -> {
            searchField.clear();
            refreshDepartmentTable();
        });

        Button exportCsvButton = new Button("Export CSV");
        exportCsvButton.setOnAction(e -> handleExport("csv"));

        Button exportPdfButton = new Button("Export PDF");
        exportPdfButton.setOnAction(e -> handleExport("pdf"));

        Button editButton = new Button("Edit Selected");
        editButton.setOnAction(e -> handleEditSelectedDepartment());

        row.getChildren().addAll(searchField, searchButton, clearButton, exportCsvButton, exportPdfButton, editButton);
        searchBox.getChildren().add(row);
        return searchBox;
    }

    /**
     * Create department form
     */
    private VBox createDepartmentForm() {
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #cccccc; -fx-padding: 15; -fx-background-color: #f9f9f9;");

        Label formTitle = new Label("Add / Update Department");
        formTitle.setFont(new Font("Arial", 16));
        formTitle.setStyle("-fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Department ID (Read-only)
        Label idLabel = new Label("Department ID:");
        TextField idField = new TextField();
        idField.setPromptText("Auto-generated");
        idField.setDisable(true);

        // Department Name
        Label nameLabel = new Label("Department Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter department name");

        // Head of Department
        Label headLabel = new Label("Head of Department:");
        ComboBox<String> headCombo = new ComboBox<>();
        headCombo.setItems(FXCollections.observableArrayList(getEmployeeNames()));
        headCombo.setPrefWidth(200);
        headCombo.setEditable(true);

        // Total Employees
        Label totalLabel = new Label("Total Employees:");
        TextField totalField = new TextField();
        totalField.setPromptText("Enter number of employees");

        // Description
        Label descLabel = new Label("Description:");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Enter department description");
        descArea.setPrefRowCount(4);
        descArea.setWrapText(true);

        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(headLabel, 0, 2);
        grid.add(headCombo, 1, 2);
        grid.add(totalLabel, 0, 3);
        grid.add(totalField, 1, 3);
        grid.add(descLabel, 0, 4);
        grid.add(descArea, 1, 4);

        HBox formButtonBox = new HBox(10);
        formButtonBox.setPadding(new Insets(10, 0, 0, 0));

        Button addButton = new Button("Add Department");
        addButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        addButton.setOnAction(e -> handleAddDepartment(nameField, headCombo, totalField, descArea));

        Button clearButton = new Button("Clear Form");
        clearButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        clearButton.setOnAction(e -> clearForm(nameField, headCombo, totalField, descArea));

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
        refreshButton.setOnAction(e -> refreshDepartmentTable());

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        deleteButton.setOnAction(e -> handleDeleteDepartment());

        actionBox.getChildren().addAll(refreshButton, deleteButton);
        return actionBox;
    }

    /**
     * Create table section
     */
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));

        Label tableTitle = new Label("Department List");
        tableTitle.setFont(new Font("Arial", 16));
        tableTitle.setStyle("-fx-font-weight: bold;");

        departmentTable = new TableView<>();
        departmentTable.setPrefHeight(350);

        // Create columns
        TableColumn<Department, Integer> idColumn = new TableColumn<>("Dept ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDepartmentId()));

        TableColumn<Department, String> nameColumn = new TableColumn<>("Department Name");
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDepartmentName()));

        TableColumn<Department, String> headColumn = new TableColumn<>("Head");
        headColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHead()));

        TableColumn<Department, Integer> empColumn = new TableColumn<>("Total Employees");
        empColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTotalEmployees()));

        TableColumn<Department, String> descColumn = new TableColumn<>("Description");
        descColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));

        departmentTable.getColumns().addAll(idColumn, nameColumn, headColumn, empColumn, descColumn);

        refreshDepartmentTable();

        tableBox.getChildren().addAll(tableTitle, departmentTable);
        return tableBox;
    }

    /**
     * Refresh department table
     */
    private void refreshDepartmentTable() {
        List<Department> departments = departmentService.getAllDepartments();
        ObservableList<Department> data = FXCollections.observableArrayList(departments);
        departmentTable.setItems(data);
    }

    private void handleSearch(String query) {
        List<Department> results = searchService.searchDepartments(departmentService.getAllDepartments(), query);
        departmentTable.setItems(FXCollections.observableArrayList(results));
    }

    private void handleExport(String format) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            List<Department> departmentsToExport = departmentService.getAllDepartments();
            if (departmentsToExport.isEmpty()) {
                AlertUtil.showWarning("Export", "No departments available to export.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName("departments_" + timestamp + ("pdf".equalsIgnoreCase(format) ? ".pdf" : ".csv"));
            File initialDir = new File("exports");
            if (!initialDir.exists()) {
                initialDir.mkdirs();
            }
            chooser.setInitialDirectory(initialDir);

            if ("pdf".equalsIgnoreCase(format)) {
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
                File selectedFile = chooser.showSaveDialog(departmentTable.getScene().getWindow());
                if (selectedFile == null) {
                    return;
                }
                List<String> lines = departmentsToExport.stream()
                        .map(dept -> String.format("ID:%d | Name:%s | Head:%s | Total Employees:%d | Description:%s",
                                dept.getDepartmentId(), dept.getDepartmentName(), dept.getHead(), dept.getTotalEmployees(), dept.getDescription()))
                        .collect(Collectors.toList());
                java.nio.file.Path target = selectedFile.toPath();
                exportService.exportTextToPdf("Department Report", lines, target);
                AlertUtil.showSuccess("Export Complete", "Department PDF exported to " + target.toAbsolutePath());
            } else {
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
                File selectedFile = chooser.showSaveDialog(departmentTable.getScene().getWindow());
                if (selectedFile == null) {
                    return;
                }
                java.nio.file.Path target = selectedFile.toPath();
                exportService.exportDepartmentsToCsv(departmentsToExport, target);
                AlertUtil.showSuccess("Export Complete", "Department CSV exported to " + target.toAbsolutePath());
            }
        } catch (IOException exception) {
            AlertUtil.showError("Export Error", exception.getMessage());
        } catch (Exception exception) {
            AlertUtil.showError("Export Error", "Failed to export data: " + exception.getMessage());
        }
    }

    private void handleEditSelectedDepartment() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Warning", "Please select a department to edit");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Department");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameField = new TextField(selected.getDepartmentName());
        TextField headField = new TextField(selected.getHead());
        TextField totalField = new TextField(String.valueOf(selected.getTotalEmployees()));
        TextArea descField = new TextArea(selected.getDescription());

        VBox content = new VBox(8,
                new Label("Department Name"), nameField,
                new Label("Head"), headField,
                new Label("Total Employees"), totalField,
                new Label("Description"), descField);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    int totalEmployees = Integer.parseInt(totalField.getText());
                    if (departmentService.updateDepartment(selected.getDepartmentId(), nameField.getText(), headField.getText(), totalEmployees, descField.getText())) {
                        refreshDepartmentTable();
                        AlertUtil.showSuccess("Success", "Department updated successfully!");
                    } else {
                        AlertUtil.showError("Error", "Failed to update department");
                    }
                } catch (NumberFormatException exception) {
                    AlertUtil.showError("Validation Error", "Total employees must be a valid number");
                }
            }
        });
    }

    /**
     * Handle add department
     */
    private void handleAddDepartment(TextField nameField, ComboBox<String> headCombo, TextField totalField, TextArea descArea) {
        String name = nameField.getText();
        String head = headCombo.getValue();
        String total = totalField.getText();
        String description = descArea.getText();

        // Validation
        if (ValidationUtil.isEmpty(name)) {
            AlertUtil.showError("Validation Error", "Please enter department name");
            return;
        }
        if (ValidationUtil.isEmpty(head)) {
            AlertUtil.showError("Validation Error", "Please select or enter head of department");
            return;
        }

        int totalEmp;
        try {
            totalEmp = Integer.parseInt(total);
            if (totalEmp < 0) {
                AlertUtil.showError("Validation Error", "Total employees must be a positive number");
                return;
            }
        } catch (NumberFormatException ex) {
            AlertUtil.showError("Validation Error", "Please enter valid number for total employees");
            return;
        }

        if (departmentService.addDepartment(name, head, totalEmp, description)) {
            AlertUtil.showSuccess("Success", "Department added successfully!");
            clearForm(nameField, headCombo, totalField, descArea);
            refreshDepartmentTable();
        } else {
            AlertUtil.showError("Error", "Failed to add department");
        }
    }

    /**
     * Handle delete department
     */
    private void handleDeleteDepartment() {
        Department selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Warning", "Please select a department to delete");
            return;
        }

        if (AlertUtil.showConfirmation("Confirm Delete", "Are you sure you want to delete " + selected.getDepartmentName() + "?")) {
            if (departmentService.deleteDepartment(selected.getDepartmentId())) {
                AlertUtil.showSuccess("Success", "Department deleted successfully!");
                refreshDepartmentTable();
            } else {
                AlertUtil.showError("Error", "Failed to delete department");
            }
        }
    }

    /**
     * Get list of employee names
     */
    private List<String> getEmployeeNames() {
        List<String> names = new java.util.ArrayList<>();
        employeeService.getAllEmployees().forEach(emp -> names.add(emp.getEmployeeName()));
        return names;
    }

    /**
     * Clear form
     */
    private void clearForm(TextField nameField, ComboBox<String> headCombo, TextField totalField, TextArea descArea) {
        nameField.clear();
        headCombo.setValue(null);
        totalField.clear();
        descArea.clear();
    }
}
