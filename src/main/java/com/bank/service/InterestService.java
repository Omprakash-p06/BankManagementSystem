package com.bank.service;

import com.bank.dao.AccountDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class InterestService {

    public void calculateMonthlyInterest() throws SQLException {
        AccountDAO accountDAO = new AccountDAO();
        LocalDate today = LocalDate.now();

        // Run on the last day of the month
        if (today.getDayOfMonth() == today.lengthOfMonth()) {
            accountDAO.applyInterestToAllAccounts();
        }
    }
}