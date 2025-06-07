package com.bank.controller;

import com.bank.dao.AccountDAO;
import com.bank.model.Account;
import com.bank.model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/create-account")
public class CreateAccountServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            jsonResponse.put("message", "Unauthorized: Please login to create an account.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        Customer customer = (Customer) session.getAttribute("customer");

        // Read JSON data from request body
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        JSONObject requestJson = new JSONObject(jsonBuilder.toString());
        String accountType = requestJson.getString("accountType");
        double initialDeposit = requestJson.getDouble("initialDeposit");

        // Basic validation
        if (initialDeposit <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("message", "Initial deposit must be a positive amount.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            Account newAccount = new Account();
            newAccount.setCif(customer.getCif()); // Associate with logged-in customer's CIF
            newAccount.setAcctype(accountType);
            newAccount.setBalance(initialDeposit);
            // Interest rate and facility will be set in AccountDAO or can be passed from frontend
            // For simplicity, let AccountDAO handle defaults for now.

            AccountDAO accountDAO = new AccountDAO();
            Account createdAccount = accountDAO.addAccount(newAccount);

            response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
            jsonResponse.put("message", "Account created successfully!");
            jsonResponse.put("account", createdAccount.toJSON()); // Return the new account details
            response.getWriter().write(jsonResponse.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("message", "Database error: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("message", "Failed to create account: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
}
