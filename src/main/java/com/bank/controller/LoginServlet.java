package com.bank.controller;

import com.bank.dao.CustomerDAO;
import com.bank.model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.json.JSONObject; // Import for JSON handling
import java.io.BufferedReader; // Import for reading request body
import java.io.IOException;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json"); // Set content type for JSON response
        JSONObject jsonResponse = new JSONObject(); // For structured error messages

        // Read JSON data from request body
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        String identificationNo = null;
        String password = null;

        try {
            JSONObject json = new JSONObject(jsonBuilder.toString());
            identificationNo = json.getString("identificationNo");
            password = json.getString("password");

            // Fetch customer from database
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getCustomerByIdentificationNo(identificationNo);

            // Verify password using BCrypt.checkpw
            if (customer != null && BCrypt.checkpw(password, customer.getPasswordHash())) {
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("customer", customer);

                response.setStatus(HttpServletResponse.SC_OK); // Send 200 OK
                jsonResponse.put("message", "Login successful!"); // Send a success message
                response.getWriter().write(jsonResponse.toString());
            } else {
                // Send 401 Unauthorized for invalid credentials
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("message", "Invalid User ID or password.");
                response.getWriter().write(jsonResponse.toString());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the actual exception for debugging
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("message", "Login failed due to server error: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
}
