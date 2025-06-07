package com.bank.controller;

import com.bank.dao.AccountDAO;
import com.bank.dao.CustomerDAO;
import com.bank.model.Customer;
import com.bank.model.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("customer") == null) {
            response.sendRedirect("login.html");
            return;
        }

        Customer customer = (Customer) session.getAttribute("customer");
        JSONObject jsonResponse = new JSONObject();

        try {
            // Fetch full customer details (important if session object is partial)
            CustomerDAO customerDAO = new CustomerDAO();
            // Assuming getCustomerByIdentificationNo is the reliable way to get customer details
            // The CIF in session might be the old int type, so re-fetching is safer.
            Customer fullCustomer = customerDAO.getCustomerByIdentificationNo(customer.getIdentificationNo());

            if (fullCustomer == null) {
                // This scenario should ideally not happen if login was successful, but handle defensively
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found after login.");
                return;
            }

            // Fetch accounts using the *String* CIF from the fullCustomer object
            AccountDAO accountDAO = new AccountDAO();
            List<Account> accounts = accountDAO.getAccountsByCIF(fullCustomer.getCif()); // Pass String CIF

            // Build JSON response
            jsonResponse.put("customer", fullCustomer.toJSON()); // Use toJSON method for customer details
            JSONArray accountsJson = new JSONArray();
            for (Account account : accounts) {
                accountsJson.put(account.toJSON()); // Use toJSON method for account details
            }
            jsonResponse.put("accounts", accountsJson);

            response.getWriter().write(jsonResponse.toString());

        } catch (SQLException e) {
            e.printStackTrace(); // Log the full stack trace for debugging
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) { // Catch any other unexpected exceptions
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + e.getMessage());
        }
    }
}
