package com.bank.dao;

import com.bank.model.Customer;
import com.bank.util.DatabaseConnector;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.UUID; // Import UUID for CIF generation

public class CustomerDAO {

    // Add customer with pre-hashed password from servlet
    public void addCustomer(Customer customer) throws SQLException {
        // --- FIX 1: Generate and set CIF before insertion ---
        // Since CIF is VARCHAR(20) PRIMARY KEY and not auto-increment,
        // it must be generated and provided by the application.
        // We'll use a UUID and truncate it to fit VARCHAR(20).
        String newCif = "CUST-" + UUID.randomUUID().toString().substring(0, 14); // Ensures it's < 20 chars
        customer.setCif(newCif); // Set the generated CIF to the customer object

        // --- FIX 2: Include all NOT NULL columns in the INSERT statement ---
        String sql = "INSERT INTO customer (CIF, fname, lname, contactno, identification_no, password_hash, " +
                "opening_date, gender, birthdate, address, homebranch) " + // Added gender, birthdate, address, homebranch
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getCif()); // Set the generated CIF (VARCHAR)
            stmt.setString(2, customer.getFname());
            stmt.setString(3, customer.getLname());
            stmt.setString(4, customer.getContactno());
            stmt.setString(5, customer.getIdentificationNo());
            stmt.setString(6, customer.getPasswordHash()); // Password already hashed by servlet
            stmt.setDate(7, customer.getOpeningDate());

            // --- FIX 2: Set values for newly added NOT NULL columns ---
            stmt.setString(8, customer.getGender());
            stmt.setDate(9, customer.getBirthdate());
            stmt.setString(10, customer.getAddress());
            stmt.setString(11, customer.getHomebranch());

            stmt.executeUpdate();
        }
    }

    // Get customer by ID
    public Customer getCustomerByIdentificationNo(String identificationNo) throws SQLException {
        String sql = "SELECT * FROM customer WHERE identification_no = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, identificationNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCustomerFromResultSet(rs);
            }
            return null;
        }
    }

    // Password reset methods
    public void storeResetToken(String identificationNo, String token, Timestamp expiry) throws SQLException {
        String sql = "UPDATE customer SET reset_token = ?, token_expiry = ? WHERE identification_no = ?"; // Changed WHERE to identification_no
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setTimestamp(2, expiry);
            stmt.setString(3, identificationNo);
            stmt.executeUpdate();
        }
    }

    public boolean isValidToken(String token) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customer WHERE reset_token = ? AND token_expiry > NOW()";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void updatePassword(String token, String newPassword) throws SQLException {
        String sql = "UPDATE customer SET password_hash = ?, reset_token = NULL, token_expiry = NULL " +
                "WHERE reset_token = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            stmt.setString(2, token);
            stmt.executeUpdate();
        }
    }

    // Helper method to map ResultSet to Customer object
    private Customer mapCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCif(rs.getString("CIF")); // Should be getString as CIF is VARCHAR
        customer.setFname(rs.getString("fname"));
        customer.setLname(rs.getString("lname"));
        customer.setContactno(rs.getString("contactno"));
        customer.setIdentificationNo(rs.getString("identification_no"));
        customer.setPasswordHash(rs.getString("password_hash"));
        customer.setGender(rs.getString("gender")); // Retrieve gender
        customer.setBirthdate(rs.getDate("birthdate")); // Retrieve birthdate
        customer.setAddress(rs.getString("address")); // Retrieve address
        customer.setHomebranch(rs.getString("homebranch")); // Retrieve homebranch
        customer.setOpeningDate(rs.getDate("opening_date")); // Retrieve openingDate
        // Note: reset_token and token_expiry are not retrieved here. Add if needed for Customer model.
        return customer;
    }
}
