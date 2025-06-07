package com.bank.controller;

import com.bank.dao.TransactionDAO;
import com.bank.model.Employee; // Make sure Employee is imported
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Make sure HttpSession is imported
import java.io.IOException;

@WebServlet("/api/reject-transaction")
public class RejectTransactionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employee") == null) {
            response.sendRedirect("employee.html"); // Redirect to employee login
            return;
        }

        // transactionId is now String
        String transactionId = request.getParameter("id");

        if (transactionId == null || transactionId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Transaction ID is required");
            return;
        }

        try {
            new TransactionDAO().rejectTransaction(transactionId);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\": \"Transaction rejected successfully!\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Rejection failed: " + e.getMessage());
        }
    }
}
