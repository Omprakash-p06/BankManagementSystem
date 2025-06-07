package com.bank.controller;

import com.bank.model.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/api/employee-dashboard")
public class EmployeeDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("employee") == null) {
            response.sendRedirect("employee.html");
            return;
        }

        Employee employee = (Employee) session.getAttribute("employee");
        JSONObject json = new JSONObject();
        json.put("empname", employee.getEmpname());
        json.put("designation", employee.getDesignation());
        json.put("joiningDate", employee.getJoiningDate());
        json.put("address", employee.getAddress());

        response.getWriter().write(json.toString());
    }
}