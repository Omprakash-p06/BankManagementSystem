package com.bank.controller;

import com.bank.dao.AccountDAO;
import com.bank.dao.TransferDAO;
import com.bank.model.Transfer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.SQLException;
import java.io.BufferedReader; // Added import for BufferedReader

@WebServlet("/api/transfer")
public class TransferServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();
        HttpSession session = request.getSession(false);

        // Ensure a customer is logged in
        if (session == null || session.getAttribute("customer") == null) {
            // Instead of redirecting directly, send an unauthorized error
            // The frontend JS will handle the redirect based on this status
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("error", "Please login first to perform transfers.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            // Parse JSON request
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) { // Use try-with-resources for reader
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            JSONObject json = new JSONObject(sb.toString());

            // Get account numbers as Strings
            String fromAccno = json.getString("fromAccno");
            String toAccno = json.getString("toAccno");
            double amount = json.getDouble("amount");

            // Validate amount
            if (amount <= 0) {
                jsonResponse.put("error", "Transfer amount must be positive.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Validate accounts and balance
            AccountDAO accountDAO = new AccountDAO();
            double fromBalance = accountDAO.getBalance(fromAccno); // Pass String accno

            if (fromBalance == -1) { // -1 might indicate account not found, or handle specifically
                jsonResponse.put("error", "Source account not found or invalid.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            if (fromBalance < amount) {
                jsonResponse.put("error", "Insufficient funds in source account.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Check if toAccno exists before transferring, optional but good practice
            double toBalanceCheck = accountDAO.getBalance(toAccno);
            if (toBalanceCheck == -1) {
                jsonResponse.put("error", "Destination account not found or invalid.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(jsonResponse.toString());
                return;
            }


            // Perform transfer
            // Pass String accnos to transferFunds
            accountDAO.transferFunds(fromAccno, toAccno, amount);

            // Record transfer
            Transfer transfer = new Transfer();
            transfer.setFromAccno(fromAccno);
            transfer.setToAccno(toAccno);
            transfer.setAmount(amount);
            // transferId and transferDate will be handled in TransferDAO (e.g., UUID and NOW())
            new TransferDAO().recordTransfer(transfer);

            jsonResponse.put("message", "Transfer successful!");
            response.setStatus(HttpServletResponse.SC_OK); // Explicitly set success status
            response.getWriter().write(jsonResponse.toString());

        } catch (SQLException e) {
            jsonResponse.put("error", "Database error during transfer: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(jsonResponse.toString());
            e.printStackTrace(); // Log the exception for debugging
        } catch (NumberFormatException e) {
            jsonResponse.put("error", "Invalid amount or account number format: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(jsonResponse.toString());
            e.printStackTrace();
        } catch (Exception e) { // Catch any other unexpected exceptions
            jsonResponse.put("error", "An unexpected error occurred during transfer: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(jsonResponse.toString());
            e.printStackTrace();
        }
    }
}
