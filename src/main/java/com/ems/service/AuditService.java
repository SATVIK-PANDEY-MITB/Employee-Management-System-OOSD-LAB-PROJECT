package com.ems.service;

import com.ems.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Writes user actions to the audit log table.
 */
public class AuditService {
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    public void log(String username, String action, String details) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO audit_log(username, action, details) VALUES (?, ?, ?)") ) {
            statement.setString(1, username);
            statement.setString(2, action);
            statement.setString(3, details);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to write audit log", e);
        }
    }
}