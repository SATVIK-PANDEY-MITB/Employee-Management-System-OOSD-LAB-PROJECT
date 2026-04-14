package com.ems;

import com.ems.service.EmployeeService;
import com.ems.service.DepartmentService;
import com.ems.service.AttendanceService;
import com.ems.service.AuthService;
import com.ems.ui.controller.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.ems.util.AlertUtil;

/**
 * Main Application class - Employee Management System
 * Tab-based GUI using JavaFX
 */
public class EmployeeManagementApp extends Application {
    private EmployeeService employeeService;
    private DepartmentService departmentService;
    private AttendanceService attendanceService;
    private AuthService authService;
    private BorderPane root;
    private Timeline sessionMonitor;

    @Override
    public void init() throws Exception {
        super.init();
        // Initialize services with sample data
        employeeService = new EmployeeService();
        departmentService = new DepartmentService();
        attendanceService = new AttendanceService();
        authService = new AuthService();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(true);

        primaryStage.setTitle("Employee Management System");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(700);

        primaryStage.setOnCloseRequest(event -> {
            stopSessionMonitor();
            authService.logout();
            Platform.exit();
            System.exit(0);
        });

        // Create main border pane
        root = new BorderPane();

        showAuthView();

        // Create and set scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Show authentication view with Login and Register options.
     */
    private void showAuthView() {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));
        container.setStyle("-fx-background-color: linear-gradient(to bottom, #f7f9fc, #e9eef6);");

        Label titleLabel = new Label("Employee Management System");
        titleLabel.setFont(new Font("Arial", 30));
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label subtitleLabel = new Label("Login or register to continue");
        subtitleLabel.setFont(new Font("Arial", 14));

        Label defaultLoginLabel = new Label("Default admin login: admin / admin123");
        defaultLoginLabel.setStyle("-fx-text-fill: #666666;");

        TabPane authTabs = new TabPane();
        authTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        authTabs.setMaxWidth(420);

        Tab loginTab = new Tab("Login", createLoginView());
        Tab registerTab = new Tab("Register", createRegisterView());
        authTabs.getTabs().addAll(loginTab, registerTab);

        container.getChildren().addAll(titleLabel, subtitleLabel, defaultLoginLabel, authTabs);

        root.setTop(null);
        root.setCenter(container);
    }

    /**
     * Create login form.
     */
    private VBox createLoginView() {
        VBox loginBox = new VBox(12);
        loginBox.setPadding(new Insets(20));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-padding: 8 16;");
        loginButton.setOnAction(e -> {
            if (authService.login(usernameField.getText(), passwordField.getText())) {
                AlertUtil.showSuccess("Login Successful", "Welcome, " + authService.getCurrentUser() + "!");
                showMainAppView();
            } else {
                AlertUtil.showError("Login Failed", "Invalid username or password");
            }
        });

        loginBox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);
        return loginBox;
    }

    /**
     * Create registration form.
     */
    private VBox createRegisterView() {
        VBox registerBox = new VBox(12);
        registerBox.setPadding(new Insets(20));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose username");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Choose password");

        Label confirmLabel = new Label("Confirm Password:");
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Re-enter password");

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-padding: 8 16;");
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmField.getText();

            if (username == null || username.trim().isEmpty()) {
                AlertUtil.showError("Registration Failed", "Username is required");
                return;
            }
            if (password == null || password.trim().isEmpty()) {
                AlertUtil.showError("Registration Failed", "Password is required");
                return;
            }
            if (!password.equals(confirmPassword)) {
                AlertUtil.showError("Registration Failed", "Passwords do not match");
                return;
            }

            if (authService.register(username, password)) {
                AlertUtil.showSuccess("Registration Successful", "Account created. You can now login.");
                usernameField.clear();
                passwordField.clear();
                confirmField.clear();
            } else {
                AlertUtil.showError("Registration Failed", "Username already exists or input is invalid");
            }
        });

        registerBox.getChildren().addAll(
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                confirmLabel, confirmField,
                registerButton
        );
        return registerBox;
    }

    /**
     * Show the main application after successful login.
     */
    private void showMainAppView() {
        stopSessionMonitor();

        // Create top bar with logged-in user and logout action.
        HBox header = new HBox(10);
        header.setPadding(new Insets(10, 15, 10, 15));
        header.setStyle("-fx-background-color: #f2f2f2; -fx-border-color: #d8d8d8; -fx-border-width: 0 0 1 0;");

        Label userLabel = new Label("Logged in as: " + authService.getCurrentUser() + " (" + authService.getCurrentRole() + ")");
        userLabel.setFont(new Font("Arial", 13));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            if (AlertUtil.showConfirmation("Logout", "Do you want to logout?")) {
                authService.logout();
                showAuthView();
            }
        });

        header.getChildren().addAll(userLabel, spacer, logoutButton);

        // Create tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Dashboard Tab
        DashboardController dashboardController = new DashboardController(employeeService, departmentService, attendanceService);
        Tab dashboardTab = new Tab("Dashboard", dashboardController.getDashboardView());
        EmployeeManagerController employeeController = new EmployeeManagerController(employeeService);
        Tab employeeTab = new Tab("Employee Management", employeeController.getEmployeeManagementView());

        DepartmentManagerController departmentController = new DepartmentManagerController(departmentService, employeeService);
        Tab departmentTab = new Tab("Department Management", departmentController.getDepartmentManagementView());

        AttendanceManagerController attendanceController = new AttendanceManagerController(attendanceService, employeeService);
        Tab attendanceTab = new Tab("Attendance Management", attendanceController.getAttendanceManagementView());

        tabPane.getTabs().addAll(dashboardTab, employeeTab, departmentTab, attendanceTab);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == dashboardTab) {
                dashboardController.refreshDashboard();
            } else if (newTab == employeeTab) {
                employeeController.refreshView();
            } else if (newTab == attendanceTab) {
                attendanceController.refreshView();
            }
        });

        root.setTop(header);
        root.setCenter(tabPane);
        startSessionMonitor();
    }

    private void startSessionMonitor() {
        sessionMonitor = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            if (!authService.isSessionValid()) {
                AlertUtil.showWarning("Session Expired", "Your session has expired. Please login again.");
                showAuthView();
            }
        }));
        sessionMonitor.setCycleCount(Timeline.INDEFINITE);
        sessionMonitor.play();
    }

    private void stopSessionMonitor() {
        if (sessionMonitor != null) {
            sessionMonitor.stop();
            sessionMonitor = null;
        }
    }

    @Override
    public void stop() {
        stopSessionMonitor();
        if (authService != null) {
            authService.logout();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
