package com.bank.controller;

import com.bank.dao.TransactionDAO;
import com.bank.model.Transaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/pending-transactions")
public class PendingTransactionsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try {
            List<Transaction> pendingTransactions = new TransactionDAO().getPendingTransactions();
            JSONArray jsonArray = new JSONArray();
            for (Transaction t : pendingTransactions) {
                jsonArray.put(t.toJSON());
            }
            response.getWriter().write(jsonArray.toString());
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}