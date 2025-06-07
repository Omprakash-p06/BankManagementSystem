package com.bank;

import com.bank.dao.CustomerDAO;
import com.bank.model.Customer;
import com.bank.util.InterestScheduler;

import java.sql.Date;

public class Main {
    public static void main(String[] args) {
        Customer customer = new Customer();
        customer.setFname("John");
        customer.setLname("Doe");
        customer.setContactno("123-456-7890");
        customer.setGender("MALE");
        customer.setBirthdate(Date.valueOf("1990-05-15"));
        customer.setAddress("123 Main St");
        customer.setHomebranch("Downtown Branch");
        customer.setOpeningDate(new Date(System.currentTimeMillis())); // Current date
        customer.setIdentificationNo("ID12345");

        try {
            new CustomerDAO().addCustomer(customer);
            System.out.println("Customer added to the database!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // In your application startup code
        InterestScheduler.start();
    }
}