package com.bank.controller;

import com.bank.dao.EmployeeDAO;
import com.bank.model.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/employee-login")
public class EmployeeLoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Read JSON data from request
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        JSONObject json = new JSONObject(jsonBuilder.toString());
        // Changed to getString for PFNO
        String pfno = json.getString("pfno");
        String password = json.getString("password");

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            // Changed parameter type to String
            Employee employee = employeeDAO.getEmployeeByPFNO(pfno);

            if (employee != null && BCrypt.checkpw(password, employee.getPasswordHash())) {
                HttpSession session = request.getSession();
                session.setAttribute("employee", employee);
                // Send a 200 OK status for success instead of redirecting directly in servlet
                // Frontend JS will handle redirect after receiving OK status
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Login successful!\"}");
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid PFNO or password");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the actual exception for debugging
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Login failed: " + e.getMessage());
        }
    }
}
