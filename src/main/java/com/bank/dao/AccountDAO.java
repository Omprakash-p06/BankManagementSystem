package com.bank.dao;

import com.bank.model.Account;
import com.bank.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Import UUID for generating account numbers

public class AccountDAO {

    public Account addAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (accno, CIF, acctype, opening_date, balance, interestrate, facility) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Generate a unique account number as a String (already present)
        String newAccno = UUID.randomUUID().toString();
        account.setAccno(newAccno);

        // Set opening date to current date (already present)
        account.setOpeningDate(new Date(System.currentTimeMillis()));

        // Default interest rate (e.g., for SAVINGS) and facility (e.g., NONE, OVERDRAFT) (already present)
        if (account.getAcctype() == null || account.getAcctype().isEmpty()) {
            account.setAcctype("SAVINGS"); // Default to SAVINGS if not provided
        }
        if ("SAVINGS".equalsIgnoreCase(account.getAcctype())) {
            account.setInterestrate(0.03); // Example interest rate for savings
        } else if ("CURRENT".equalsIgnoreCase(account.getAcctype())) {
            account.setInterestrate(0.00); // No interest for current accounts
        } else {
            account.setInterestrate(0.01); // Generic default
        }

        if (account.getFacility() == null || account.getFacility().isEmpty()) {
            account.setFacility("NONE"); // Default facility
        }

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccno());
            stmt.setString(2, account.getCif()); // Changed from setInt to setString
            stmt.setString(3, account.getAcctype());
            stmt.setDate(4, account.getOpeningDate());
            stmt.setDouble(5, account.getBalance());
            stmt.setDouble(6, account.getInterestrate());
            stmt.setString(7, account.getFacility());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                return account;
            } else {
                throw new SQLException("Creating account failed, no rows affected.");
            }
        }
    }

    public List<Account> getAccountsByCIF(String cif) throws SQLException { // CHANGED parameter type to String
        String sql = "SELECT * FROM accounts WHERE CIF = ?";
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cif); // CHANGED from setInt to setString
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Account account = new Account();
                account.setAccno(rs.getString("accno"));
                account.setCif(rs.getString("CIF")); // CHANGED to getString
                account.setAcctype(rs.getString("acctype"));
                account.setOpeningDate(rs.getDate("opening_date"));
                account.setBalance(rs.getDouble("balance"));
                account.setInterestrate(rs.getDouble("interestrate"));
                account.setFacility(rs.getString("facility"));
                accounts.add(account);
            }
        }
        return accounts;
    }

    public double getBalance(String accno) throws SQLException {
        String sql = "SELECT balance FROM accounts WHERE accno = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accno);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble("balance") : -1;
        }
    }

    public double updateBalance(String accno, double amount) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE accno = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setString(2, accno);
            stmt.executeUpdate();
            return getBalance(accno);
        }
    }

    public void transferFunds(String fromAccno, String toAccno, double amount) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false);
            updateBalance(fromAccno, -amount, conn);
            updateBalance(toAccno, amount, conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateBalance(String accno, double amount, Connection conn) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE accno = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setString(2, accno);
            stmt.executeUpdate();
        }
    }

    public void applyInterestToAllAccounts() throws SQLException {
        String sql = "UPDATE accounts SET balance = balance * (1 + interestrate/100) " +
                "WHERE acctype = 'SAVINGS'";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}
