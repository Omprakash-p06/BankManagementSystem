package com.bank.controller;

import com.bank.dao.EmployeeDAO;
import com.bank.model.Employee;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.json.JSONObject;

@WebServlet("/api/register-employee")
public class RegisterEmployeeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        JSONObject json = new JSONObject(request.getReader().readLine());
        Employee employee = new Employee();
        employee.setEmpname(json.getString("empname"));
        employee.setDesignation(json.getString("designation"));
        employee.setPasswordHash(BCrypt.hashpw(json.getString("password"), BCrypt.gensalt()));
        employee.setAddress(json.getString("address"));

        try {
            new EmployeeDAO().addEmployee(employee);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Registration failed");
        }
    }
}