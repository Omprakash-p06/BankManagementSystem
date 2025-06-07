package com.bank.dao;

import com.bank.model.Transaction;
import com.bank.util.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Added for UUID generation for transactionid
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDAO {

    public void createTransaction(Transaction transaction, String makerId) throws SQLException { // makerId parameter is now String
        // FIX: Changed "transactiondate" to "transaction_date" to match database schema
        String sql = "INSERT INTO transactions (transactionid, accno, amount, transactiontype, transaction_date, makerid) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Generate a unique transaction ID (VARCHAR in DB)
            String transactionId = UUID.randomUUID().toString();
            stmt.setString(1, transactionId); // Set transactionid as String
            stmt.setString(2, transaction.getAccno()); // Set accno as String
            stmt.setDouble(3, transaction.getAmount());
            stmt.setString(4, transaction.getTransactionType());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Set current timestamp
            stmt.setString(6, makerId); // Set makerId as String
            stmt.executeUpdate();
        }
    }

    public List<Transaction> getPendingTransactions() throws SQLException {
        String sql = "SELECT * FROM transactions WHERE checkerid IS NULL";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionid(rs.getString("transactionid")); // Get as String
                t.setAccno(rs.getString("accno")); // Get as String
                t.setAmount(rs.getDouble("amount"));
                t.setTransactionType(rs.getString("transactiontype"));
                t.setMakerid(rs.getString("makerid")); // Get as String
                t.setCheckerid(rs.getString("checkerid")); // Get as String (can be null)
                t.setTransactionDate(rs.getTimestamp("transaction_date")); // Fixed column name
                transactions.add(t);
            }
        }
        return transactions;
    }

    public void approveTransaction(String transactionId, String checkerId) throws SQLException { // transactionId and checkerId are now String
        Connection conn = null;
        try {
            conn = DatabaseConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction

            Transaction transaction = getTransactionById(transactionId, conn); // Pass String transactionId
            AccountDAO accountDAO = new AccountDAO(); // Note: AccountDAO might need customer.getCif() if not already String
            if ("WITHDRAWAL".equalsIgnoreCase(transaction.getTransactionType())) {
                accountDAO.updateBalance(transaction.getAccno(), -transaction.getAmount(), conn); // Pass String accno
            } else {
                accountDAO.updateBalance(transaction.getAccno(), transaction.getAmount(), conn); // Pass String accno
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE transactions SET checkerid = ? WHERE transactionid = ?")) {
                stmt.setString(1, checkerId); // Set checkerId as String
                stmt.setString(2, transactionId); // Set transactionId as String
                stmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
            if (conn != null) {
                try {
                    conn.close(); // Ensure connection is closed
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void rejectTransaction(String transactionId) throws SQLException { // transactionId is now String
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM transactions WHERE transactionid = ?")) {
            stmt.setString(1, transactionId); // Set transactionId as String
            stmt.executeUpdate();
        }
    }

    // Helper method to get transaction by ID - transactionId is now String
    private Transaction getTransactionById(String transactionId, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM transactions WHERE transactionid = ?")) {
            stmt.setString(1, transactionId); // Set transactionId as String
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionid(rs.getString("transactionid")); // Get as String
                t.setAccno(rs.getString("accno")); // Get as String
                t.setAmount(rs.getDouble("amount"));
                t.setTransactionType(rs.getString("transactiontype"));
                t.setMakerid(rs.getString("makerid")); // Get as String
                t.setCheckerid(rs.getString("checkerid")); // Get as String
                t.setTransactionDate(rs.getTimestamp("transaction_date")); // Fixed column name
                return t;
            }
            throw new SQLException("Transaction not found with ID: " + transactionId);
        }
    }

    public List<Transaction> getTransactionsByAccount(String accno) throws SQLException { // accno is now String
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE accno = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accno); // Set accno as String
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionid(rs.getString("transactionid")); // Get as String
                t.setAccno(rs.getString("accno")); // Get as String
                t.setAmount(rs.getDouble("amount"));
                t.setTransactionType(rs.getString("transactiontype"));
                t.setTransactionDate(rs.getTimestamp("transaction_date")); // Fixed column name
                t.setMakerid(rs.getString("makerid")); // Get as String
                t.setCheckerid(rs.getString("checkerid")); // Get as String
                transactions.add(t);
            }
        }
        return transactions;
    }
}
