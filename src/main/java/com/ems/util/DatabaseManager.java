package com.ems.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central H2 database setup and schema initialization.
 */
public class DatabaseManager {
    private static final String DEFAULT_JDBC_URL = "jdbc:h2:file:./data/emsdb;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private DatabaseManager() {
        initialize();
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        String jdbcUrl = System.getProperty("ems.db.url", DEFAULT_JDBC_URL);
        return DriverManager.getConnection(jdbcUrl, USER, PASSWORD);
    }

    private void initialize() {
        try {
            Path dataDir = Paths.get("data");
            Files.createDirectories(dataDir);
            try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS employees (" +
                        "employee_id INT PRIMARY KEY, " +
                        "employee_name VARCHAR(255) NOT NULL, " +
                        "designation VARCHAR(255) NOT NULL, " +
                        "salary DOUBLE NOT NULL, " +
                        "department VARCHAR(255) NOT NULL, " +
                        "phone_number VARCHAR(20) NOT NULL, " +
                        "email VARCHAR(255) NOT NULL, " +
                        "status VARCHAR(50) NOT NULL, " +
                        "joining_date VARCHAR(20) NOT NULL)");
                statement.execute("CREATE TABLE IF NOT EXISTS departments (" +
                        "department_id INT PRIMARY KEY, " +
                        "department_name VARCHAR(255) NOT NULL, " +
                        "head VARCHAR(255) NOT NULL, " +
                        "total_employees INT NOT NULL, " +
                        "description VARCHAR(1000))");
                statement.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                        "attendance_id INT PRIMARY KEY, " +
                        "employee_id INT NOT NULL, " +
                        "attendance_date VARCHAR(20) NOT NULL, " +
                        "status VARCHAR(50) NOT NULL, " +
                        "remarks VARCHAR(1000))");
                statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "username VARCHAR(255) PRIMARY KEY, " +
                        "password_hash VARCHAR(255) NOT NULL, " +
                        "salt VARCHAR(255) NOT NULL, " +
                        "role VARCHAR(50) NOT NULL, " +
                        "last_login BIGINT DEFAULT 0)");
                statement.execute("CREATE TABLE IF NOT EXISTS audit_log (" +
                        "id IDENTITY PRIMARY KEY, " +
                        "username VARCHAR(255), " +
                        "action VARCHAR(255) NOT NULL, " +
                        "details VARCHAR(2000), " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            }
        } catch (SQLException | java.io.IOException e) {
            throw new IllegalStateException("Failed to initialize database", e);
        }
    }

    public boolean isTableEmpty(String tableName) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to inspect table " + tableName, e);
        }
    }
}