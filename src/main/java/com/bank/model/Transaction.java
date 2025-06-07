package com.bank.model;

import org.json.JSONObject;
import java.sql.Timestamp;

public class Transaction {
    private String transactionid; // Changed to String as per DB schema
    private String accno;         // Changed to String as per DB schema (if accno is VARCHAR)
    private double amount;
    private String transactionType;
    private Timestamp transactionDate;
    private String makerid;   // Changed to String to match DB VARCHAR(20)
    private String checkerid; // Changed to String to match DB VARCHAR(20)

    // Getters/Setters
    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getAccno() { // Changed to String
        return accno;
    }

    public void setAccno(String accno) { // Changed to String
        this.accno = accno;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getMakerid() { // Changed to String
        return makerid;
    }

    public void setMakerid(String makerid) { // Changed to String
        this.makerid = makerid;
    }

    public String getCheckerid() { // Changed to String
        return checkerid;
    }

    public void setCheckerid(String checkerid) { // Changed to String
        this.checkerid = checkerid;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("transactionid", transactionid);
        json.put("accno", accno);
        json.put("amount", amount);
        json.put("transactiontype", transactionType);
        json.put("transactionDate", transactionDate != null ? transactionDate.toString() : JSONObject.NULL);
        // Include makerid and checkerid if needed for frontend display
        json.put("makerid", makerid);
        json.put("checkerid", checkerid != null ? checkerid : JSONObject.NULL);
        return json;
    }
}
