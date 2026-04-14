package com.ems.service;

import com.ems.util.DatabaseManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

/**
 * AuthService provides hashed, role-aware authentication backed by H2.
 */
public class AuthService {
    private static final long SESSION_TIMEOUT_MILLIS = 30L * 60L * 1000L;
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final AuditService auditService = new AuditService();
    private String currentUser;
    private String currentRole;
    private long sessionExpiresAt;

    public AuthService() {
        seedAdminUser();
    }

    public boolean register(String username, String password) {
        return register(username, password, "USER");
    }

    public boolean register(String username, String password, String role) {
        if (isBlank(username) || isBlank(password)) {
            return false;
        }

        String normalizedUsername = normalize(username);
        if (userExists(normalizedUsername)) {
            return false;
        }

        String salt = generateSalt();
        String passwordHash = hashPassword(password, salt);

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users(username, password_hash, salt, role, last_login) VALUES (?, ?, ?, ?, 0)")) {
            statement.setString(1, normalizedUsername);
            statement.setString(2, passwordHash);
            statement.setString(3, salt);
            statement.setString(4, role == null ? "USER" : role.toUpperCase());
            statement.executeUpdate();
            auditService.log(normalizedUsername, "REGISTER", "User registered with role " + role);
            return true;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to register user", e);
        }
    }

    public boolean login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return false;
        }

        String normalizedUsername = normalize(username);
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT password_hash, salt, role FROM users WHERE username = ?")) {
            statement.setString(1, normalizedUsername);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password_hash");
                    String salt = resultSet.getString("salt");
                    if (storedHash.equals(hashPassword(password, salt))) {
                        currentUser = normalizedUsername;
                        currentRole = resultSet.getString("role");
                        sessionExpiresAt = System.currentTimeMillis() + SESSION_TIMEOUT_MILLIS;
                        updateLastLogin(normalizedUsername);
                        auditService.log(normalizedUsername, "LOGIN", "Successful login");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to login", e);
        }

        return false;
    }

    public boolean resetPassword(String username, String currentPassword, String newPassword) {
        if (isBlank(username) || isBlank(currentPassword) || isBlank(newPassword)) {
            return false;
        }

        String normalizedUsername = normalize(username);
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT password_hash, salt FROM users WHERE username = ?")) {
            statement.setString(1, normalizedUsername);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password_hash");
                    String salt = resultSet.getString("salt");
                    if (storedHash.equals(hashPassword(currentPassword, salt))) {
                        String newSalt = generateSalt();
                        String newHash = hashPassword(newPassword, newSalt);
                        try (PreparedStatement updateStatement = connection.prepareStatement(
                                "UPDATE users SET password_hash = ?, salt = ? WHERE username = ?")) {
                            updateStatement.setString(1, newHash);
                            updateStatement.setString(2, newSalt);
                            updateStatement.setString(3, normalizedUsername);
                            updateStatement.executeUpdate();
                            auditService.log(normalizedUsername, "PASSWORD_RESET", "Password reset successful");
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to reset password", e);
        }

        return false;
    }

    public void logout() {
        if (currentUser != null) {
            auditService.log(currentUser, "LOGOUT", "User logged out");
        }
        currentUser = null;
        currentRole = null;
        sessionExpiresAt = 0L;
    }

    public boolean isLoggedIn() {
        return currentUser != null && isSessionValid();
    }

    public boolean isSessionValid() {
        if (currentUser == null) {
            return false;
        }
        if (System.currentTimeMillis() > sessionExpiresAt) {
            logout();
            return false;
        }
        return true;
    }

    public String getCurrentUser() {
        return isSessionValid() ? currentUser : null;
    }

    public String getCurrentRole() {
        return isSessionValid() ? currentRole : null;
    }

    private void seedAdminUser() {
        if (!userExists("admin")) {
            register("admin", "admin123", "ADMIN");
        }
    }

    private boolean userExists(String username) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?")) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to inspect user", e);
        }
    }

    private void updateLastLogin(String username) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE users SET last_login = ? WHERE username = ?")) {
            statement.setLong(1, System.currentTimeMillis());
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private String generateSalt() {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalize(String value) {
        return value.trim().toLowerCase();
    }
}