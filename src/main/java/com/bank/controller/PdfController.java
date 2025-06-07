package com.bank.controller;

import com.bank.dao.TransactionDAO;
import com.bank.model.Transaction;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class PdfController {

    private final TransactionDAO transactionDAO;

    @Autowired
    public PdfController(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    @GetMapping(value = "/statement", produces = "application/pdf")
    public void generatePdfStatement(@RequestParam("accno") String accountNumber, // Changed to String
                                     HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition",
                "attachment; filename=statement_" + accountNumber + ".pdf");
        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Header
            Paragraph header = new Paragraph("Account Statement - #" + accountNumber)
                    .setFontSize(18)
                    .setBold();
            document.add(header);

            // Transactions Table
            Table table = new Table(4);
            table.addHeaderCell("Date");
            table.addHeaderCell("Type");
            table.addHeaderCell("Amount");
            table.addHeaderCell("Status");

            // Call getTransactionsByAccount with String accountNumber
            List<Transaction> transactions = transactionDAO.getTransactionsByAccount(accountNumber);

            for (Transaction t : transactions) {
                // Ensure date format is readable
                String dateString = (t.getTransactionDate() != null) ?
                        t.getTransactionDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A";
                table.addCell(dateString);
                table.addCell(t.getTransactionType());
                table.addCell("₹" + String.format("%.2f", t.getAmount())); // Format amount
                table.addCell(t.getCheckerid() != null ?
                        "Approved (Emp#" + t.getCheckerid() + ")" : "Pending");
            }
            document.add(table);

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF: " + e.getMessage());
            e.printStackTrace(); // Log for debugging
        }
    }
}
