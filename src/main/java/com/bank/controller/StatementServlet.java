package com.bank.controller;

import com.bank.dao.TransactionDAO;
import com.bank.model.Transaction;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject; // Keep this if using JSONObject in other methods or removing it below
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/statement")
public class StatementServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("customer") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login first");
            return;
        }

        // Changed to getString for accno
        String accno = request.getParameter("accno");

        // Basic validation for accno (can be enhanced)
        if (accno == null || accno.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account number is required.");
            return;
        }

        try {
            // Call getTransactionsByAccount with String accno
            List<Transaction> transactions = new TransactionDAO().getTransactionsByAccount(accno);
            JSONArray jsonArray = new JSONArray(); // Changed to jsonArray for consistency

            for (Transaction t : transactions) {
                jsonArray.put(t.toJSON());
            }
            response.getWriter().write(jsonArray.toString());

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error fetching statement: " + e.getMessage());
            e.printStackTrace(); // Log the error for debugging
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account number format: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
