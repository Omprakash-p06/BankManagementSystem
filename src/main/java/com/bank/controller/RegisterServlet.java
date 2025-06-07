package com.bank.controller;

import com.bank.dao.CustomerDAO;
import com.bank.model.Customer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import org.json.JSONObject;
import java.sql.Date; // Import java.sql.Date

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json"); // Set content type for JSON response
        JSONObject jsonResponse = new JSONObject(); // For structured error messages

        // Read the JSON data from the request body
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        try {
            // Parse the JSON data
            JSONObject json = new JSONObject(jsonBuilder.toString());

            String fname = json.getString("fname");
            String lname = json.getString("lname");
            String identificationNo = json.getString("identificationNo");
            String contactno = json.getString("contactno");
            String gender = json.getString("gender"); // Extract gender
            String birthdateStr = json.getString("birthdate"); // Extract birthdate string
            String address = json.getString("address"); // Extract address
            String homebranch = json.getString("homebranch"); // Extract homebranch
            String password = json.getString("password");

            // Hash the password ONCE here in the servlet
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

            // Create the Customer object and set all required fields
            Customer customer = new Customer();
            customer.setFname(fname);
            customer.setLname(lname);
            customer.setIdentificationNo(identificationNo);
            customer.setContactno(contactno);
            customer.setPasswordHash(passwordHash);

            // Set the additional NOT NULL fields
            customer.setGender(gender);

            // Parse birthdate string to java.sql.Date
            // Ensure the frontend sends date in "YYYY-MM-DD" format
            customer.setBirthdate(Date.valueOf(birthdateStr));

            customer.setAddress(address);
            customer.setHomebranch(homebranch);
            customer.setOpeningDate(new Date(System.currentTimeMillis())); // Set current date as opening date

            new CustomerDAO().addCustomer(customer); // DAO now expects an already hashed password and all fields

            response.setStatus(HttpServletResponse.SC_CREATED); // 201: Created
            jsonResponse.put("message", "Registration successful!");
            response.getWriter().write(jsonResponse.toString());

        } catch (SQLException e) {
            e.printStackTrace(); // Log the actual exception for debugging
            // Check for duplicate entry error specifically (e.g., for identification_no or contactno)
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                jsonResponse.put("message", "A user with this ID number or contact number already exists. Please use unique details.");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
                jsonResponse.put("message", "Database error during registration: " + e.getMessage());
            }
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) { // Catch any other unexpected errors (e.g., JSON parsing, date parsing)
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("message", "Registration failed due to server error: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
}
