package com.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    // Simple GET endpoint for testing
    @GetMapping("/accounts")
    public String getAccounts() {
        return "Accounts endpoint works!";
    }

    // Simple POST endpoint for testing
    @PostMapping("/transfer")
    public String transferMoney(@RequestBody TransferRequest request) {
        return "Transfer successful: " + request.toString();
    }

    // Inner class to represent the transfer request body
    static class TransferRequest {
        private String fromAccount;
        private String toAccount;
        private double amount;

        // Getters and setters (required for JSON parsing)
        public String getFromAccount() { return fromAccount; }
        public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }

        public String getToAccount() { return toAccount; }
        public void setToAccount(String toAccount) { this.toAccount = toAccount; }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        @Override
        public String toString() {
            return "From: " + fromAccount + ", To: " + toAccount + ", Amount: " + amount;
        }
    }
}