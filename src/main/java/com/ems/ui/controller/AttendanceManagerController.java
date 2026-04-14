package com.ems.ui.controller;

import com.ems.model.Attendance;
import com.ems.model.Employee;
import com.ems.service.ExportService;
import com.ems.service.AttendanceService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Attendance Manager Controller for managing attendance records
 */
public class AttendanceManagerController {
    private AttendanceService attendanceService;
    private EmployeeService employeeService;
    private SearchService searchService = new SearchService();
    private ExportService exportService = new ExportService();
    private TableView<Attendance> attendanceTable;
    private ComboBox<String> employeeCombo;

    public AttendanceManagerController(AttendanceService attendanceService, EmployeeService employeeService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }

    /**
     * Create and return attendance management view
     */
    public VBox getAttendanceManagementView() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(15));

        // Title
        Label titleLabel = new Label("Attendance Management");
        titleLabel.setFont(new Font("Arial", 24));
        titleLabel.setStyle("-fx-font-weight: bold;");

        VBox searchSection = createSearchSection();

        // Form Section
        VBox formSection = createAttendanceForm();

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
        searchField.setPromptText("Search attendance by employee id, date, or status");
        searchField.setPrefWidth(360);

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> handleSearch(searchField.getText()));

        Button clearButton = new Button("Clear Search");
        clearButton.setOnAction(e -> {
            searchField.clear();
            refreshAttendanceTable();
        });

        Button exportCsvButton = new Button("Export CSV");
        exportCsvButton.setOnAction(e -> handleExport("csv"));

        Button exportPdfButton = new Button("Export PDF");
        exportPdfButton.setOnAction(e -> handleExport("pdf"));

        Button editButton = new Button("Edit Selected");
        editButton.setOnAction(e -> handleEditSelectedAttendance());

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> handleDeleteSelectedAttendance());

        row.getChildren().addAll(searchField, searchButton, clearButton, exportCsvButton, exportPdfButton, editButton, deleteButton);
        searchBox.getChildren().add(row);
        return searchBox;
    }

    /**
     * Create attendance form
     */
    private VBox createAttendanceForm() {
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-border-style: solid; -fx-border-width: 1; -fx-border-color: #cccccc; -fx-padding: 15; -fx-background-color: #f9f9f9;");

        Label formTitle = new Label("Mark Attendance");
        formTitle.setFont(new Font("Arial", 16));
        formTitle.setStyle("-fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Employee
        Label empLabel = new Label("Select Employee:");
        employeeCombo = new ComboBox<>();
        refreshEmployeeOptions();
        employeeCombo.setPrefWidth(250);

        // Date
        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());

        // Attendance Status
        Label statusLabel = new Label("Attendance Status:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList("Present", "Absent", "Leave", "Half Day"));
        statusCombo.setPrefWidth(250);

        // Remarks
        Label remarksLabel = new Label("Remarks:");
        TextArea remarksArea = new TextArea();
        remarksArea.setPromptText("Enter any remarks (optional)");
        remarksArea.setPrefRowCount(3);
        remarksArea.setWrapText(true);

        grid.add(empLabel, 0, 0);
        grid.add(employeeCombo, 1, 0);
        grid.add(dateLabel, 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(statusLabel, 0, 2);
        grid.add(statusCombo, 1, 2);
        grid.add(remarksLabel, 0, 3);
        grid.add(remarksArea, 1, 3);

        HBox formButtonBox = new HBox(10);
        formButtonBox.setPadding(new Insets(10, 0, 0, 0));

        Button markButton = new Button("Mark Attendance");
        markButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        markButton.setOnAction(e -> handleMarkAttendance(employeeCombo, datePicker, statusCombo, remarksArea));

        Button clearButton = new Button("Clear Form");
        clearButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        clearButton.setOnAction(e -> {
            employeeCombo.setValue(null);
            datePicker.setValue(LocalDate.now());
            statusCombo.setValue(null);
            remarksArea.clear();
        });

        formButtonBox.getChildren().addAll(markButton, clearButton);

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
        refreshButton.setOnAction(e -> refreshAttendanceTable());

        Button viewStatsButton = new Button("View Statistics");
        viewStatsButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        viewStatsButton.setOnAction(e -> handleViewStatistics());

        actionBox.getChildren().addAll(refreshButton, viewStatsButton);
        return actionBox;
    }

    /**
     * Create table section
     */
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));

        Label tableTitle = new Label("Attendance Records");
        tableTitle.setFont(new Font("Arial", 16));
        tableTitle.setStyle("-fx-font-weight: bold;");

        attendanceTable = new TableView<>();
        attendanceTable.setPrefHeight(350);

        // Create columns
        TableColumn<Attendance, Integer> idColumn = new TableColumn<>("Rec ID");
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getAttendanceId()));

        TableColumn<Attendance, Integer> empIdColumn = new TableColumn<>("Employee ID");
        empIdColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEmployeeId()));

        TableColumn<Attendance, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));

        TableColumn<Attendance, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        TableColumn<Attendance, String> remarksColumn = new TableColumn<>("Remarks");
        remarksColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRemarks()));

        attendanceTable.getColumns().addAll(idColumn, empIdColumn, dateColumn, statusColumn, remarksColumn);

        refreshAttendanceTable();

        tableBox.getChildren().addAll(tableTitle, attendanceTable);
        return tableBox;
    }

    /**
     * Refresh attendance table
     */
    private void refreshAttendanceTable() {
        List<Attendance> records = attendanceService.getAllAttendance();
        ObservableList<Attendance> data = FXCollections.observableArrayList(records);
        attendanceTable.setItems(data);
    }

    private void handleSearch(String query) {
        List<Attendance> results = searchService.searchAttendance(attendanceService.getAllAttendance(), query);
        attendanceTable.setItems(FXCollections.observableArrayList(results));
    }

    private void handleExport(String format) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            List<Attendance> attendanceToExport = attendanceService.getAllAttendance();
            if (attendanceToExport.isEmpty()) {
                AlertUtil.showWarning("Export", "No attendance records available to export.");
                return;
            }

            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName("attendance_" + timestamp + ("pdf".equalsIgnoreCase(format) ? ".pdf" : ".csv"));
            File initialDir = new File("exports");
            if (!initialDir.exists()) {
                initialDir.mkdirs();
            }
            chooser.setInitialDirectory(initialDir);

            if ("pdf".equalsIgnoreCase(format)) {
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
                File selectedFile = chooser.showSaveDialog(attendanceTable.getScene().getWindow());
                if (selectedFile == null) {
                    return;
                }
                List<String> lines = attendanceToExport.stream()
                        .map(att -> String.format("Attendance ID:%d | Employee ID:%d | Date:%s | Status:%s | Remarks:%s",
                                att.getAttendanceId(), att.getEmployeeId(), att.getDate(), att.getStatus(), att.getRemarks()))
                        .collect(Collectors.toList());
                java.nio.file.Path target = selectedFile.toPath();
                exportService.exportTextToPdf("Attendance Report", lines, target);
                AlertUtil.showSuccess("Export Complete", "Attendance PDF exported to " + target.toAbsolutePath());
            } else {
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
                File selectedFile = chooser.showSaveDialog(attendanceTable.getScene().getWindow());
                if (selectedFile == null) {
                    return;
                }
                java.nio.file.Path target = selectedFile.toPath();
                exportService.exportAttendanceToCsv(attendanceToExport, target);
                AlertUtil.showSuccess("Export Complete", "Attendance CSV exported to " + target.toAbsolutePath());
            }
        } catch (IOException exception) {
            AlertUtil.showError("Export Error", exception.getMessage());
        } catch (Exception exception) {
            AlertUtil.showError("Export Error", "Failed to export data: " + exception.getMessage());
        }
    }

    private void handleEditSelectedAttendance() {
        Attendance selected = attendanceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Warning", "Please select an attendance record to edit");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Attendance");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField statusField = new TextField(selected.getStatus());
        TextArea remarksField = new TextArea(selected.getRemarks());

        VBox content = new VBox(8,
                new Label("Status"), statusField,
                new Label("Remarks"), remarksField);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (attendanceService.updateAttendance(selected.getAttendanceId(), statusField.getText(), remarksField.getText())) {
                    refreshAttendanceTable();
                    AlertUtil.showSuccess("Success", "Attendance updated successfully!");
                } else {
                    AlertUtil.showError("Error", "Failed to update attendance");
                }
            }
        });
    }

    private void handleDeleteSelectedAttendance() {
        Attendance selected = attendanceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("Warning", "Please select an attendance record to delete");
            return;
        }
        if (AlertUtil.showConfirmation("Confirm Delete", "Are you sure you want to delete this attendance record?")) {
            if (attendanceService.deleteAttendance(selected.getAttendanceId())) {
                refreshAttendanceTable();
                AlertUtil.showSuccess("Success", "Attendance deleted successfully!");
            } else {
                AlertUtil.showError("Error", "Failed to delete attendance record");
            }
        }
    }

    /**
     * Handle mark attendance
     */
    private void handleMarkAttendance(ComboBox<String> employeeCombo, DatePicker datePicker, 
                                     ComboBox<String> statusCombo, TextArea remarksArea) {
        String selectedEmployee = employeeCombo.getValue();
        LocalDate date = datePicker.getValue();
        String status = statusCombo.getValue();
        String remarks = remarksArea.getText();

        if (ValidationUtil.isEmpty(selectedEmployee)) {
            AlertUtil.showError("Validation Error", "Please select an employee");
            return;
        }
        if (date == null) {
            AlertUtil.showError("Validation Error", "Please select a date");
            return;
        }
        if (ValidationUtil.isEmpty(status)) {
            AlertUtil.showError("Validation Error", "Please select attendance status");
            return;
        }

        Integer employeeId = extractEmployeeId(selectedEmployee);
        if (employeeId == null) {
            AlertUtil.showError("Validation Error", "Please select a valid employee from Employee Management list");
            return;
        }

        Employee emp = employeeService.getEmployeeById(employeeId);

        if (emp == null) {
            AlertUtil.showError("Error", "Employee not found");
            return;
        }

        if (attendanceService.markAttendance(emp.getEmployeeId(), date.toString(), status, remarks)) {
            AlertUtil.showSuccess("Success", "Attendance marked successfully!");
            employeeCombo.setValue(null);
            datePicker.setValue(LocalDate.now());
            statusCombo.setValue(null);
            remarksArea.clear();
            refreshAttendanceTable();
        } else {
            AlertUtil.showError("Error", "Failed to mark attendance");
        }
    }

    /**
     * Handle view statistics
     */
    private void handleViewStatistics() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Attendance Statistics");
        dialog.setHeaderText("Select Employee to View Statistics");

        ComboBox<String> empCombo = new ComboBox<>();
        empCombo.setItems(FXCollections.observableArrayList(getEmployeeOptions()));

        TextArea statsArea = new TextArea();
        statsArea.setEditable(false);
        statsArea.setPrefRowCount(10);
        statsArea.setWrapText(true);

        Button viewButton = new Button("View");
        viewButton.setOnAction(e -> {
            String selected = empCombo.getValue();
            if (selected != null) {
                Integer employeeId = extractEmployeeId(selected);
                Employee emp = employeeId == null ? null : employeeService.getEmployeeById(employeeId);

                if (emp != null) {
                    Map<String, Integer> stats = attendanceService.getAttendanceStats(emp.getEmployeeId());
                    StringBuilder statsText = new StringBuilder();
                    statsText.append("Attendance Statistics for ").append(emp.getEmployeeName()).append("\n\n");
                    stats.forEach((key, value) -> statsText.append(key).append(": ").append(value).append("\n"));
                    statsArea.setText(statsText.toString());
                }
            }
        });

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.getChildren().addAll(
                new Label("Select Employee:"),
                empCombo,
                viewButton,
                new Label("Statistics:"),
                statsArea
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * Refresh employee dropdown options using current Employee Management data.
     */
    private void refreshEmployeeOptions() {
        if (employeeCombo != null) {
            employeeCombo.setItems(FXCollections.observableArrayList(getEmployeeOptions()));
        }
    }

    /**
     * Public refresh hook for tab navigation events.
     */
    public void refreshView() {
        refreshEmployeeOptions();
        refreshAttendanceTable();
    }

    /**
     * Build employee options in a unique and stable format.
     */
    private List<String> getEmployeeOptions() {
        List<String> options = new java.util.ArrayList<>();
        employeeService.getAllEmployees().forEach(emp ->
                options.add(emp.getEmployeeId() + " - " + emp.getEmployeeName()));
        return options;
    }

    private Integer extractEmployeeId(String employeeOption) {
        if (employeeOption == null) {
            return null;
        }
        int separatorIndex = employeeOption.indexOf(" - ");
        if (separatorIndex <= 0) {
            return null;
        }
        try {
            return Integer.parseInt(employeeOption.substring(0, separatorIndex).trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
