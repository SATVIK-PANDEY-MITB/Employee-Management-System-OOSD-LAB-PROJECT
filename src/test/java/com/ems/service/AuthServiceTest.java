package com.ems.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {
    static {
        System.setProperty("ems.db.url", "jdbc:h2:mem:ems_test;DB_CLOSE_DELAY=-1");
    }

    private final AuthService authService = new AuthService();

    @BeforeAll
    static void prepare() {
        System.setProperty("ems.db.url", "jdbc:h2:mem:ems_test;DB_CLOSE_DELAY=-1");
    }

    @Test
    void logsInDefaultAdmin() {
        assertTrue(authService.login("admin", "admin123"));
        assertEquals("admin", authService.getCurrentUser());
        assertEquals("ADMIN", authService.getCurrentRole());
        authService.logout();
    }

    @Test
    void registersAndResetsPassword() {
        String username = "user_" + System.nanoTime();
        assertTrue(authService.register(username, "secret123"));
        assertTrue(authService.login(username, "secret123"));
        assertTrue(authService.resetPassword(username, "secret123", "newsecret123"));
        authService.logout();
        assertTrue(authService.login(username, "newsecret123"));
        assertNotNull(authService.getCurrentUser());
        assertFalse(authService.login(username, "secret123"));
    }
}