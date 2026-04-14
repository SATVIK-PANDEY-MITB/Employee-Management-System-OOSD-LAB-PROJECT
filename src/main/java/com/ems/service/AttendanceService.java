package com.ems.service;

import com.ems.model.Attendance;
import com.ems.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AttendanceService class for managing attendance records.
 */
public class AttendanceService {
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final AuditService auditService = new AuditService();

    public AttendanceService() {
        seedSampleData();
    }

    private void seedSampleData() {
        if (databaseManager.isTableEmpty("attendance")) {
            markAttendanceInternal(new Attendance(2001, 1001, "2026-04-07", "Present", ""));
            markAttendanceInternal(new Attendance(2002, 1002, "2026-04-07", "Present", ""));
            markAttendanceInternal(new Attendance(2003, 1003, "2026-04-07", "Leave", "Medical leave"));
        }
    }

    public boolean markAttendance(int employeeId, String date, String status, String remarks) {
        Attendance attendance = new Attendance(getNextAttendanceId(), employeeId, date, status, remarks);
        boolean saved = markAttendanceInternal(attendance);
        if (saved) {
            auditService.log("system", "MARK_ATTENDANCE", attendance.toString());
        }
        return saved;
    }

    public List<Attendance> getAllAttendance() {
        List<Attendance> attendanceRecords = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM attendance ORDER BY attendance_id")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    attendanceRecords.add(mapAttendance(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load attendance records", e);
        }
        return attendanceRecords;
    }

    public List<Attendance> getAttendanceByEmployeeId(int employeeId) {
        return getAllAttendance().stream().filter(attendance -> attendance.getEmployeeId() == employeeId).collect(Collectors.toList());
    }

    public List<Attendance> getAttendanceByDate(String date) {
        return getAllAttendance().stream().filter(attendance -> attendance.getDate().equals(date)).collect(Collectors.toList());
    }

    public boolean updateAttendance(int attendanceId, String status, String remarks) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE attendance SET status = ?, remarks = ? WHERE attendance_id = ?")) {
            statement.setString(1, status);
            statement.setString(2, remarks);
            statement.setInt(3, attendanceId);
            int updated = statement.executeUpdate();
            if (updated > 0) {
                auditService.log("system", "UPDATE_ATTENDANCE", String.valueOf(attendanceId));
            }
            return updated > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update attendance", e);
        }
    }

    public boolean deleteAttendance(int attendanceId) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM attendance WHERE attendance_id = ?")) {
            statement.setInt(1, attendanceId);
            int deleted = statement.executeUpdate();
            if (deleted > 0) {
                auditService.log("system", "DELETE_ATTENDANCE", String.valueOf(attendanceId));
            }
            return deleted > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete attendance record", e);
        }
    }

    public Map<String, Integer> getAttendanceStats(int employeeId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Present", 0);
        stats.put("Absent", 0);
        stats.put("Leave", 0);
        stats.put("Half Day", 0);

        getAttendanceByEmployeeId(employeeId).forEach(attendance -> stats.put(attendance.getStatus(), stats.getOrDefault(attendance.getStatus(), 0) + 1));
        return stats;
    }

    public Map<String, Integer> getMonthlyAttendanceTrend() {
        Map<String, Integer> trend = new HashMap<>();
        getAllAttendance().forEach(attendance -> {
            String month = YearMonth.parse(attendance.getDate().substring(0, 7)).toString();
            trend.put(month, trend.getOrDefault(month, 0) + 1);
        });
        return trend;
    }

    private int getNextAttendanceId() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COALESCE(MAX(attendance_id), 2000) + 1 FROM attendance")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 2001;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to calculate next attendance id", e);
        }
    }

    private boolean markAttendanceInternal(Attendance attendance) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO attendance(attendance_id, employee_id, attendance_date, status, remarks) VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, attendance.getAttendanceId());
            statement.setInt(2, attendance.getEmployeeId());
            statement.setString(3, attendance.getDate());
            statement.setString(4, attendance.getStatus());
            statement.setString(5, attendance.getRemarks());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save attendance", e);
        }
    }

    private Attendance mapAttendance(ResultSet resultSet) throws SQLException {
        return new Attendance(
                resultSet.getInt("attendance_id"),
                resultSet.getInt("employee_id"),
                resultSet.getString("attendance_date"),
                resultSet.getString("status"),
                resultSet.getString("remarks")
        );
    }
}
