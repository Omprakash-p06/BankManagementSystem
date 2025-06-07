package com.bank.model;

import java.sql.Timestamp;
import java.util.UUID; // Import UUID for generating transferId

public class Transfer {
    private String transferId; // Changed to String as per common UUID practice and for flexibility
    private String fromAccno;  // Changed to String
    private String toAccno;    // Changed to String
    private double amount;
    private Timestamp transferDate;

    // Getters and Setters
    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getFromAccno() { // Changed return type
        return fromAccno;
    }

    public void setFromAccno(String fromAccno) { // Changed parameter type
        this.fromAccno = fromAccno;
    }

    public String getToAccno() { // Changed return type
        return toAccno;
    }

    public void setToAccno(String toAccno) { // Changed parameter type
        this.toAccno = toAccno;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Timestamp transferDate) {
        this.transferDate = transferDate;
    }
}
