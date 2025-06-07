package com.bank.controller;

import com.bank.dao.CustomerDAO;
import com.bank.util.EmailService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet("/api/password-reset")
public class PasswordResetServlet extends HttpServlet {

    private final EmailService emailService = new EmailService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        JSONObject json = new JSONObject(jsonBuilder.toString());
        String email = json.getString("email");

        try {
            CustomerDAO dao = new CustomerDAO();
            String token = UUID.randomUUID().toString();
            Timestamp expiry = Timestamp.valueOf(LocalDateTime.now().plusHours(1)); // Fixed datetime conversion

            dao.storeResetToken(email, token, expiry);

            // Use HTTPS for security
            String resetLink = "https://yourdomain.com/reset-password?token=" + token;
            emailService.sendEmail(email, "Password Reset",
                    "Click here to reset your password: " + resetLink);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Password reset failed: " + e.getMessage());
        }
    }
}