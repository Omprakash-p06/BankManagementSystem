package com.bank.controller;

import com.bank.dao.TransactionDAO;
import com.bank.model.Employee; // Import Employee
import com.bank.model.Customer; // Import Customer
import com.bank.model.Transaction;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/transaction")
public class TransactionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json"); // Ensure JSON response
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);
        String makerId = null;
        String userType = "UNKNOWN";

        // Determine if customer or employee is logged in to get the makerId
        if (session != null) {
            Customer customer = (Customer) session.getAttribute("customer");
            Employee employee = (Employee) session.getAttribute("employee");

            if (customer != null) {
                makerId = customer.getCif(); // Get customer's CIF as makerId (now String)
                userType = "CUSTOMER";
            } else if (employee != null) {
                makerId = employee.getPfno(); // Get employee's PFNO as makerId (String)
                userType = "EMPLOYEE";
            }
        }

        // If no valid user session (customer or employee) is found, deny access
        if (makerId == null || makerId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("message", "Unauthorized: Please login as a customer or employee to make transactions.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        // Parse JSON request
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        try {
            JSONObject json = new JSONObject(jsonBuilder.toString());

            String accno = json.getString("accno");
            double amount = json.getDouble("amount");
            String type = json.getString("type");

            // Validate amount
            if (amount <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("message", "Amount must be positive.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Create transaction
            Transaction transaction = new Transaction();
            transaction.setAccno(accno);
            transaction.setAmount(amount);
            transaction.setTransactionType(type);

            TransactionDAO transactionDAO = new TransactionDAO();
            transactionDAO.createTransaction(transaction, makerId); // Pass the determined makerId

            response.setStatus(HttpServletResponse.SC_CREATED);
            jsonResponse.put("message", "Transaction created successfully and sent for approval!");
            response.getWriter().write(jsonResponse.toString());

        } catch (Exception e) {
            e.printStackTrace(); // Log the actual exception for debugging
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("message", "Transaction failed: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
}
