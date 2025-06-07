package com.bank.controller;

import com.bank.dao.TransactionDAO;
import com.bank.model.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject; // Import for JSON response

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays; // Import for Arrays.asList

@WebServlet("/api/approve-transaction")
public class ApproveTransactionServlet extends HttpServlet {

    // Define roles that are allowed to approve transactions
    private static final String[] APPROVED_ROLES = {"CHECKER", "OPERATIONS MANAGER", "BRANCH HEAD", "BANK TELLER"}; // Added Bank Teller

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employee") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("message", "Unauthorized: Please login as an employee.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        Employee employee = (Employee) session.getAttribute("employee");

        // Check if the employee's designation is among the approved roles
        // FIX: Flexible role checking
        if (!Arrays.asList(APPROVED_ROLES).contains(employee.getDesignation().toUpperCase())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            jsonResponse.put("message", "Forbidden: Your designation (" + employee.getDesignation() + ") is not authorized to approve transactions.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        String transactionId = request.getParameter("id");
        String checkerId = employee.getPfno(); // Employee's PFNO is the checkerId

        if (transactionId == null || transactionId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("message", "Transaction ID is required.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            new TransactionDAO().approveTransaction(transactionId, checkerId);
            response.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.put("message", "Transaction approved successfully!");
            response.getWriter().write(jsonResponse.toString());
        } catch (SQLException e) {
            e.printStackTrace(); // Log the actual SQL exception for debugging
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("message", "Database error during approval: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace(); // Log any other unexpected exception
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("message", "Approval failed: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
}
