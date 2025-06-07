package com.bank.model;

import java.sql.Date;
import org.json.JSONObject; // Import JSONObject for toJSON method

public class Account {
    private String accno; // Changed to String as per DB schema and ERD
    private String cif;   // CHANGED from int to String
    private String acctype;
    private Date openingDate;
    private double balance;
    private double interestrate;
    private String facility;

    // Getters and Setters
    public String getAccno() {
        return accno;
    }
    public void setAccno(String accno) {
        this.accno = accno;
    }

    public String getCif() { // CHANGED return type
        return cif;
    }
    public void setCif(String cif) { // CHANGED parameter type
        this.cif = cif;
    }

    public String getAcctype() {
        return acctype;
    }
    public void setAcctype(String acctype) {
        this.acctype = acctype;
    }

    public Date getOpeningDate() {
        return openingDate;
    }
    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getInterestrate() {
        return interestrate;
    }
    public void setInterestrate(double interestrate) {
        this.interestrate = interestrate;
    }

    public String getFacility() {
        return facility;
    }
    public void setFacility(String facility) {
        this.facility = facility;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("accno", accno);
        json.put("CIF", cif); // Use CIF as per DB schema
        json.put("acctype", acctype);
        json.put("openingDate", openingDate != null ? openingDate.toString() : JSONObject.NULL);
        json.put("balance", balance);
        json.put("interestrate", interestrate);
        json.put("facility", facility);
        return json;
    }
}
