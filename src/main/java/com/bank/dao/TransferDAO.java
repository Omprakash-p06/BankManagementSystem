package com.bank.dao;

import com.bank.model.Transfer;
import com.bank.util.DatabaseConnector;

import java.sql.*;
import java.util.UUID; // Import UUID for generating transferId

public class TransferDAO {

    public void recordTransfer(Transfer transfer) throws SQLException {
        // Generate a unique transfer ID
        // A standard UUID is 36 characters. If DB column for transfer_id is shorter (e.g., VARCHAR(20)),
        // we need to truncate it. Assuming VARCHAR(20) as a common practice for shorter IDs.
        // Prepend "XFR-" (for Transfer) and take the first 16 characters of the UUID to ensure it fits VARCHAR(20)
        String transferId = "XFR-" + UUID.randomUUID().toString().substring(0, 15);
        transfer.setTransferId(transferId); // Set the generated ID to the Transfer object

        String sql = "INSERT INTO transfers (transfer_id, from_accno, to_accno, amount, transfer_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transfer.getTransferId()); // Set transferId as String
            stmt.setString(2, transfer.getFromAccno()); // Changed to setString
            stmt.setString(3, transfer.getToAccno());   // Changed to setString
            stmt.setDouble(4, transfer.getAmount());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Set current timestamp
            stmt.executeUpdate();
        }
    }
}
