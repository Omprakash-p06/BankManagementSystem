package com.bank.filter;

import com.bank.dao.AuditDAO;
import com.bank.model.AuditLog;
import com.bank.model.Customer;
import com.bank.model.Employee;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebFilter("/*")
public class AuditFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuditFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        AuditLog log = new AuditLog();
        String requestURI = httpRequest.getRequestURI();
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        try {
            // Capture request details before processing the request
            // getUserId now returns String
            log.setUserId(getUserId(httpRequest));
            log.setUserType(getUserType(httpRequest));
            log.setAction(httpRequest.getMethod() + " " + requestURI);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            log.setTimestamp(new Timestamp(System.currentTimeMillis()));
            log.setDetails("Request initiated for " + requestURI);

            chain.doFilter(request, response);

            // Post-processing: capture status code and update details if needed
            log.setStatusCode(httpResponse.getStatus());
            log.setDetails(log.getDetails() + " - Status: " + httpResponse.getStatus());

        } catch (Exception e) {
            log.setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.setDetails(log.getDetails() + " - Error: " + e.getMessage());
            logger.error("Request processing error for {}: {}", requestURI, e.getMessage(), e);
            throw e;
        } finally {
            try {
                new AuditDAO().logActivity(log);
                logger.debug("Audit logged for {} - Status {}", log.getAction(), log.getStatusCode());
            } catch (SQLException e) {
                logger.error("Failed to save audit log for {}: {}", log.getAction(), e.getMessage(), e);
            }
        }
    }

    // Changed return type to String
    private String getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object user = session.getAttribute("customer");
            if (user instanceof Customer) {
                // Customer.cif is int, convert to String for AuditLog
                return String.valueOf(((Customer) user).getCif());
            }
            user = session.getAttribute("employee");
            if (user instanceof Employee) {
                // Employee.pfno is String, use directly
                return ((Employee) user).getPfno();
            }
        }
        return "GUEST"; // Or a default ID for guests, ensure it matches VARCHAR(50)
    }

    private String getUserType(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute("customer") != null) return "CUSTOMER";
            if (session.getAttribute("employee") != null) return "EMPLOYEE";
        }
        return "GUEST";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
