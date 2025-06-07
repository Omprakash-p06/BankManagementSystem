package com.bank.model;

import org.json.JSONObject;
import java.sql.Date;

public class Customer {
    private String cif; // CHANGED from int to String
    private String fname;
    private String lname;
    private String contactno;
    private String gender;
    private Date birthdate;
    private String address;
    private String homebranch;
    private Date openingDate;
    private String identificationNo;
    private String passwordHash;

    // Getters and Setters
    public String getCif() { return cif; } // CHANGED return type
    public void setCif(String cif) { this.cif = cif; } // CHANGED parameter type

    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }

    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; }

    public String getContactno() { return contactno; }
    public void setContactno(String contactno) { this.contactno = contactno; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Date getBirthdate() { return birthdate; }
    public void setBirthdate(Date birthdate) { this.birthdate = birthdate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getHomebranch() { return homebranch; }
    public void setHomebranch(String homebranch) { this.homebranch = homebranch; }

    public Date getOpeningDate() { return openingDate; }
    public void setOpeningDate(Date openingDate) { this.openingDate = openingDate; }

    public String getIdentificationNo() { return identificationNo; }
    public void setIdentificationNo(String identificationNo) { this.identificationNo = identificationNo; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("cif", cif);
        json.put("fname", fname);
        json.put("lname", lname);
        json.put("identificationNo", identificationNo);
        json.put("contactno", contactno);
        // Add other fields as needed for JSON representation
        json.put("gender", gender);
        json.put("birthdate", birthdate != null ? birthdate.toString() : JSONObject.NULL);
        json.put("address", address);
        json.put("homebranch", homebranch);
        json.put("openingDate", openingDate != null ? openingDate.toString() : JSONObject.NULL);
        return json;
    }
}
