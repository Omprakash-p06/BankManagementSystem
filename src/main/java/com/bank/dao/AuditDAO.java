package com.bank.dao;

import com.bank.model.AuditLog;
import com.bank.util.DatabaseConnector;

import java.sql.*;

public class AuditDAO {
    private static final String INSERT_LOG_SQL = "INSERT INTO audit_logs " +
            "(user_id, user_type, action, ip_address, status_code, user_agent, details) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public void logActivity(AuditLog log) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_LOG_SQL)) {
            stmt.setString(1, log.getUserId()); // Changed to setString
            stmt.setString(2, log.getUserType());
            stmt.setString(3, log.getAction());
            stmt.setString(4, log.getIpAddress());
            stmt.setInt(5, log.getStatusCode());
            stmt.setString(6, log.getUserAgent());
            stmt.setString(7, log.getDetails());
            stmt.executeUpdate();
        }
    }
}
