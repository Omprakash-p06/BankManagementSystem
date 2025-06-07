package com.bank.dao;

import com.bank.model.Employee;
import com.bank.util.DatabaseConnector;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // Add employee with hashed password, including new fields
    public void addEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO employee (PFNO, empname, designation, address, contactno, gender, birthdate, type, joiningdate, password_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // Added PFNO to insert statement
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getPfno()); // Set PFNO as String
            stmt.setString(2, employee.getEmpname());
            stmt.setString(3, employee.getDesignation());
            stmt.setString(4, employee.getAddress());
            stmt.setString(5, employee.getContactno());
            stmt.setString(6, employee.getGender());
            stmt.setDate(7, employee.getBirthdate());
            stmt.setString(8, employee.getType());
            stmt.setDate(9, employee.getJoiningDate());
            stmt.setString(10, employee.getPasswordHash()); // Password should already be hashed by servlet
            stmt.executeUpdate();
        }
    }

    // Get employee by PFNO - changed parameter type to String
    public Employee getEmployeeByPFNO(String pfno) throws SQLException {
        String sql = "SELECT PFNO, empname, designation, address, contactno, gender, birthdate, type, joiningdate, password_hash, reset_token, token_expiry FROM employee WHERE PFNO = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pfno); // Set PFNO as String
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapEmployeeFromResultSet(rs);
            }
            return null;
        }
    }

    // Password reset methods for Employee (similar to CustomerDAO)
    public void storeResetToken(String empname, String token, Timestamp expiry) throws SQLException {
        String sql = "UPDATE employee SET reset_token = ?, token_expiry = ? WHERE empname = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setTimestamp(2, expiry);
            stmt.setString(3, empname);
            stmt.executeUpdate();
        }
    }

    public boolean isValidToken(String token) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employee WHERE reset_token = ? AND token_expiry > NOW()";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void updatePassword(String token, String newPassword) throws SQLException {
        String sql = "UPDATE employee SET password_hash = ?, reset_token = NULL, token_expiry = NULL " +
                "WHERE reset_token = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            stmt.setString(2, token);
            stmt.executeUpdate();
        }
    }

    // Helper method to map ResultSet to Employee object - changed PFNO to String
    private Employee mapEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setPfno(rs.getString("PFNO")); // Get PFNO as String
        employee.setEmpname(rs.getString("empname"));
        employee.setDesignation(rs.getString("designation"));
        employee.setAddress(rs.getString("address"));
        employee.setContactno(rs.getString("contactno"));
        employee.setGender(rs.getString("gender"));
        employee.setBirthdate(rs.getDate("birthdate"));
        employee.setType(rs.getString("type"));
        employee.setJoiningDate(rs.getDate("joiningdate"));
        employee.setPasswordHash(rs.getString("password_hash"));
        employee.setResetToken(rs.getString("reset_token"));
        employee.setTokenExpiry(rs.getTimestamp("token_expiry"));
        return employee;
    }
}
